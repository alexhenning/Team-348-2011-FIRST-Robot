/**
 * PlacePiece: An autonomous stage that places a piece on the peg.
 */

package org.usfirst.frc348.auton;

import org.usfirst.frc348.JagBot;

import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class PlacePiece implements Stage {
    protected JagBot bot;
    protected boolean error = false;
    protected int timer;
    
    public PlacePiece(JagBot bot) {
	this.bot = bot;
    }
    
    public void enter() {
	timer = 0;
    }
    
    public void periodic() {
	timer += 1;
	bot.arm.placePiece();
	if (timer > 20) {
	    try {
		bot.dt.drive(-.25, -.25, bot.gyro);
	    } catch (CANTimeoutException e) {
		e.printStackTrace();
		error = true;
	    }
	}
    }
    
    public void exit() {
	// No cleaning up
    }
    
    public boolean isDone() {
	return timer > 80;
    }
    
    public boolean isError() {
	return error;
    }
    
}
