package org.ugp.api.chess.enginev2;

public class Knight extends ChessPiece
{
	public Knight(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.KNIGHT, color, x, y);
	}
	
	@Override
	public boolean canMoveTo(int x, int y, boolean checkIfKingInCheck) {
		if (super.canMoveTo(x, y, checkIfKingInCheck)) 
		{
			if (Math.abs(getX() - x) == 2 && Math.abs(getY() - y) == 1)
				return true;
			if (Math.abs(getX() - x) == 1 && Math.abs(getY() - y) == 2)
				return true;
		}
		return false;
	}
	
	@Override
	protected ChessPiece newInstance(SimpleChessEngine board) {
		return new Knight(board, 0, 0, 0);
	}
}
