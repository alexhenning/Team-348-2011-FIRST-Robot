package org.usfirst.frc348.auton;

import org.usfirst.frc348.JagBot;

import edu.wpi.first.wpilibj.DigitalInput;

public class Autonomous {
    protected Stage[] auton,
	              noAuton = {},
                      centerTop = {null, null, null, null, null},
	              outerTop = {null, null, null, null, null};
    protected DigitalInput lowBit, highBit;
    public int current;

    public Autonomous(JagBot bot, int lowPort, int highPort) {
	lowBit = new DigitalInput(lowPort);
	highBit = new DigitalInput(highPort);
	
	centerTop[0] = new MoveArm(bot, 1);
    	centerTop[1] = new DriveForward(bot, 20, 16.5, 0.7);
    	centerTop[2] = new PlacePiece(bot);
	centerTop[3] = new DriveBackward(bot, 8);
	centerTop[4] = new MoveArm(bot, 5);
	
    	//outerTop[0] = new ZeroArm(bot);
    	outerTop[0] = new MoveArm(bot, 2);
    	outerTop[1] = new DriveForward(bot, 20, 16.25, 0.7);
    	outerTop[2] = new PlacePiece(bot);
    	outerTop[3] = new DriveBackward(bot, 8);
    	outerTop[4] = new MoveArm(bot, 5);
    	
    	restart();
    }
    
    public void restart() {
    	auton = getAutonomousMode();
    	current = 0;
    	if (current < auton.length) {
    	auton[current].enter();
    	}
    }
    
    public Stage[] getAutonomousMode() {
	if (autonNumber() == 3) {
	    return noAuton;
	} else if (autonNumber() == 2) {
	    return centerTop;
	} else {
	    return outerTop;
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
    
    public String getAutonomousName() {
	if (autonNumber() == 3) {
	    return "No Autonomous";
	} else if (autonNumber() == 2) {
	    return "Top Center";
	} else {
	    return "Top Outer";
	}
    }

    public void periodic() {
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
