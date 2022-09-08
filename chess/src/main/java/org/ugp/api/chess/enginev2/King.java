package org.ugp.api.chess.enginev2;

public class King extends ChessPiece
{
	public King(SimpleChessEngine board, int color, int x, int y) {
		super(board, "k", color, x, y);
	}
	
	public boolean canMoveTo(int x, int y) {
		return super.canMoveTo(x, y) && distanceTo(x, y) == 1;
	}
}
