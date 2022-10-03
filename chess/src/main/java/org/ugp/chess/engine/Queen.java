package org.ugp.chess.engine;

public class Queen extends ChessPiece
{
	public Queen(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.QUEEN, color, x, y);
	}
	
	@Override
	public int[][] generateMovmentMetrix(int[][] newEmptyMetrix, boolean checkIfKingInCheck) {
		return movmentMetrix = generateMovmentFromDirs(this, generateMovmentFromDirs(this, newEmptyMetrix, STRAIGHT_DIRS, checkIfKingInCheck), DIAGONAL_DIRS, checkIfKingInCheck);
	}
}
