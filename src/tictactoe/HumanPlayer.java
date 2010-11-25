package tictactoe;

import java.util.Scanner;

public class HumanPlayer implements Player {

	@Override
	public String getName() {
		return "Human";
	}

	@Override
	public Move doMove(TicTacToe game) {
		System.out.print(game.toString());
		Scanner in = new Scanner(System.in);
		System.out.println("Enter row:");
		int x = in.nextInt();
		System.out.println("Enter column:");
		int y = in.nextInt();
		return new Move(x, y);
	}

	@Override
	public void onGameOver(TicTacToe game) {
		System.out.print(game.toString());

		if (game.getWinner() == this)
			System.out.println("You won!!");
		else if (game.isDraw())
			System.out.println("Draw");
		else
			System.out.println("You lost!!");
	}

}
