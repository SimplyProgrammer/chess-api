package org.ugp.api.chess.enginev2;

import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

public class SimpleChessEngine implements SelfSerializable
{
	protected ChessPiece[][] pieces;
	protected int onTurn;
	
	public SimpleChessEngine(int width, int height, int onTurn) {
		pieces = new ChessPiece[width][height];
		this.onTurn = onTurn;
	}
	
	@Override
	public Object[] serialize() {
		Scope s = new Scope();
		s.put("pieces", pieces);
		return new Object[] {s};
	}
	
	@Override
	public String toString() {
		String str = "";
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				ChessPiece piece = pieces[x][y];
				str += piece == null ? '#' : piece;
			}
			str += "\n";
		}
		return str;
	}
	
	public int[][] getMovmentMetrix(int px, int py) {
		int[][] metrix = new int[getWidth()][getHeight()];
		
		ChessPiece piece = get(px, py);
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (piece.canMoveTo(x, y))
					metrix[x][y]++;
			}
		}
		
		return metrix;
	}
	
	public boolean isOnTurn(int color) {
		return onTurn == color;
	}
	
	public boolean isOnTurn(int x, int y) {
		ChessPiece piece = get(x, y);
		return piece != null && isOnTurn(piece.color);
	}
	
	public boolean isEmpty(int x, int y) {
		return isInBounds(x, y) && pieces[x][y] == null;
	}
	
	public void move(int fromX, int fromY, int toX, int toY) {
		if (isInBounds(toX, toY) && isOnTurn(fromX, fromY)) {
			remove(fromX, fromY).moveTo(toX, toY);
		}
	}
	
	public boolean isInBounds(int x, int y) {
		return x < getWidth() && x >= 0 &&
			   y < getHeight() && y >= 0;
	}
	
	public ChessPiece get(int x, int y) {
		return isInBounds(x, y) ? pieces[x][y] : null; 
	}
	
	public ChessPiece remove(int x, int y) {
		if (isInBounds(x, y))
		{
			ChessPiece piece = get(x, y);
			pieces[x][y] = null;
			return piece;
		}
		return null;
	}

	public void remove(ChessPiece removePiece) {
		pieces[removePiece.getX()][removePiece.getY()] = null;
	}

	public ChessPiece put(String type, int color, int x, int y) {
		if (isInBounds(x, y))
		{
			if (type == "k")
				return pieces[x][y] = new King(this, color, x, y);
			if (type == "q")
				return pieces[x][y] = new Queen(this, color, x, y);
			if (type == "n")
				return pieces[x][y] = new Knight(this, color, x, y);
			if (type == "b")
				return pieces[x][y] = new Bishop(this, color, x, y);
			if (type == "r")
				return pieces[x][y] = new Rook(this, color, x, y);
			if (type == "p")
				return pieces[x][y] = new Pawn(this, color, x, y);
			return pieces[x][y] = new ChessPiece(this, type, color, x, y);
		}
		return null;
	}
	
	public void put(ChessPiece chessPiece, int x, int y) {
		if (isInBounds(x, y))
			pieces[x][y] = chessPiece;
	}
	
	public int getWidth() {
		return pieces[0].length;
	}
	
	public int getHeight() {
		return pieces.length;
	}
	
	public ChessPiece[][] getPieces() {
		return pieces;
	}
}
