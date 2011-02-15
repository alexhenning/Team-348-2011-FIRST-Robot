/**
 * DriveForward: An autonomous stage that drives forward a fixed distance.
 */

package org.usfirst.frc348.auton;

import org.usfirst.frc348.JagBot;

import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class DriveForward implements Stage {
    protected JagBot bot;
    protected boolean error = false;
    protected double distance, gain;
    protected double leftStart, rightStart, leftDist, rightDist;
    
    public DriveForward(JagBot bot, double feet, double gain) {
	this.bot = bot;
	this.distance = feet;
	this.gain = gain;
    }
    
    public void enter() {
	try {
	    leftStart = bot.dt.getLeftDist();
	    rightStart = bot.dt.getRightDist();
	} catch (CANTimeoutException e) {
	    e.printStackTrace();
	    error = true;
	}
    }
    
    public void periodic() {
	try {
	    leftDist = bot.dt.getLeftDist() - leftStart;
	    rightDist = bot.dt.getRightDist() - rightStart;

	    double left = gain * ((distance - leftDist) / distance);
	    double right = gain * ((distance - rightDist) / distance);
	    
	    bot.dt.drive(left, right, bot.gyro);
	} catch (CANTimeoutException e) { 
	    e.printStackTrace();
	    error = true;
	}
    }
    
    public void exit() {
	// No cleaning up
    }
    
    public boolean isDone() {
	return ((leftDist + rightDist) / 2) > distance;
    }
    
    public boolean isError() {
	return error;
    }
    
}
