package org.ugp.chess.engine;

public class Knight extends ChessPiece
{
	public static final int[][] L_MOVMENT_OFFSETS = {
		{1, 2},
		{-1, 2},
		{1, -2},
		{-1, -2},
		{2, 1},
		{-2, 1},
		{2, -1},
		{-2, -1},
	};
	
	public Knight(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.KNIGHT, color, x, y);
	}
	
	@Override
	public int[][] generateMovmentMetrix(int[][] newEmptyMetrix, boolean checkIfKingInCheck) {
		return movmentMetrix = generateMovmentFromOffsets(this, newEmptyMetrix, L_MOVMENT_OFFSETS, checkIfKingInCheck);
	}
}


