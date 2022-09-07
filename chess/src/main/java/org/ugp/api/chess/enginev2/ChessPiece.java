package org.ugp.api.chess.enginev2;

import org.ugp.serialx.Scope;
import org.ugp.serialx.protocols.SelfSerializable;

public class ChessPiece implements SelfSerializable
{
	public static final int BLACK = 0, WHITE = 1;
	
	protected int x, y;
	protected int color;
	protected int moveCount;
	protected SimpleChessEngine myBoard;
	protected String type;

	public ChessPiece(SimpleChessEngine board, String type, int color, int x, int y) {
		this.myBoard = board;
		this.color = color;
		this.x = x;
		this.y = y;
		this.type = type;
		
		myBoard.put(this, x, y);
	}
	
	@Override
	public String toString() {
		return type;
	}
	
	@Override
	public Object[] serialize() {
		Scope s = new Scope();
		s.put("color", color);
		s.put("type", type);
		return new Object[] {s};
	}
	
	public boolean canMoveTo(int x, int y) {
		if (!myBoard.isInBounds(x, y))
			return false;
		
		ChessPiece piece = myBoard.get(x, y);
		if (piece == null || piece.getColor() != this.color) 
			return true;
		return false;
	}
	
	public ChessPiece moveTo(int x, int y) {	
		if (myBoard.get(x, y) == this)
			myBoard.remove(this);
		this.x = x;
		this.y = y;
		
		ChessPiece target = myBoard.get(x, y);
		if (target != null) 
			kill();
		
		myBoard.put(this, x, y);
		moveCount++;
		return this;
	}
	
	public void kill() {
		myBoard.remove(this);
		x = y = -1;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getColor() {
		return color;
	}
	
	public int getMoveCount() {
		return moveCount;
	}

	protected boolean isMovingStraight(int x, int y) {
		int currX = this.getX();
		int currY = this.getY();
		
		int smallerVal;
		int largerVal;
		
		
		if (currX == x) {
			if (currY > y) {
				smallerVal = y;
				largerVal = currY;
			}
			else if (y > currY) {
				smallerVal = currY;
				largerVal = y;
			}
			else 
				return false;
			
			smallerVal++;
			for(; smallerVal < largerVal; smallerVal++) {
				if (myBoard.get(currX, smallerVal) != null) {
					return false;
				}
			}
			return true;
		}
		
		
		if (currY == y) {
			if (currX > x) {
				smallerVal = x;
				largerVal = currX;
			}
			else if (x > currX) {
				smallerVal = currX;
				largerVal = x;
			}
			else 
				return false;
			
			smallerVal++;
			for(; smallerVal < largerVal; smallerVal++) {
				if (myBoard.get(smallerVal, currY) != null) {
					return false;
				}
			}
			return true;
		}
		
		return false;
	}

	
	protected boolean isMovingDiagonal(int x, int y) {
		int xStart = 0;
		int yStart = 0;
		int xFinish = 1;
		
		int xTotal = Math.abs(x - this.getX());
		int yTotal = Math.abs(y - this.getY());
		
		if (xTotal == yTotal) {
			if (x < this.getX()) {
				xStart = x;
				xFinish = this.getX();
			}
			else if (x > this.getX()) {
				xStart = this.getX();
				xFinish = x;
			}
			else
				return false;
			
			if (y < this.getY()) {
				yStart = y;
			}
			else if (y > this.getY()) {
				yStart = this.getY();
			}
			else
				return false;
			
			xStart++;
			yStart++;
			
			
			for(;xStart < xFinish; xStart++, yStart++) {
				if (myBoard.get(xStart, yStart) != null) {
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
