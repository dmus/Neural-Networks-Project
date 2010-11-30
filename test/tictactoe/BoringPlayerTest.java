package tictactoe;

import org.junit.Test;


public class BoringPlayerTest {

	@Test
	public void testPlayer() {
		TicTacToe game = new TicTacToe(new BoringPlayer(), new HumanPlayer());
		game.start();
	}
}
