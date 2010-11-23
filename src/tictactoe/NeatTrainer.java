package tictactoe;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.neat.NEATTraining;
import org.encog.util.logging.Logging;

public class NeatTrainer {
	public static void main(String[] args) {
		CalculateScore score = new TicTacToeBenchmark(100);
		final NEATTraining train = new NEATTraining(score, 9, 9, 100);
		train.setOutputActivationFunction(new ActivationSigmoid());
		
		int epoch = 1;
		do {
			train.iteration();
			epoch++;
		} while (epoch <= 100);

		BasicNetwork network = train.getNetwork();
		System.out.println(network);
	}
}
