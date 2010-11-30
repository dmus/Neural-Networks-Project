package torcs;

import java.util.concurrent.Exchanger;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.neat.NEATTraining;


public class Trainer implements Runnable, CalculateScore {
	
	private Exchanger<Object> exchanger;
	
	public Trainer(Exchanger<Object> exchanger) {
		this.exchanger = exchanger;
	}
	
	public void run() {
		final NEATTraining train = new NEATTraining(this, 3, 1, 10);
		train.setOutputActivationFunction(new ActivationTANH());
		
		int epoch = 1;
		do {
			System.out.println("Epoch " + epoch + " started");
			train.iteration();
			epoch++;
		} while (epoch <= 10);
		
		System.out.println("Best score: " + train.getNetwork());
	}

	@Override
	public double calculateScore(BasicNetwork network) {
		try {
			exchanger.exchange(network);
			
			return (Double) exchanger.exchange(null);
		} catch (InterruptedException e) {
			return Double.MIN_VALUE;
		}
	}

	@Override
	public boolean shouldMinimize() {
		return false;
	}
}
