package torcs;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.CalculateScore;


public class QDriver extends Controller {

	/**
	 * Gear changing constants
	 */
	private final int[] gearUp = {7500, 7500, 7500, 7500, 7500, 0};
	private final int[] gearDown = {0, 2500, 3000, 3000, 3500, 3500};
	
	private double distance = 0;
	
	public QDriver() { // BasisNetwork network
	}
	
	public void setNetwork(BasicNetwork network) {
		
	}
	
	public void getNetwork() {
		
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
	
	private double getAcceleration(double speed, double targetSpeed) {
		double acceleration = 0;
		
		double difference = targetSpeed - speed;
		
		if (difference > 20) {
			acceleration = 1;
		} else if (difference >= 0 && difference <= 20) {
			acceleration = difference / 20;
		} else if (difference <= 0 && difference >= -20) {
			acceleration = -1 * (difference / 20);
		} else {
			acceleration = -1;
		}
		
		return acceleration;
	}
	
	private double getTargetSpeed(double[] sensors) {
		if (sensors[9] >= 100)
			return 250;
		
		return 50 + sensors[9] / 10;
	}
	
	private double getSteering(double angleToTrackAxis) {
		double steering = 0;
		if (angleToTrackAxis < 0) {
            steering = -0.2;
        }
        else {
            steering = 0.2;
        }
		
		return steering;
	}
	
	@Override
	public Action control(SensorModel sensorModel) {
		Action action = new Action();
		
		action.gear = getGear(sensorModel.getGear(), sensorModel.getRPM());

        if (sensorModel.getSpeed () < 20) {
            action.accelerate = 1;
        }
        if (sensorModel.getAngleToTrackAxis() < 0) {
            action.steering = -0.1;
        }
        else {
            action.steering = 0.1;
        }
        action.gear = 1;
        distance = distance + 1.0;
        System.err.println(distance);
        return action;
	}

	@Override
	public void reset() {
		// score is distance raced
		//trainer.receiveScore(score);
		//trainer.getNextNetwork();
		System.out.println("Driver says: Race restarted");
	}

	@Override
	public void shutdown() {
		System.out.println("Driver says: Race abandoned");
	}
}
