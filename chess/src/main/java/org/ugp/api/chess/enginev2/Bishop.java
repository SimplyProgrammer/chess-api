package org.ugp.api.chess.enginev2;


public class Bishop extends ChessPiece 
{
	public Bishop(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.BISHOP, color, x, y);
	}
	
	@Override
	public boolean canMoveTo(int x, int y, boolean checkIfKingInCheck) {
		return super.canMoveTo(x, y, checkIfKingInCheck) && canMoveDiagonal(this, x, y);
	}
	
	@Override
	protected ChessPiece newInstance(SimpleChessEngine board) {
		return new Bishop(board, 0, 0, 0);
	}
}