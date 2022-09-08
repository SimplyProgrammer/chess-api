package org.ugp.api.chess.enginev2;

public class Pawn extends ChessPiece
{
	public Pawn(SimpleChessEngine board, int color, int x, int y) {
		super(board, "p", color, x, y);
	}
	
	public boolean canMoveTo(int x, int y) {
		
		ChessPiece pieceToKill = myBoard.get(x, y);
		int step = getMoveCount() > 0 ? 1 : 2;
		if (getColor() == BLACK)
		{
			return (y > getY() && x == getX() && isMovingStraight(x, y) && distanceTo(x, y) <= step);
		}
		return getY() - y <= step && y < getY();
	}
}
