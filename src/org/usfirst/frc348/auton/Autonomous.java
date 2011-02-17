package org.usfirst.frc348.auton;

import org.usfirst.frc348.JagBot;

import edu.wpi.first.wpilibj.DigitalInput;

public class Autonomous {
    protected Stage[] auton,
	              noAuton = {},
                      centerTop = {null, null, null, null},
	              outerTop = {null, null, null, null};
    protected DigitalInput lowBit, highBit;
    protected int current;

    public Autonomous(JagBot bot, int lowPort, int highPort) {
	lowBit = new DigitalInput(lowPort);
	highBit = new DigitalInput(highPort);
	
    	centerTop[0] = new MoveArm(bot, 1);
    	centerTop[1] = new DriveForward(bot, 20, 16.25, 0.7);
    	centerTop[2] = new PlacePiece(bot);
	centerTop[3] = new DriveBackward(bot, 16);
	
    	//outerTop[0] = new ZeroArm(bot);
    	outerTop[0] = new MoveArm(bot, 2);
    	outerTop[1] = new DriveForward(bot, 20, 16.25, 0.7);
    	outerTop[2] = new PlacePiece(bot);
	outerTop[3] = new DriveBackward(bot, 16);
    	
    	restart();
    }
    
    public void restart() {
    	auton = getAutonomousMode();
    	current = 0;
    	auton[current].enter();
    }
    
    public Stage[] getAutonomousMode() {
	if (autonNumber() == 3) {
	    return centerTop;
	} else if (autonNumber() == 2 || autonNumber() == 1) {
	    return outerTop;
	} else {
	    return noAuton;
	}
    }

    public int autonNumber() {
	int mode = 0;
	if (lowBit.get()) {
	    mode += 1;
	}
	if (highBit.get()) {
	    mode += 2;
	}
	return mode;
    }

    public void periodic() {
	System.out.println("HighBit: "+highBit.get()+
			   " LowBit: "+lowBit.get());
    	if (current < auton.length) {
	    auton[current].periodic();
	    if (auton[current].isDone()) {
		auton[current].exit();
		current += 1;
		System.out.println("Moving to autonomous stage "+current);
		if (current < auton.length) {
		    auton[current].enter();
		}
	    } else if (auton[current].isError()) {
		System.out.println("Error: stopping autonomous at stage "+current);
		auton[current].exit();
		current = auton.length + 1;
	    }
    	}
    }
}
