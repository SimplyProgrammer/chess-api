package org.ugp.api.chess.enginev2;

public class King extends ChessPiece
{
	public King(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.KING, color, x, y);
	}
	
	@Override
	public int[][] generateMovmentMetrix(int[][] newEmptyMetrix, boolean checkIfKingInCheck) {
		return movmentMetrix = generateMovmentForRange(this, newEmptyMetrix, 1, checkIfKingInCheck);
	}
	
	@Override
	protected ChessPiece newInstance(SimpleChessEngine board) {
		return new King(board, 0, 0, 0);
	}
}
