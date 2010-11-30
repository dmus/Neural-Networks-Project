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

/**
 * Player learns by playing games (Q-learning)
 */
public class QPlayer implements Player {

	/**
	 * Player's name
	 */
	private String name;
	
	/**
	 * Random generator
	 */
	private Random random = new Random();
	
	/**
	 * Neural network to approximate q-value for each move
	 */
	private Map<Move, BasicNetwork> networks = new HashMap<Move, BasicNetwork>(9);
	
	/**
	 * Parameters for exploration vs exploitation dilemma
	 */
	private double t, a = 1, b = 0.99, c = 0.002;
	
	/**
	 * Number of moves done in current game
	 */
	private int movesDone = 0;
	
	/**
	 * Number of training games played
	 */
	private int gamesPlayed = 0;
	
	/**
	 * Learning rate
	 */
	private double learningRate = 0.1;
	private boolean explore = true;
	private boolean learn = true;
	
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
		
		boolean exploreInMove = explore && learn;
		
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
			
			if (!exploreInMove) {
				if (bestQValue == null || qValue > bestQValue) {
					bestQValue = qValue;
					selectedMove = action;
				}
			} else {
				divisor += Math.exp(qValue / t);
			}
		}
		
		if (exploreInMove) {
			double choice = random.nextDouble();
			double sum = 0;
			
			for (Move action : possible) {
				double p = Math.exp(qValues.get(action) / t) / divisor;
				
				sum += p;
				if (choice <= sum) {
					selectedMove = action;
					break;
				}
			}
		}
		
		if (selectedMove == null) {
			System.out.println("Hoij");
		}
		
		qOutput = qValues.get(selectedMove);
		movesDone++;
		return selectedMove;
	}

	private void observeResultingState(TicTacToe game) {
		if (!learn)
			return;
		
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
		final Train train = new Backpropagation(networks.get(selectedMove), trainingSet, 0.2, 0.9);
		train.iteration();
	}
	
	@Override
	public void onGameOver(TicTacToe game) {
		movesDone = 0;
		
		if (!learn)
			return;
		
		observeResultingState(game);
		gamesPlayed++;
		
		t = a * Math.pow(b, gamesPlayed);
		explore = (t > c);
	}
	
	
	public void setLearn(boolean learn) {
		this.learn = learn;
	}
	

	@Override
	public String getName() {
		return name;
	}
}
