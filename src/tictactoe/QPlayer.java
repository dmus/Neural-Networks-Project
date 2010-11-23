package tictactoe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

/**
 * Player learns by playing games (Q-learning)
 */
public class QPlayer implements Player {

	private String name;
	private Random random = new Random();
	private Map<Move, BasicNetwork> networks = new HashMap<Move, BasicNetwork>(9);
	private double t;
	private double a = 1, b = 0.99, c = 0.002;
	private int movesDone = 0;
	private int gamesPlayed = 0;
	private double learningRate = 0.1;
	private boolean explore = true;
	
	private double qOutput;
	private Move selectedMove;
	private double[] state;
	
	public QPlayer(String name) {
		this.name = name;
		
		// empty game to get all possible actions
		TicTacToe game = new TicTacToe(null, null);
		List<Move> actions = game.getPossibleMoves();
		
		// for each action a neural network
		for (Move action : actions) {
			BasicNetwork network = new BasicNetwork();
			network.addLayer(new BasicLayer(null, true, 9));
			network.addLayer(new BasicLayer(new ActivationTANH(), true, 27));
			network.addLayer(new BasicLayer(new ActivationTANH(), true, 1));
			network.getStructure().finalizeStructure();
			network.reset();
			
			networks.put(action, network);
		}
		
		// Temperature value
		t = a * Math.pow(b, gamesPlayed);
	}
	
	@Override
	public Move doMove(TicTacToe game) {
		if (movesDone > 0)
			observeResultingState(game);
		
		state = game.getState();
		List<Move> possible = game.getPossibleMoves();
		NeuralData state = new BasicNeuralData(this.state);
		selectedMove = null;
		
		double divisor = 0.0;
		Map<Move, Double> qValues = new HashMap<Move, Double>();
		Double bestQValue = null;
		
		for (Move action : possible) {
			BasicNetwork network = networks.get(action);
			final NeuralData output = network.compute(state);
			double qValue = output.getData(0);
			qValues.put(action, qValue);
			
			if (!explore) {
				if (bestQValue == null || qValue > bestQValue) {
					bestQValue = qValue;
					selectedMove = action;
				}
			} else {
				divisor += Math.exp(qValue / t);
			}
		}
		
		if (explore) {
			double choice = random.nextDouble();
			double sum = 0.0;
			
			for (Move action : possible) {
				double p = Math.exp(qValues.get(action) / t) / divisor;
				
				sum += p;
				if (choice <= sum) {
					selectedMove = action;
					break;
				}
			}
		}
			
		qOutput = qValues.get(selectedMove);
		movesDone++;
		return selectedMove;
	}

	public void observeResultingState(TicTacToe game) {
		// reward received
		double reward = 0.0;
		if (game.hasEnded() && game.isDraw() == false) {
			if (game.getWinner() == this)
				reward += 1;
			else
				reward -= 1;
		}

		// observe resulting state
		NeuralData newState = new BasicNeuralData(game.getState());
		Double best = 0.0;
		List<Move> possible = game.getPossibleMoves();
		for (Move action : possible) {
			final NeuralData output = networks.get(action).compute(newState);
			
			if (best == null || output.getData(0) > best) {
				best = output.getData(0);
			}
		}
		
		// adjust the neural network
		double qTarget = (1 - learningRate) * qOutput + learningRate * (reward + best);
		
		NeuralDataSet trainingSet = new BasicNeuralDataSet(
			new double[][]{ state }, 
			new double[][] { {qTarget} }
		);
		final Train train = new Backpropagation(networks.get(selectedMove), trainingSet);
		train.iteration();
	}
	
	@Override
	public void onGameOver(TicTacToe game) {
		observeResultingState(game);
		
		gamesPlayed++;
		movesDone = 0;
		t = a * Math.pow(b, gamesPlayed);
		if (t <= c) {
			explore = false;
		}
	}

	@Override
	public String getName() {
		return name;
	}
}
