package org.ugp.api.chess.enginev2;


public class Bishop extends ChessPiece 
{
	public Bishop(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.BISHOP, color, x, y);
	}
	
	@Override
	public int[][] generateMovmentMetrix(int[][] newEmptyMetrix, boolean checkIfKingInCheck) {
		return movmentMetrix = generateMovmentFromDirs(this, newEmptyMetrix, DIAGONAL_DIRS, checkIfKingInCheck);
	}
}