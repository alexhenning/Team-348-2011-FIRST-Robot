/**
 * MoveArm: An autonomous stage that moves the arm to a specific location
 */

package org.usfirst.frc348.auton;

import org.usfirst.frc348.JagBot;

import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class MoveArm implements Stage {
    protected JagBot bot;
    protected boolean error = false;
    protected int position;
    
    public MoveArm(JagBot bot, int position) {
	this.bot = bot;
	this.position = position;
    }
    
    public void enter() {
	bot.arm.setMagicMode();
    }
    
    public void periodic() {
	try {
	    bot.arm.moveToPosition(position);
	} catch (CANTimeoutException e) {
	    e.printStackTrace();
	    error = true;
	}
    }
    
    public void exit() {
	// No cleaning up
    }
    
    public boolean isDone() {
	return bot.arm.pid.getError() < 0.1;
    }
    
    public boolean isError() {
	return error;
    }
    
}
