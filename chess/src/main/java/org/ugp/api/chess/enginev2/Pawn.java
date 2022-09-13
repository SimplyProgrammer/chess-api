package org.ugp.api.chess.enginev2;

public class Pawn extends ChessPiece
{
	public Pawn(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.PAWN, color, x, y);
	}
	
	@Override
	public int[][] generateMovmentMetrix(int[][] newEmptyMetrix, boolean checkIfKingInCheck) {
		return movmentMetrix = generateMovmentForRange(this, newEmptyMetrix, getMoveCount() < 1 ? 2 : 1, checkIfKingInCheck);
	}
	
	@Override
	public boolean canMoveTo(int x, int y, boolean checkIfKingInCheck) {
		return super.canMoveTo(x, y, checkIfKingInCheck) && (canMoveForeward(x, y) || canFork(x, y) || canEnpassant(x, y));
	}
	
	@Override
	protected ChessPiece newInstance(SimpleChessEngine board) {
		return new Pawn(board, 0, 0, 0);
	}
	
	public boolean canFork(int x, int y) {
		return x != getX() && (getColor() == ChessPiece.BLACK ? y > getY() : y < getY()) && distanceTo(x, y) == 1 && !myBoard.isEmpty(x, y);
	}

	public boolean canMoveForeward(int x, int y) {
		return getX() == x && (getColor() == ChessPiece.BLACK ? y > getY() : y < getY()) && myBoard.isEmpty(x, y) && canMoveStraight(this, x, y);
	}
	
	public boolean canEnpassant(int x, int y) {
		return false; //Uniplemented...
	}
}
