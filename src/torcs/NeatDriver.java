package torcs;

import java.util.concurrent.Exchanger;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;


public class NeatDriver extends Controller {

	private Exchanger<Object> exchanger;
	
	private BasicNetwork network;
	
	private double score;
	/**
	 * Gear changing constants
	 */
	private final int[] gearUp = {7500, 7500, 7500, 7500, 7500, 0};
	private final int[] gearDown = {0, 2500, 3000, 3000, 3500, 3500};
	
	public NeatDriver() {
		exchanger = new Exchanger<Object>();
		
		// setup training thread
		Thread trainingThread = new Thread(new Trainer(exchanger));
		trainingThread.start();
		
		setUp();
	}
	
	private void setUp() {
		try {
			score = 0;
			network = (BasicNetwork) exchanger.exchange(null);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		score = sensorModel.getDistanceRaced();
		Action action = new Action();
		
		// TODO by collision give penalty and go to restore mode
		if (sensorModel.getTrackPosition() < -1 || sensorModel.getTrackPosition() > 1) {
			score -= 1000; // penalty
			action.restartRace = true;
			return action;
		}
		
		
		action.gear = getGear(sensorModel.getGear(), sensorModel.getRPM());
		
		double[] sensors = sensorModel.getTrackEdgeSensors();
		double frontSensor = Math.max(Math.max(sensors[8], sensors[10]), sensors[9]);
		
		NeuralData input = new BasicNeuralData(new double[] {
			//sensors[0],
			sensors[3],
			//sensors[6],
			frontSensor,
			//sensors[12],
			sensors[15],
			//sensors[18]
		});
		
		NeuralData output = network.compute(input);
		//System.out.println(output);
		action.steering = output.getData(0);
		
		if (sensorModel.getSpeed() < 20)
			action.accelerate = 1;

        return action;
	}
	
	private void tearDown() {
		try {
			System.out.println("Score: " + score);
			exchanger.exchange(score);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void reset() {
		tearDown();
		setUp();
	}

	@Override
	public void shutdown() {
		tearDown();
		System.out.println("Driver says: Race abandoned");
	}
}
