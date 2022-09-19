package org.ugp.api.chess.enginev2;

public class Rook extends ChessPiece
{
	public Rook(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.ROOK, color, x, y);
	}
	
	@Override
	public int[][] generateMovmentMetrix(int[][] newEmptyMetrix, boolean checkIfKingInCheck) {
		return movmentMetrix = generateMovmentFromDirs(this, newEmptyMetrix, STRAIGHT_DIRS, checkIfKingInCheck);
	}
}