package tictactoe;

import org.junit.Test;


public class TicTacToeTest {

	@Test
	public void testGetState() {
		TicTacToe game = new TicTacToe(new BoringPlayer(), new BoringPlayer());
		double[] state = game.getState();
	}
}
