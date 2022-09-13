package org.ugp.api.chess.enginev2;

public class Queen extends ChessPiece
{
	public Queen(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.QUEEN, color, x, y);
	}
	
	@Override
	public int[][] generateMovmentMetrix(int[][] newEmptyMetrix, boolean checkIfKingInCheck) {
		return movmentMetrix = generateMovmentFromDirs(this, generateMovmentFromDirs(this, newEmptyMetrix, STRAIGHT_DIRS, checkIfKingInCheck), DIAGONAL_DIRS, checkIfKingInCheck);
	}
	
	@Override
	protected ChessPiece newInstance(SimpleChessEngine board) {
		return new Queen(board, 0, 0, 0);
	}
}
