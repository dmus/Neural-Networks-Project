package tictactoe;

import java.util.ArrayList;
import java.util.List;

public class TicTacToe {

	/**
	 * True used for X values, false for O
	 */
	private Boolean[][] grid = new Boolean[3][3];
	
	/**
	 * The X player and O player
	 */
	private Player xPlayer, oPlayer;
	
	/**
	 * Winner of the game
	 */
	private Player winner = null;
	
	/**
	 * Total number of moves done
	 */
	private int moves = 0;
	
	public TicTacToe(Player xPlayer, Player oPlayer) throws IllegalArgumentException {
		this.xPlayer = xPlayer;
		this.oPlayer = oPlayer;
	}
	
	/**
	 * Let players do moves
	 */
	public void start() {
		while (!hasEnded()) {
			Player player = (moves % 2 == 0) ? xPlayer : oPlayer;
			Move move;
			do {
				move = player.doMove(this);
			} while (!isValid(move));
			grid[move.getX() - 1][move.getY() - 1] = (player == xPlayer);
			moves++;
		}
		
		xPlayer.onGameOver(this);
		oPlayer.onGameOver(this);
	}
	
	public boolean isValid(Move move) {
		try {
			return (grid[move.getX() - 1][move.getY() - 1] == null);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}

	public boolean hasEnded() {
		// check rows
		for (int row = 0; row < 3; row++) {
			if (grid[row][0] == null)
				continue;
			
			boolean breakOut = false;
			for (int i = 1; i < 3; i++) {
				if (grid[row][i] != grid[row][i - 1]) {
					breakOut = true;
					break;
				}
			}
			
			if (breakOut)
				continue;
			
			winner = grid[row][0] ? xPlayer : oPlayer;
			return true;
		}
		
		// check columns
		for (int column = 0; column < 3; column++) {
			if (grid[0][column] == null)
				continue;
			
			boolean breakOut = false;
			for (int i = 1; i < 3; i++) {
				if (grid[i][column] != grid[i - 1][column]) {
					breakOut = true;
					break;
				}
			}
			
			if (breakOut)
				continue;
			
			winner = grid[0][column] ? xPlayer : oPlayer;
			return true;
		}
		
		// check diagonals
		if (grid[1][1] != null) {
			if (grid[0][0] == grid[1][1] && grid[0][0] == grid[2][2]) {
				winner = grid[1][1] ? xPlayer : oPlayer;
				return true;
			}
			
			if (grid[0][2] == grid[1][1] && grid[0][2] == grid[2][0]) {
				winner = grid[1][1] ? xPlayer : oPlayer;
				return true;
			}
		}
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (grid[i][j] == null)
					return false;
			}
		}
		
		return true;
	}
	
	public Player getWinner() {
		return winner;
	}
	
	public boolean isDraw() {
		return hasEnded() && getWinner() == null;
	}
	
	public List<Move> getPossibleMoves() {
		List<Move> moves = new ArrayList<Move>();
		for (int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if (grid[i][j] == null)
					moves.add(new Move(i + 1, j + 1));
			}
		}

		return moves;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				hash = 31 * hash + (null == grid[i][j] ? 0 : grid[i][j].hashCode());
		
		return hash;
	}
	
	public double[] getState() {
		double[] state = new double[9];
		
		int k = 0;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				if (grid[i][j] == null) {
					state[k] = 0.0;
					k++;
					continue;
				}
				state[k] = (grid[i][j]) ? 1.0 : -1.0;
				k++;
			}
		
		return state;
	}
	
	@Override
	public String toString() {
		String string = "";
		for (int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if (grid[i][j] == null) {
					string += "_ ";
					continue;
				}
				
				string += (grid[i][j] ? "X " : "O ");
			}
			string += "\n";
		}
		
		return string;
	}
	
	public static void main(String[] args) {
		TicTacToe ticTacToe = new TicTacToe(new RandomPlayer(), new HumanPlayer());
		ticTacToe.start();
		System.out.print(ticTacToe.toString());
	}
}
