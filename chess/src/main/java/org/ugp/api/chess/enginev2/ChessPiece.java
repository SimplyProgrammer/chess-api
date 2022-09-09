package org.ugp.api.chess.enginev2;

import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

public class ChessPiece implements SelfSerializable
{
	public static final int BLACK = 0, WHITE = 1;
	public static final String KING = "k", QUEEN = "q", BISHOP = "b", ROOK = "r", KNIGHT = "n", PAWN = "p";
	
	protected int x, y;
	protected int color;
	protected int moveCount;
	protected SimpleChessEngine myBoard;
	protected String type;

	public ChessPiece(SimpleChessEngine board, String type, int color, int x, int y) {
		this.myBoard = board;
		this.color = color;
		this.x = x;
		this.y = y;
		this.type = type;
		
		myBoard.put(this, x, y);
	}
	
	@Override
	public String toString() {
		return type;
	}
	
	@Override
	public Object[] serialize() {
		Scope s = new Scope();
		s.put("color", color);
		s.put("type", type);
		return new Object[] {s};
	}
	
	public boolean canMoveTo(int x, int y) {
		if (!myBoard.isInBounds(x, y))
			return false;
		
		ChessPiece piece = myBoard.get(x, y);
		if (piece == null || piece.getColor() != getColor()) 
			return true;
		return false;
	}
	
	public ChessPiece moveToIfCan(int x, int y) {	
		if (canMoveTo(x, y))
			return moveTo(x, y);
		return null;
	}
	
	public ChessPiece moveTo(int x, int y) {	
		if (getX() == x && getY() == y)
			return null;
		this.x = x;
		this.y = y;
		
		ChessPiece target = myBoard.get(x, y);
		if (target != null) 
			kill();
		
		myBoard.put(this, x, y);
		moveCount++;
		return this;
	}
	
	public void kill() {
		myBoard.remove(this);
		x = y = -1;
	}
	
	public ChessPiece getNeighbour(int offX, int offY) 
	{
		return myBoard.get(x + offX, y + offY);
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

	protected boolean canMoveStraight(int x, int y) {
		int myX = getX(), myY = getY();
		if (y == myY) //Horizontal
		{
			if (x > myX) 
			{
				for (int i = 1; i < x - myX; i++) //Go right
					if (getNeighbour(i, 0) != null)
						return false;
			}
			else
			{
				for (int i = 1; i < myX - x; i++) //Go left
					if (getNeighbour(-i, 0) != null)
						return false;
			}
			return true;
		}
		
		if (x == myX) //Vertical
		{
			if (y > myY)
			{
				for (int i = 1; i < y - myY; i++) //Go down
					if (getNeighbour(0, i) != null)
						return false;
			}
			else
			{
				for (int i = 1; i < myY - y; i++) //Go top
					if (getNeighbour(0, -i) != null)
						return false;
			}
			return true;
		}
		
		return false;
	}
	
	protected boolean canMoveDiagonal(int x, int y) {
		int myX = getX(), myY = getY();
		if (Math.abs(x - getX()) == Math.abs(y - getY())) //Diagonal
		{
			if (x > myX) //Go right
			{
				int dist = x - myX;
				if (y > myY)
				{
					for (int i = 1; i < dist; i++) //Go down
						if (getNeighbour(i, i) != null)
							return false;
				}
				else
				{
					for (int i = 1; i < dist; i++) //Go up
						if (getNeighbour(i, -i) != null)
							return false;
				}
			}
			else //Go left
			{
				int dist = myX - x;
				if (y > myY)
				{
					for (int i = 1; i < dist; i++) //Go down
						if (getNeighbour(-i, i) != null)
							return false;
				}
				else
				{
					for (int i = 1; i < dist; i++) //Go up
						if (getNeighbour(-i, -i) != null)
							return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	protected int distanceTo(int x, int y) {
		int distX = Math.abs(x - getX()), distY = Math.abs(y - getY());
		return Math.max(distX, distY);
	}
}
