package torcs;

import java.util.concurrent.Exchanger;

import org.encog.neural.data.NeuralData;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogPersistedCollection;

/**
 * Neat Driver
 * @author Derk
 *
 */
public class NeatDriver extends Controller {

	private Exchanger<Object> exchanger;
	
	private BasicNetwork network;
	
	private Trainer trainer;
	
	boolean training = false;
	
	private int episode = 0;
	
	private double score;
	
	/**
	 * Gear changing constants
	 */
	private final int[] gearUp = {7500, 7500, 7500, 7500, 7500, 0};
	private final int[] gearDown = {0, 2500, 3000, 3000, 3500, 3500};
	
	public NeatDriver() throws InterruptedException {
		if (training) {
			exchanger = new Exchanger<Object>();
			
			// setup training thread
			trainer = new Trainer(exchanger);
			Thread trainingThread = new Thread(trainer);
			trainingThread.setDaemon(true);
			trainingThread.start();
			
			setUp();
		} else {
			final EncogPersistedCollection encog = new EncogPersistedCollection(
				"data/torcs/NeatNetwork.eg");
			network = (BasicNetwork) encog.find("network");
		}
	}
	
	private void setUp() {
		try {
			score = 0;
			episode++;
			network = (BasicNetwork) exchanger.exchange(null);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
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
		Action action = new Action();

		// if car is outside track game is over
		if (sensorModel.getTrackPosition() < -1 || sensorModel.getTrackPosition() > 1) {
			action.restartRace = true;
			return action;
		}
		
		if (sensorModel.getLastLapTime() != 0.0) {
			System.out.println("Lap completed!!!!" + sensorModel.getLastLapTime());
			action.restartRace = true;
			return action;
		}
		
		
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
		//System.out.println(output);
		action.steering = output.getData(0);
		
		if (sensorModel.getSpeed() < 15)
			action.accelerate = 1;
		
		score = sensorModel.getDistanceFromStartLine() / 2057.56;
		
        return action;
	}
	
	private void tearDown() {
		try {
			System.out.println(episode + ": score: " + score);
			exchanger.exchange(score);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void reset() {
		if (training) {
			tearDown();
			setUp();
		}
	}

	@Override
	public void shutdown() {
		if (training) {
			// save best network
			final EncogPersistedCollection encog = new EncogPersistedCollection(
					"data/torcs/NeatNetwork.eg");
			encog.create();
			encog.add("network", trainer.getNetwork());
		}
	}
	
	@Override
	protected void finalize() {
		System.out.println("Finalize called");
		shutdown();
	}
}
