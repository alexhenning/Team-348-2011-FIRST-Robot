package org.usfirst.frc348.auton;

import org.usfirst.frc348.JagBot;

import edu.wpi.first.wpilibj.DigitalInput;

public class Autonomous {
    protected Stage[] auton,
	              centerTop = {null, null, null},
                      outerTop = {null, null, null};
    protected DigitalInput lowBit, highBit;
    protected int current;

    public Autonomous(JagBot bot, int lowPort, int highPort) {
	lowBit = new DigitalInput(lowPort);
	highBit = new DigitalInput(highPort);
	
    	centerTop[0] = new MoveArm(bot, 1);
    	centerTop[1] = new DriveForward(bot, 20, 16.25, 0.7);
    	centerTop[2] = new PlacePiece(bot);
	
    	//outerTop[0] = new ZeroArm(bot);
    	outerTop[0] = new MoveArm(bot, 2);
    	outerTop[1] = new DriveForward(bot, 20, 16.25, 0.7);
    	outerTop[2] = new PlacePiece(bot);
    	
    	restart();
    }
    
    public void restart() {
    	auton = getAutonomousMode();
    	current = 0;
    	auton[current].enter();
    }
    
    public Stage[] getAutonomousMode() {
    	return outerTop;
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
