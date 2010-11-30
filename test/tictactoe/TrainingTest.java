package tictactoe;

import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.lma.LevenbergMarquardtTraining;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.junit.Test;


public class TrainingTest {

	@Test
	public void testIteration() {
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, 9));
		network.addLayer(new BasicLayer(new ActivationTANH(), true, 27));
		network.addLayer(new BasicLayer(new ActivationTANH(), true, 1));
		network.getStructure().finalizeStructure();
		network.reset();
		
		
		double[] state = {0.0, 0.0, 0.0, 0.0, 0.0, -1.0, 1.0, 0.0, 0.0};
		double beforeTraining = network.compute(new BasicNeuralData(state)).getData(0);
		
		NeuralDataSet trainingSet = new BasicNeuralDataSet(
			new double[][] { state }, 
			new double[][] { {0.25} }
		);
		
		final Train train = new Backpropagation(network, trainingSet, 0.4, 0.0);
		train.iteration();
		//train.iteration(50);

		double afterTraining = network.compute(new BasicNeuralData(state)).getData(0);
		
		System.out.println("Before training: " + beforeTraining + " after: " + afterTraining);
	}
}
