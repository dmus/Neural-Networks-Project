package tictactoe;

import org.encog.util.logging.Logging;

public class Trainer {
	public static void main(String[] args) {
		Logging.stopConsoleLogging();
		TicTacToeBenchmark benchmark = new TicTacToeBenchmark(100, new RandomPlayer());
		
		QPlayer p1 = new QPlayer("P1");
		Player p2 = new RandomPlayer();//new QPlayer("P2");
		
		p1.setLearn(false);
		System.out.println("Score before training: " + benchmark.calculateScore(p1));
		p1.setLearn(true);
		
		// training games
		for (int i = 1; i <= 100000; i++) {
			TicTacToe game = new TicTacToe(p1, p2);
			game.start();
			
			if (i % 10000 == 0) {
				p1.setLearn(false);
				System.out.println("Score after " + i + " training games: " + benchmark.calculateScore(p1));
				p1.setLearn(true);
			}
		}
		
		p1.setLearn(false);
		System.out.println("Score after training: " + benchmark.calculateScore(p1));
		p1.setLearn(true);
		
		TicTacToe ticTacToe = new TicTacToe(p1, new HumanPlayer());
		ticTacToe.start();
	}
}
