package torcs;

import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;


public class QDriver extends Controller {

	/**
	 * Gear changing constants
	 */
	private final int[] gearUp = {7500, 7500, 7500, 7500, 7500, 0};
	private final int[] gearDown = {0, 2500, 3000, 3000, 3500, 3500};
	
	private BasicNetwork network;
	
	private int step = 0;
	
	public QDriver() {
		network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, 7));
		network.addLayer(new BasicLayer(new ActivationLinear(), true, 5));
		network.addLayer(new BasicLayer(new ActivationLinear(), true, 1));
		network.getStructure().finalizeStructure();
		network.reset();
	}
	
	/**
	 * Calculate new gear value depending on current gear and rotations
	 * per minute of car engine
	 * @param gear
	 * @param rpm
	 * @return New gear
	 */
	private int getGear(int gear, double rpm){
	    
	    // if gear is 0 (Neutral) or -1 (Rewind) just return 1 
	    if (gear < 1)
	        return 1;
	    
	    // check if the RPM value of car is greater than the one suggested 
	    // to shift up the gear from the current one     
	    if (gear < 6 && rpm >= gearUp[gear - 1]) {
	        return gear + 1;
	    } else {
	    	// check if the RPM value of car is lower than the one suggested 
	    	// to shift down the gear from the current one
	        if (gear > 1 && rpm <= gearDown[gear-1])
	            return gear - 1;
	    }
	    
	    // Otherwise keep current gear
        return gear;
	}
	
	@Override
	public Action control(SensorModel sensorModel) {
		if (step > 0)
			observeResultingState(sensorModel);
		
		step++;
		Action action = new Action();
		action.gear = getGear(sensorModel.getGear(), sensorModel.getRPM());

		double[] sensors = sensorModel.getTrackEdgeSensors();
		double frontSensor = Math.max(Math.max(sensors[8], sensors[10]), sensors[9]);
		
		NeuralData input = new BasicNeuralData(new double[] {
			sensors[0] / 200,
			sensors[3] / 200,
			sensors[6] / 200,
			frontSensor / 200,
			sensors[12] / 200,
			sensors[15] / 200,
			sensors[18] / 200
		});
		
		NeuralData output = network.compute(input);
		action.steering = output.getData(1) - output.getData(0);
		
		if (sensorModel.getSpeed() < 10)
			action.accelerate = 1;
		
        return action;
	}

	private void observeResultingState(SensorModel sensorModel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() {
		step = 0;

		System.out.println("Driver says: Race restarted");
	}

	@Override
	public void shutdown() {
		System.out.println("Driver says: Race abandoned");
	}
}
