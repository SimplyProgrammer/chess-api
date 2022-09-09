package org.ugp.api.chess.enginev2;

public class Rook extends ChessPiece
{
	public Rook(SimpleChessEngine board, int color, int x, int y) {
		super(board, "r", color, x, y);
	}
	
	public boolean canMoveTo(int x, int y) {
		return super.canMoveTo(x, y) && isMovingStraight(x, y);
	}
}