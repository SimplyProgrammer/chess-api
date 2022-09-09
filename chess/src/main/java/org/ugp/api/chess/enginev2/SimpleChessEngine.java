package org.ugp.api.chess.enginev2;

import java.beans.IntrospectionException;

import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

public class SimpleChessEngine implements SelfSerializable, Cloneable
{
	protected ChessPiece[][] pieces;
	protected int onTurn;
	
//	public SimpleChessEngine(Scope fromScope) {
//		try {
//			pieces = new ChessPiece[fromScope.getInt("h")][fromScope.getInt("w")];
//			Scope pieces = fromScope.getScope("");
//			onTurn = fromScope.get("onTurn");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public SimpleChessEngine(int width, int height, int onTurn) {
		pieces = new ChessPiece[height][width];
		this.onTurn = onTurn;
	}
	
	@Override
	protected SimpleChessEngine clone() {
		SimpleChessEngine newChess = new SimpleChessEngine(getWidth(), getHeight(), onTurn);
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				newChess.getPieces()[y][x] = this.getPieces()[y][x];
			}
		}
		return newChess;
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
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				ChessPiece piece = get(x, y);
				str += piece == null ? '#' : piece;
			}
			str += "\n";
		}
		return str;
	}
	
	public int[][] getMovmentMetrix(int px, int py) {
		int[][] metrix = new int[getHeight()][getWidth()];
		
		ChessPiece piece = get(px, py);
		if (piece == null)
			return metrix;
//		System.out.println(px + " " + py + " " + piece);
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (piece.canMoveTo(x, y))
					metrix[y][x]++;
			}
		}
		
		return metrix;
	}
	
	public boolean isThreatened(int x, int y) {
		for (int xp = 0; xp < getWidth(); xp++) {
			for (int yp = 0; yp < getHeight(); yp++) {
				if (!isOnTurn(xp, yp) && getMovmentMetrix(xp, yp)[y][x] == 1)
					return true;
			}
		}
		return false;
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
			ChessPiece piece = get(fromX, fromY).moveToIfCan(toX, toY);
			if (piece != null)
			{
				remove(fromX, fromY);
				onTurn ^= 1;
				return true;
			}
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
	
	public ChessPiece put(ChessPiece chessPiece) {
		return put(chessPiece, chessPiece.getX(), chessPiece.getY());
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
