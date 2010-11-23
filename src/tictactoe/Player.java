package tictactoe;

public interface Player {

	public String getName();
	public Move doMove(TicTacToe game);
	public void onGameOver(TicTacToe game);
}
