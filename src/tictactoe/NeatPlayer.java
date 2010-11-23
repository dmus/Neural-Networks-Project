package tictactoe;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;

public class NeatPlayer implements Player {

	private BasicNetwork network;
	
	public NeatPlayer(BasicNetwork network) {
		this.network = network;
	}

	@Override
	public String getName() {
		return "NeatPlayer";
	}

	public BasicNetwork getNetwork() {
		return network;
	}
	
	@Override
	public Move doMove(TicTacToe game) {
		NeuralData input = new BasicNeuralData(game.getState());
		NeuralData output = network.compute(input);
		
		double[] values = output.getData();
	    Map<Double, Integer> map = new TreeMap<Double, Integer>();
	    for (int i = 0; i < values.length; ++i) {
	        map.put(values[i], i);
	    }
	    Collection<Integer> indices = map.values();
		for (Integer index : indices) {
			Move move = new Move((int) (Math.floor(index / 3) + 1), (index % 3) + 1);
			if (game.isValid(move))
				return move;
		}
		
		return null;
	}

	@Override
	public void onGameOver(TicTacToe game) {
	}

}
