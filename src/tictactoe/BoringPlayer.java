package tictactoe;

import java.util.List;
import java.util.Random;

/**
 * Player plays always the same
 */
public class BoringPlayer implements Player {

	private Random random = new Random(37);
	
	@Override
	public String getName() {
		return "Fixed player";
	}

	@Override
	public Move doMove(TicTacToe game) {
		List<Move> possibilities = game.getPossibleMoves();
		Move move = possibilities.get(random.nextInt(possibilities.size()));
		return move;
	}

	@Override
	public void onGameOver(TicTacToe game) {
		random.setSeed(42);
	}

}
