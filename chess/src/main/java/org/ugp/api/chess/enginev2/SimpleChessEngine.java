package org.ugp.api.chess.enginev2;

import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

public class SimpleChessEngine implements SelfSerializable
{
	protected ChessPiece[][] pieces;
	protected int onTurn;
	
	public SimpleChessEngine(int width, int height, int onTurn) {
		pieces = new ChessPiece[height][width];
		this.onTurn = onTurn;
	}
	
	@Override
	public Object[] serialize() {
		Scope s = new Scope();
		s.put("w", getWidth());
		s.put("h", getHeight());
		s.put("onTurn", onTurn);
		s.put("pieces", pieces);
		return new Object[] {s};
	}
	
	@Override
	public String toString() {
		String str = "";
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				ChessPiece piece = get(x, y);
				str += piece == null ? '#' : piece;
			}
			str += "\n";
		}
		return str;
	}
	
	public int[][] getMovmentMetrix(int px, int py) {
		int[][] metrix = new int[getWidth()][getHeight()];
		
		ChessPiece piece = get(px, py);
		System.out.println(px + " " + py + " " + piece);
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
		return get(x, y) == null;
	}
	
	public boolean move(int fromX, int fromY, int toX, int toY) {
		if (isOnTurn(fromX, fromY)) {
			remove(fromX, fromY).moveTo(toX, toY);
			onTurn ^= 1;
			return true;
		}
		return false;
	}
	
	public boolean isInBounds(int x, int y) {
		return x < getWidth() && x >= 0 &&
			   y < getHeight() && y >= 0;
	}
	
	public ChessPiece get(int x, int y) {
		return isInBounds(x, y) ? pieces[y][x] : null; 
	}
	
	public ChessPiece remove(int x, int y) {
		if (isInBounds(x, y))
		{
			ChessPiece piece = get(x, y);
			pieces[y][x] = null;
			return piece;
		}
		return null;
	}

	public void remove(ChessPiece removePiece) {
		pieces[removePiece.getY()][removePiece.getX()] = null;
	}

	public ChessPiece put(String type, int color, int x, int y) {
		if (type == "k")
			return put(new King(this, color, x, y), x, y);
		if (type == "q")
			return put(new Queen(this, color, x, y), x, y);
		if (type == "n")
			return put(new Knight(this, color, x, y), x, y);
		if (type == "b")
			return put(new Bishop(this, color, x, y), x, y);
		if (type == "r")
			return put(new Rook(this, color, x, y), x, y);
		if (type == "p")
			return put(new Pawn(this, color, x, y), x, y);
		return put(new ChessPiece(this, type, color, x, y), x, y);
	}
	
	public ChessPiece put(ChessPiece chessPiece, int x, int y) {
		if (isInBounds(x, y))
			return pieces[y][x] = chessPiece;
		return null;
	}
	
	public int getWidth() {
		return pieces[0].length;
	}
	
	public int getHeight() {
		return pieces.length;
	}
	
	public int getLongerSide() {
		return Math.max(getWidth(), getHeight());
	}
	
	public ChessPiece[][] getPieces() {
		return pieces;
	}
}
