package tictactoe;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;

public class TicTacToeBenchmark implements CalculateScore {

	private int games;
	private Player opponent;
	
	public TicTacToeBenchmark(int games) {
		this(games, new RandomPlayer());
	}
	
	public TicTacToeBenchmark(int games, Player opponent) {
		this.games = games;
		this.opponent = opponent;
	}
	
	public double calculateScore(Player player) {
		double score = 0;
		
		for (int i = 0; i < games; i++) {
			TicTacToe game = new TicTacToe(player, opponent);
			game.start();

			if (game.getWinner() == player)
				score++;
			else if (game.getWinner() != null)
				score--;
		}

		// normalize score
		//score = score / (games * 2) + 0.5 * (games / 100);
		score = score / games;
		return score;
	}
	
	@Override
	public double calculateScore(BasicNetwork network) {
		return calculateScore(new NeatPlayer(network));
	}

	@Override
	public boolean shouldMinimize() {
		return false;
	}

}
