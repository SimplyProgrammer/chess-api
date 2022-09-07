package org.ugp.api.chess.enginev2;

import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

public class SimpleChessEngine implements SelfSerializable
{
	protected ChessPiece[][] pieces;
	
	public SimpleChessEngine(int width, int height) {
		pieces = new ChessPiece[width][height];
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
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				ChessPiece piece = pieces[x][y];
				str += piece == null ? '#' : piece;
			}
			str += "\n";
		}
		return str;
	}
	
	public int[][] getMovmentMetrixOf(int px, int py) {
		int[][] metrix = new int[getWidth()][getHeight()];
		
		ChessPiece piece = pieceAt(px, py);
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (piece.canMoveTo(x, y))
					metrix[x][y]++;
			}
		}
		
		return metrix;
	}
	
	public boolean isEmpty(int x, int y) {
		return isInBounds(x, y) && pieces[x][y] == null;
	}
	
	public boolean isInBounds(int x, int y) {
		return x < getWidth() && x >= 0 &&
			   y < getHeight() && y >= 0;
	}
	
	public ChessPiece pieceAt(int x, int y) {
		return isInBounds(x, y) ? pieces[x][y] : null; 
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
