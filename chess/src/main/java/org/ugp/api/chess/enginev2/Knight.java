package org.ugp.api.chess.enginev2;

public class Knight extends ChessPiece
{
	public static int[][] L_MOVMENT_OFFSETS = {
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
	
	@Override
	protected ChessPiece newInstance(SimpleChessEngine board) {
		return new Knight(board, 0, 0, 0);
	}
}


