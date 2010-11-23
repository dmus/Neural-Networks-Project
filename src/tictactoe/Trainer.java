package tictactoe;

import org.encog.util.logging.Logging;

public class Trainer {
	public static void main(String[] args) {
		Logging.stopConsoleLogging();
		
		QPlayer p1 = new QPlayer("P1");
		QPlayer p2 = new QPlayer("P2");
		
		System.out.println("Score before training");
		int won = 0, draw = 0, lost = 0;
		for (int i = 1; i <= 100; i++) {
			TicTacToe game = new TicTacToe(p1, new RandomPlayer());
			game.start();
			
			if (game.getWinner() == p1)
				won++;
			else if (game.isDraw())
				draw++;
			else
				lost++;
		}
		System.out.println("Won: " + won + " Draw: " + draw + " Lost: " + lost);
		
		// 1 million training games
		for (int i = 1; i <= 1000000; i++) {
			TicTacToe game = new TicTacToe(p1, p2);
			game.start();
		}
		
		System.out.println("Score after training");
		won = 0;
		lost = 0;
		draw = 0;
		for (int i = 1; i <= 100; i++) {
			TicTacToe game = new TicTacToe(p1, new RandomPlayer());
			game.start();
			
			if (game.getWinner() == p1)
				won++;
			else if (game.isDraw())
				draw++;
			else
				lost++;
		}
		System.out.println("Won: " + won + " Draw: " + draw + " Lost: " + lost);
		
		TicTacToe ticTacToe = new TicTacToe(p1, new HumanPlayer());
		ticTacToe.start();
		
		ticTacToe = new TicTacToe(new HumanPlayer(), p2);
		ticTacToe.start();
	}
}
