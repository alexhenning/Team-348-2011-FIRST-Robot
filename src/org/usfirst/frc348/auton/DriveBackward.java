/**
 * DriveBackward: An autonomous stage that drives forward a fixed distance.
 */

package org.usfirst.frc348.auton;

import org.usfirst.frc348.JagBot;

import edu.wpi.first.wpilibj.SmartDashboard;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class DriveBackward implements Stage {
    protected JagBot bot;
    protected boolean error = false;
    protected double distance;
    protected double leftStart, rightStart, leftDist, rightDist;
    
    public DriveBackward(JagBot bot, double feet) {
	this.bot = bot;
	this.distance = -feet;
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
	    
	    SmartDashboard.log(leftDist, "Left Distance");
	    SmartDashboard.log(rightDist, "Right Distance");

	    double left = -1 * ((distance - leftDist) / distance);
	    double right = -1 * ((distance - rightDist) / distance);
	    
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
	return ((leftDist + rightDist) / 2) < distance;
    }
    
    public boolean isError() {
	return error;
    }
    
}
