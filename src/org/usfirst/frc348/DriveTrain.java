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
	    leftJag.configEncoderCodesPerRev(-540);
	    leftJag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
	    leftJag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
//	    leftJag.setVoltageRampRate(18);
	    
	    rightJag.configEncoderCodesPerRev(540);
	    rightJag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
	    rightJag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
//	    rightJag.setVoltageRampRate(18);
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

    public double convertEncoderReading(double reading) {
	return reading * Math.PI * (2.0/3.0) / 60.0;
    }
    
    int cnt = 0; boolean running = false; double target; 
    double error = 0, accumError = 0, prevError = 1, derror;
    double P = .415, I = .00048, D = 11.5;	    
    public void turn180(Gyro gyro) throws CANTimeoutException {
	if (!isStopped()) {
	    stop();
	    gyro.reset();
	} else {
	    if (!running) {
		target = gyro.getAngle() + 180;
		accumError = 0;
		prevError = 1;
	    } else {
		running = true;
	    }
	    
	    error = (target - gyro.getAngle()) / 180;
	    accumError += error;
	    derror = prevError - error;
	    prevError = error;
	    
	    if (cnt%10 == 0) {
		System.out.println("Error: "+error+" AccumError: "+accumError+" dError: "+derror);
	    } cnt++;
	    
	    try {
		leftJag.setX(   P * error + I * accumError + D * derror);
		rightJag.setX(-(P * error + I * accumError + D * derror));
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
		    left = right + (left - right) / 2;
		} else {
		    right = left + (right - left) / 2;
		}
	    } else if (left < 0 && right < 0) {
		if (left > right) {
		    right = left - (left - right) / 2;
		} else {
		    left = right - (right - left) / 2;
		}
	    } else {
		// Turn in place
		left *= .75;
		right *= .75;
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
//	    SmartDashboard.log(convertEncoderReading(leftJag.getSpeed()), "Left Speed");
//	    SmartDashboard.log(convertEncoderReading(rightJag.getSpeed()), "Right Speed");
//	    SmartDashboard.log(leftJag.getOutputVoltage(), "Left Voltage");
	    SmartDashboard.log(leftJag.getOutputCurrent(), "Left Current");
//	    SmartDashboard.log(rightJag.getOutputVoltage(), "Right Voltage");
	    SmartDashboard.log(rightJag.getOutputCurrent(), "Right Current");
	} catch (CANTimeoutException e) { e.printStackTrace(); }
    }
}
