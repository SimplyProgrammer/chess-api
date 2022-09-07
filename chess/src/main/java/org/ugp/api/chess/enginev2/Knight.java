package org.ugp.api.chess.enginev2;

public class Knight extends ChessPiece
{
	public Knight(SimpleChessEngine board, int color, int x, int y) {
		super(board, "n", color, x, y);
	}
	
	public boolean canMoveTo(int x, int y) {
		if (super.canMoveTo(x,y)) 
		{
			if (Math.abs(getX() - x) == 2 && Math.abs(getY() - y) == 1)
				return true;
			if (Math.abs(getX() - x) == 1 && Math.abs(getY() - y) == 2)
				return true;
		}
		return false;
	}
}
