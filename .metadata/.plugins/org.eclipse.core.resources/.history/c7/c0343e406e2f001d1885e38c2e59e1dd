package org.ugp.api.chess.enginev2;

public class Queen extends ChessPiece
{
	public Queen(SimpleChessEngine board, int color, int x, int y) {
		super(board, "q", color, x, y);
	}
	
	public boolean canMoveTo(int x, int y) {
		return super.canMoveTo(x, y) && isMovingStraight(x, y) && isMovingDiagonal(x, y);
	}
}
