package org.usfirst.frc348;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.SmartDashboard;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class DriveTrain {
    public CANJaguar leftJag, rightJag;
    double leftPrevOut, rightPrevOut;
    long prevTime;
    double prevAngle;
    public double turnSpeed;
    
    public DriveTrain(int leftID, int rightID) {
	leftJag = Utils.getJaguar(leftID);
	rightJag = Utils.getJaguar(rightID);
	
	try {
	    leftJag.configEncoderCodesPerRev(540);
	    leftJag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
	    leftJag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
	    
	    rightJag.configEncoderCodesPerRev(-540);
	    rightJag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
	    rightJag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);

	} catch (CANTimeoutException e) {
	    e.printStackTrace();
	}
	
	
	leftPrevOut = 0;
	rightPrevOut = 0;
	prevTime = System.currentTimeMillis();
	prevAngle = 0;
    }
    
    public double getMaxSpeed() throws CANTimeoutException {
	/*
	  `maxSpeed' is in rotations per second
	  `returns' uses fancy math to convert it to feet per second
	*/
	double maxSpeed = Math.max(leftJag.getSpeed(), rightJag.getSpeed());
	return convertEncoderReading(maxSpeed);
    }

    public double getLeftDist() throws CANTimeoutException {
	return rotationsToDist(leftJag.getPosition());
    }

    public double getRightDist() throws CANTimeoutException {
	return rotationsToDist(leftJag.getPosition());
    }
    
    public double rotationsToDist(double reading) {
	return reading * Math.PI * (2.0/3.0);
    }
    
    public double convertEncoderReading(double reading) {
	return reading * Math.PI * (2.0/3.0) / 60.0;
    }

    public void turn180(Gyro gyro) throws CANTimeoutException {
	// while (!isStopped()) {
	//     stop();
	//     System.out.println("Stopping.");
	// }
	int cnt = 0;
	double error = 0, accumError = 0, prevError = 1, derror;
	double P = .48, I = 0, D = 0;
	    
	gyro.reset();
	try {
	    leftJag.setX(-1);
	    rightJag.setX(1);
	} catch (CANTimeoutException e) { e.printStackTrace(); }
	double stopTime = System.currentTimeMillis() + 3000;
	    
	// while (Math.abs(gyro.getAngle() - 180) > 1) {
	while (System.currentTimeMillis() < stopTime) {
	    error = (180 - gyro.getAngle()) / 180;
	    accumError += error;
	    derror = prevError - error;
	    prevError = error;
	    
	    if (cnt%10 == 0) {
		System.out.println("Error: "+error+" AccumError: "+accumError+" dError: "+derror);
	    } cnt++;
	    SmartDashboard.log(gyro.getAngle(), "Gyro");
	    
	    try {
		leftJag.setX( -(P * error + I * accumError + D * derror));
		rightJag.setX((P * error + I * accumError + D * derror));
	    } catch (CANTimeoutException e) { e.printStackTrace(); }
	}
    }

    public boolean isStopped() throws CANTimeoutException {
	return (leftJag.getSpeed() != 0 &&
		rightJag.getSpeed() != 0);
    }
    
    public void stop() throws CANTimeoutException {
	if (leftJag.getSpeed() > 20 &&
	    rightJag.getSpeed() > 20) {
	    leftJag.setX(-.2);
	    rightJag.setX(-.2);
	} else
	    leftJag.setX(0);
	    rightJag.setX(0);
	}
    
    public void drive(double left, double right, Gyro gyro) throws CANTimeoutException {
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
		    left = right + ((left - right) / 2) - 0.05;
		} else {
		    right = left + ((right - left) / 2) - 0.05;
		}
	    } else if (left < 0 && right < 0) {
		if (left > right) {
		    right = left - ((left - right) / 2) - 0.05;
		} else {
		    left = right - ((right - left) / 2) - 0.05;
		}
	    } else {
		// Turn in place
		left *= .72;
		right *= .72;
	    }
	}
	
	long time = System.currentTimeMillis();
	double angle = gyro.getAngle();
	turnSpeed = (angle - prevAngle) / ((double) ((time - prevTime))) * 1000;
	prevTime = time;
	prevAngle = angle;
	
	leftJag.setX( left );
	rightJag.setX( right );
    }
    
    public void updateDashboard() {
    	try {
	    SmartDashboard.log(convertEncoderReading(leftJag.getSpeed()), "Left Speed");
	    SmartDashboard.log(convertEncoderReading(rightJag.getSpeed()), "Right Speed");
	} catch (CANTimeoutException e) { e.printStackTrace(); }
    }
}
