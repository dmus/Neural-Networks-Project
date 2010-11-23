package tictactoe;

import java.util.List;
import java.util.Random;

public class RandomPlayer implements Player {

	private Random random = new Random();
	
	@Override
	public Move doMove(TicTacToe game) {
		List<Move> possibilities = game.getPossibleMoves();
		Move move = possibilities.get(random.nextInt(possibilities.size()));
		return move;
	}

	@Override
	public void onGameOver(TicTacToe game) {
	}

	@Override
	public String getName() {
		return toString();
	}

}
