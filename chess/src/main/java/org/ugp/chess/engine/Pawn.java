package org.ugp.chess.engine;

public class Pawn extends ChessPiece
{
	public static final int[][][] PAWN_MOVMENT_OFFSETS = {
		{ //Black
			{0, 1},
			{0, 2},
			{-1, 1},
			{1, 1},
		},
		{ //White
			{0, -1},
			{0, -2},
			{-1, -1},
			{1, -1},
		}
	};
	
	public Pawn(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.PAWN, color, x, y);
	}
	
	@Override
	public int[][] generateMovmentMetrix(int[][] newEmptyMetrix, boolean checkIfKingInCheck) {
		return movmentMetrix = generateMovmentFromOffsets(this, newEmptyMetrix, PAWN_MOVMENT_OFFSETS[getColor() % 2], checkIfKingInCheck);
	}
	
	@Override
	public boolean canMoveTo(int x, int y, boolean checkIfKingInCheck) {
		return super.canMoveTo(x, y, checkIfKingInCheck) && (canMoveForeward(x, y) || canFork(x, y) || canEnpassant(x, y));
	}
	
	public boolean canFork(int x, int y) {
		return x != getX() && distanceTo(x, y) == 1 && !myBoard.isEmpty(x, y);
	}

	public boolean canMoveForeward(int x, int y) {
		return getX() == x && distanceTo(x, y) <= (getMoveCount() > 0 ? 1 : 2) && myBoard.isEmpty(x, y) && canMoveStraight(this, x, y);
	}
	
	public boolean canEnpassant(int x, int y) {
		return false; //Uniplemented...
	}
}
