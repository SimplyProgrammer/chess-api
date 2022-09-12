package org.ugp.api.chess.enginev2;

public class Queen extends ChessPiece
{
	public Queen(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.QUEEN, color, x, y);
	}
	
	@Override
	public boolean canMoveTo(int x, int y, boolean checkIfKingInCheck) {
		return super.canMoveTo(x, y, checkIfKingInCheck) && (canMoveStraight(this, x, y) || canMoveDiagonal(this, x, y));
	}
	
	@Override
	protected ChessPiece newInstance(SimpleChessEngine board) {
		return new Queen(board, 0, 0, 0);
	}
}
