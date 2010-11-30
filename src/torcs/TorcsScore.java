package torcs;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;

public class TorcsScore implements CalculateScore {

	@Override
	public double calculateScore(BasicNetwork network) {
		
		// TODO inject neural network
		return 0;
	}

	@Override
	public boolean shouldMinimize() {
		return false;
	}

}
