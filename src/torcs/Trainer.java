package torcs;

import java.util.concurrent.Exchanger;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;
import org.encog.neural.networks.training.neat.NEATTraining;


public class Trainer implements Runnable, CalculateScore {
	
	private Exchanger<Object> exchanger;
	private NEATTraining train;
	
	public Trainer(Exchanger<Object> exchanger) {
		this.exchanger = exchanger;
	}
	
	public void run() {
		train = new NEATTraining(this, 7, 1, 30);
		train.setOutputActivationFunction(new ActivationLinear());
		
		int epoch = 1;
		do {
			System.out.println("Epoch " + epoch + " started");
			train.iteration();
			epoch++;
		} while (true);
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

	public BasicNetwork getNetwork() {
		return train.getNetwork();
	}
	
	@Override
	public boolean shouldMinimize() {
		return false;
	}
}
