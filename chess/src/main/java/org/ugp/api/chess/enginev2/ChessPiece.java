package org.ugp.api.chess.enginev2;

import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

public class ChessPiece implements SelfSerializable, Cloneable
{
	public static final int BLACK = 0, WHITE = 1;
	public static final String KING = "k", QUEEN = "q", BISHOP = "b", ROOK = "r", KNIGHT = "n", PAWN = "p";
	
	public static final int[][] STRAIGHT_DIRS = {
		{1, 0},
		{-1, 0},
		{0, 1},
		{0, -1}
	};
	
	public static final int[][] DIAGONAL_DIRS = {
		{1, 1},
		{-1, -1},
		{-1, 1},
		{1, -1}
	};
	
	protected int x, y;
	protected int color;
	protected int moveCount;
	protected SimpleChessEngine myBoard;
	protected String type;
	protected int[][] movmentMetrix; 

	public ChessPiece(SimpleChessEngine board, String type, int color, int x, int y) {
		this.myBoard = board;
		this.color = color;
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + "[" + getType() + ", " + (getColor() == BLACK ? "Black" : "White") + ", " + getX() + ", " + getY() + "]";
	}
	
	@Override
	public Object[] serialize() {
		Scope s = new Scope();
		s.put("color", getColor());
		s.put("type", getType());
		return new Object[] {s};
	}
	
	@Override
	public final ChessPiece clone() {
		return clone(myBoard);
	}
	
	public final ChessPiece clone(SimpleChessEngine board) {
		try {
			ChessPiece clone = (ChessPiece) super.clone();
			clone.myBoard = board;
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int[][] generateMovmentMetrix(int[][] newEmptyMetrix, boolean checkIfKingInCheck) {
		for (int px = 0; px < newEmptyMetrix[0].length; px++) {
			for (int py = 0; py < newEmptyMetrix.length; py++) {
				if (canMoveTo(px, py, checkIfKingInCheck))
					newEmptyMetrix[py][px]++;
			}
		}
		
		return movmentMetrix = newEmptyMetrix;
	}
	
	public boolean canMoveTo(int x, int y, boolean checkIfKingInCheck) {
		if (!myBoard.isInBounds(x, y))
			return false;
		
		ChessPiece piece = myBoard.get(x, y);
		if (piece == null || piece.getColor() != getColor()) 
		{
			if (checkIfKingInCheck)
			{
				SimpleChessEngine cloneBoard = myBoard.clone();
				if ((piece = cloneBoard.get(getX(), getY())) == null)
					return true;
				if (piece.moveTo(x, y) == null)
					return true;
				return !cloneBoard.isCheck(cloneBoard.onTurn);
			}
			return true;
		}
		return false;
	}
	
	public ChessPiece moveToIfCan(int x, int y) {	
		if (canMoveTo(x, y, true))
		{
			ChessPiece piece = moveTo(x, y);
			if (piece != null)
			{
				piece.movmentMetrix = null;
				return piece;
			}
		}
		return null;
	}
	
	public ChessPiece moveTo(int x, int y) {	
		if (getX() == x && getY() == y)
			return null;
		
		ChessPiece piece = myBoard.put(this, x, y);
		if (piece != null)
		{
			myBoard.remove(this);
			this.moveCount++;
			this.x = x;
			this.y = y;
		}
		return piece;
	}
	
	public void kill() {
		myBoard.remove(this);
	}

	public ChessPiece getNeighbour(int offX, int offY) 
	{
		return myBoard.get(x + offX, y + offY);
	}
	
	public int distanceTo(int x, int y) {
		int distX = Math.abs(x - getX()), distY = Math.abs(y - getY());
		return Math.max(distX, distY);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getColor() {
		return color;
	}
	
	public int getMoveCount() {
		return moveCount;
	}
	
	public String getType() {
		return type;
	}
	
	public SimpleChessEngine getMyBoard() {
		return myBoard;
	}
	
	public int[][] getMovmentMetrix() {
		return movmentMetrix;
	}
	
	public static int[][] generateMovmentFromOffsets(ChessPiece piece, int[][] newEmptyMetrix, int[][] offsets, boolean checkIfKingInCheck) {
		int myX = piece.getX(), myY = piece.getY();
		for (int[] offset : offsets) {
			if (piece.canMoveTo(myX + offset[0], myY + offset[1], checkIfKingInCheck))
				newEmptyMetrix[myY + offset[1]][myX + offset[0]]++;
		}
		
		return newEmptyMetrix;
	}
	
	public static int[][] generateMovmentForRange(ChessPiece piece, int[][] newEmptyMetrix, int range, boolean checkIfKingInCheck) {
		int myX = piece.getX(), myY = piece.getY();
		for (int px = myX - range; px <= myX + range; px++) {
			for (int py = myY - range; py <= myY + range; py++) {
				if (piece.canMoveTo(px, py, checkIfKingInCheck))
					newEmptyMetrix[py][px]++;
			}
		}
		
		return newEmptyMetrix;
	}
	
	public static int[][] generateMovmentFromDirs(ChessPiece piece, int[][] newEmptyMetrix, int[][] directions, boolean checkIfKingInCheck) {
		int w = newEmptyMetrix[0].length, h = newEmptyMetrix.length;
		for (int[] dirs : directions) {
			int xDir = dirs[0], yDir = dirs[1];
			for (int x = piece.getX() + xDir, y = piece.getY() + yDir; x >= 0 && y >= 0 && x < w && y < h; x += xDir, y += yDir) {
				if (piece.canMoveTo(x, y, checkIfKingInCheck))
					newEmptyMetrix[y][x]++;
				if (!piece.getMyBoard().isEmpty(x, y))
					break;
			}
		}
		
		return newEmptyMetrix;
	}

	public static boolean canMoveStraight(ChessPiece piece, int toX, int toY) {
		int myX = piece.getX(), myY = piece.getY();
		if (toY == myY) //Horizontal
		{
			if (toX > myX) 
			{
				for (int i = 1; i < toX - myX; i++) //Go right
					if (piece.getNeighbour(i, 0) != null)
						return false;
			}
			else
			{
				for (int i = 1; i < myX - toX; i++) //Go left
					if (piece.getNeighbour(-i, 0) != null)
						return false;
			}
			return true;
		}
		
		if (toX == myX) //Vertical
		{
			if (toY > myY)
			{
				for (int i = 1; i < toY - myY; i++) //Go down
					if (piece.getNeighbour(0, i) != null)
						return false;
			}
			else
			{
				for (int i = 1; i < myY - toY; i++) //Go top
					if (piece.getNeighbour(0, -i) != null)
						return false;
			}
			return true;
		}
		
		return false;
	}
	
	public static boolean canMoveDiagonal(ChessPiece piece, int toX, int toY) {
		int myX = piece.getX(), myY = piece.getY();
		if (Math.abs(toX - myX) == Math.abs(toY - myY)) //Diagonal
		{
			if (toX > myX) //Go right
			{
				int dist = toX - myX;
				if (toY > myY)
				{
					for (int i = 1; i < dist; i++) //Go down
						if (piece.getNeighbour(i, i) != null)
							return false;
				}
				else
				{
					for (int i = 1; i < dist; i++) //Go up
						if (piece.getNeighbour(i, -i) != null)
							return false;
				}
			}
			else //Go left
			{
				int dist = myX - toX;
				if (toY > myY)
				{
					for (int i = 1; i < dist; i++) //Go down
						if (piece.getNeighbour(-i, i) != null)
							return false;
				}
				else
				{
					for (int i = 1; i < dist; i++) //Go up
						if (piece.getNeighbour(-i, -i) != null)
							return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
