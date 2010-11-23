package tictactoe;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;

public class TicTacToeBenchmark implements CalculateScore {

	private int games;
	
	public TicTacToeBenchmark(int games) {
		this.games = games;
	}
	
	@Override
	public double calculateScore(BasicNetwork network) {
		double score = 0;
		
		for (int i = 0; i < games; i++) {
			NeatPlayer player = new NeatPlayer(network);
			TicTacToe game = new TicTacToe(player, new RandomPlayer());
			game.start();
			if (game.getWinner() == player)
				score++;
			else if (game.getWinner() != null)
				score--;
		}

		// normalize score
		score = score / (games * 2) + 0.5 * (games / 100);
		
		//System.out.println(score);
		return Math.random();
	}

	@Override
	public boolean shouldMinimize() {
		return false;
	}

}
