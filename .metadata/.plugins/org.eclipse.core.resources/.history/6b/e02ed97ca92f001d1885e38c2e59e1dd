package org.ugp.api.chess.enginev2;


public class Bishop extends ChessPiece 
{
	public Bishop(SimpleChessEngine board, int color, int x, int y) {
		super(board, "b", color, x, y);
	}
	
	public boolean canMoveTo(int x, int y) {
		return super.canMoveTo(x, y) && isMovingDiagonal(x, y);
	}
}