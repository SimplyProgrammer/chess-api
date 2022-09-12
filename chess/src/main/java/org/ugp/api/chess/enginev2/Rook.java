package org.ugp.api.chess.enginev2;

public class Rook extends ChessPiece
{
	public Rook(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.ROOK, color, x, y);
	}
	
	@Override
	public boolean canMoveTo(int x, int y, boolean checkIfKingInCheck) {
		return super.canMoveTo(x, y, checkIfKingInCheck) && canMoveStraight(this, x, y);
	}
	
	@Override
	protected ChessPiece newInstance(SimpleChessEngine board) {
		return new Rook(board, 0, 0, 0);
	}
}