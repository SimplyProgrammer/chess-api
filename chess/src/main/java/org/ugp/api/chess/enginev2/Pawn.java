package org.ugp.api.chess.enginev2;

public class Pawn extends ChessPiece
{
	public Pawn(SimpleChessEngine board, int color, int x, int y) {
		super(board, "p", color, x, y);
	}
	
	public boolean canMoveTo(int x, int y) {
		int step = (getColor() == BLACK ? 1 : -1) * (getMoveCount() > 0 ? 1 : 2);
		
		return super.canMoveTo(x,y) && y - getY() <= step && myBoard.pieceAt(x, y) != null;
	}
}