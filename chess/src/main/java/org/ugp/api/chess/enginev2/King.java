package org.ugp.api.chess.enginev2;

public class King extends ChessPiece
{
	public King(SimpleChessEngine board, int color, int x, int y) {
		super(board, ChessPiece.KING, color, x, y);
	}
	
	@Override
	public boolean canMoveTo(int x, int y, boolean checkIfKingInCheck) {
		return super.canMoveTo(x, y, checkIfKingInCheck) && distanceTo(x, y) == 1 && !isThreatened(x, y);
	}
	
	@Override
	protected ChessPiece newInstance(SimpleChessEngine board) {
		return new King(board, 0, 0, 0);
	}
	
	public boolean isThreatened(int x, int y) {
		myBoard.remove(this);
		boolean isTileThreatened = myBoard.isThreatened(x, y, false);
		myBoard.put(this);
		return isTileThreatened;
	}
}
