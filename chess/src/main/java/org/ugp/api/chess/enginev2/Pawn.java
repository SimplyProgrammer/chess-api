package org.ugp.api.chess.enginev2;

public class Pawn extends ChessPiece
{
	public Pawn(SimpleChessEngine board, int color, int x, int y) {
		super(board, "p", color, x, y);
	}
	
	public boolean canMoveTo(int x, int y) {
		if (!super.canMoveTo(x, y) || x != getX())
			return false;
		
		int step = getMoveCount() > 0 ? 1 : 2;
		if (getColor() == BLACK)
		{
			for (int i = 1; i <= step; i++) {
				System.out.println(getX() + " " + (getY() + i) + !myBoard.isEmpty(getX(), getY() + i));
				if (!myBoard.isEmpty(getX(), getY() + i))
					return false;
			}
		}
		else
		for (int i = getY(); i <= step; i--) {
			if (!myBoard.isEmpty(getX(), i))
				return false;
		}
		return true;
	}
}
