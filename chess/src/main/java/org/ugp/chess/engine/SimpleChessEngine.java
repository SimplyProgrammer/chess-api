package org.ugp.chess.engine;

import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

public class SimpleChessEngine implements SelfSerializable, Cloneable
{
	protected ChessPiece[][] pieces;
	protected int onTurn;
	protected ChessPiece[] kings;
	
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
		this.kings = new ChessPiece[2];
		this.onTurn = onTurn;
	}
	
	@Override
	public SimpleChessEngine clone() {
		SimpleChessEngine newChess = new SimpleChessEngine(getWidth(), getHeight(), getOnTurn());
		newChess.kings = kings.clone(); 
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				ChessPiece newOne = this.getPieces()[y][x];
				if (newOne != null)
					newChess.getPieces()[y][x] = newOne.clone(newChess);
			}
		}
		return newChess;
	}
	
	@Override
	public Object[] serialize() {
		Scope s = new Scope();
		s.put("w", getWidth());
		s.put("h", getHeight());
		s.put("onTurn", getOnTurn());
		s.put("pieces", getPieces());
		return new Object[] {s};
	}
	
	@Override
	public String toString() {
		String str = " ";
		for (int x = 0; x < getWidth(); str += (x++));
		str += "\n";
		
		for (int y = 0; y < getHeight(); y++) {
			str += y;
			for (int x = 0; x < getWidth(); x++) {
				ChessPiece piece = get(x, y);
				str += piece == null ? '#' : piece.getType();
			}
			str += "\n";
		}
		return str;
	}
	
	public int[][] getMovmentMetrix(int x, int y, boolean checkIfKingInCheck) {
		ChessPiece piece = get(x, y);
		int[][] metrix = new int[getHeight()][getWidth()];
		if (piece == null)
			return metrix;
		return piece.generateMovmentMetrix(metrix, checkIfKingInCheck);
	}
	
	public boolean isThreatened(int x, int y, boolean checkIfKingInCheck) {
		for (int xp = 0; xp < getWidth(); xp++) {
			for (int yp = 0; yp < getHeight(); yp++) {
				if (!isOnTurn(xp, yp) && getMovmentMetrix(xp, yp, checkIfKingInCheck)[y][x] == 1)
					return true;
			}
		}
		return false;
	}
	
	public boolean hasAnyMove(int x, int y, boolean checkIfKingInCheck) {
		for (int[] col : getMovmentMetrix(x, y, checkIfKingInCheck))
			for (int i : col)
				if (i > 0)
					return true;
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
	
	public ChessPiece move(int fromX, int fromY, int toX, int toY) {
		ChessPiece piece = get(fromX, fromY);
		if (piece == null)
			return piece;
		return piece.moveTo(toX, toY);
	}
	
	public boolean moveIfCan(int fromX, int fromY, int toX, int toY) {
		if (isOnTurn(fromX, fromY)) {
			ChessPiece piece = get(fromX, fromY).moveToIfCan(toX, toY);
			if (piece != null)
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
		ChessPiece piece = get(x, y);
		if (piece == null)
			return piece;
		pieces[y][x] = null;
		return piece;
	}

	public void remove(ChessPiece removePiece) {
		pieces[removePiece.getY()][removePiece.getX()] = null;
	}

	public ChessPiece put(String type, int color, int x, int y) {
		if (type.equals(ChessPiece.KING))
			return put(new King(this, color, x, y));
		if (type.equals(ChessPiece.QUEEN))
			return put(new Queen(this, color, x, y));
		if (type.equals(ChessPiece.KNIGHT))
			return put(new Knight(this, color, x, y));
		if (type.equals(ChessPiece.BISHOP))
			return put(new Bishop(this, color, x, y));
		if (type.equals(ChessPiece.ROOK))
			return put(new Rook(this, color, x, y));
		if (type.equals(ChessPiece.PAWN))
			return put(new Pawn(this, color, x, y));
		return put(new ChessPiece(this, type, color, x, y));
	}
	
	public ChessPiece put(ChessPiece chessPiece) {
		return put(chessPiece, chessPiece.getX(), chessPiece.getY());
	}
	
	public ChessPiece put(ChessPiece chessPiece, int x, int y) {
		if (isInBounds(x, y))
		{
			if (chessPiece.getType() == ChessPiece.KING)
				kings[chessPiece.getColor() % 2] = chessPiece;
			return pieces[y][x] = chessPiece;
		}
		return null;
	}
	
	public int endTurn() {
		return onTurn ^= 1;
	}
	
	public ChessPiece getKing(int color) {
		return kings[color % 2];
	}
	
	public boolean isCheck(int color) {
		ChessPiece king = getKing(color);
		return isThreatened(king.getX(), king.getY(), false);
	}
	
	public boolean isCheckmate(int color) {
		ChessPiece king = getKing(color);
		if (!isThreatened(king.getX(), king.getY(), false))
			return false;
		return hasAnyMove(king.getX(), king.getY(), true);
	}
	
	public void reset(int onTurn) {
		pieces = new ChessPiece[getWidth()][getHeight()];
		this.kings = new ChessPiece[2];
		this.onTurn = onTurn;
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
	
	public int getOnTurn() {
		return onTurn;
	}
	
	protected ChessPiece[][] getPieces() {
		return pieces;
	}
}
