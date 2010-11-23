package tictactoe;

public class Move {
	private int x, y;
	
	public Move(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other.getClass() != this.getClass())
			return false;
		
		Move otherMove = (Move) other;
		return getX() == otherMove.getX() && getY() == otherMove.getY();
	}
	
	@Override
	public int hashCode() {
		return (x * 37) ^ y;
	}
	
	@Override
	public String toString() {
		return x + " " + y;
	}
}
