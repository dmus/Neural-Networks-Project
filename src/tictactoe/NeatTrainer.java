package tictactoe;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.neat.NEATTraining;
import org.encog.persist.EncogPersistedCollection;

public class NeatTrainer {
	public static void main(String[] args) {
		CalculateScore score = new TicTacToeBenchmark(100);
		final NEATTraining train = new NEATTraining(score, 9, 9, 100);
		train.setOutputActivationFunction(new ActivationSigmoid());
		
		int epoch = 1;
		do {
			train.iteration();
			
			if (epoch % 100 == 0) {
				System.out.println("Score after epoch " + epoch + ": " + score.calculateScore(train.getNetwork()) + " " + train.getNetwork().hashCode());
			}
			
			epoch++;
		} while (epoch <= 1000);

		final EncogPersistedCollection encog = new EncogPersistedCollection("data/tictactoe/network.eg");
		encog.create();
		encog.add("network", train.getNetwork());
	}
}
