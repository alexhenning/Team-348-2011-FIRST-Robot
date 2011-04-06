package org.usfirst.frc348;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.SmartDashboard;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class DriveTrain {
    protected CANJaguar leftJag, rightJag;
    
    public DriveTrain(int leftID, int rightID) {
	leftJag = Utils.getJaguar(leftID);
	rightJag = Utils.getJaguar(rightID);
	
	try {
	    leftJag.configEncoderCodesPerRev(540);
	    leftJag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
	    leftJag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
	    
	    rightJag.configEncoderCodesPerRev(540);
	    rightJag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
	    rightJag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
	} catch (CANTimeoutException e) {
	    e.printStackTrace();
	}
    }
    
    public double getLeftDist() throws CANTimeoutException {
	return rotationsToDist(leftJag.getPosition());
    }
    public double getLeftSpeed() throws CANTimeoutException {
	return convertEncoderReading(leftJag.getSpeed());
    }

    public double getRightDist() throws CANTimeoutException {
	return -rotationsToDist(rightJag.getPosition());
    }
    public double getRightSpeed() throws CANTimeoutException {
	return -convertEncoderReading(rightJag.getSpeed());
    }
    
    public double rotationsToDist(double reading) {
	return reading * Math.PI * (2.0/3.0);
    }
    public double convertEncoderReading(double reading) {
	return reading * Math.PI * (2.0/3.0) / 60.0;
    }

    public void drive(double left, double right) throws CANTimeoutException {
	if (Math.abs(left-right) <= .1) {
	    // Compensate and Drive straight
	    if (left >= 0) {
		left = Math.max(left, right);
		right = left;
	    } else {
		left = Math.min(left, right);
		right = left;
	    }
	} else {
	    // Make turning more controllable
	    if (left > 0 && right > 0) {
		if (left > right) {
		    left = right + 3 * ((left - right) / 4) - 0.05;
		} else {
		    right = left + 3 * ((right - left) / 4) - 0.05;
		}
	    } else if (left < 0 && right < 0) {
		if (left > right) {
		    right = left - 3 * ((left - right) / 4) + 0.05;
		} else {
		    left = right - 3 * ((right - left) / 4) + 0.05;
		}
	    } else {
		// Turn in place
		left *= .8;
		right *= .8;
	    }
	}
	
	leftJag.setX( left );
	rightJag.setX( right );
    }
    
    public void updateDashboard() {
    	try {
	    SmartDashboard.log(Math.max(Math.abs(getLeftSpeed()),
					Math.abs(getRightSpeed())),
			       "Speed");
	} catch (CANTimeoutException e) { e.printStackTrace(); }
    }
}
