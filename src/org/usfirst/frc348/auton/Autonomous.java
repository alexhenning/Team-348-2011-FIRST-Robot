package org.usfirst.frc348.auton;

import org.usfirst.frc348.JagBot;

public class Autonomous {
    protected Stage[] auton,
	              centerTop = {null, null, null, null},
                      outerTop = {null, null, null};
    protected int current;

    public Autonomous(JagBot bot) {
    	//outerTop[0] = new ZeroArm(bot);
    	outerTop[0] = new MoveArm(bot, 2);
    	outerTop[1] = new DriveForward(bot, 18, 16.25, 0.7);
    	outerTop[2] = new PlacePiece(bot);
    	
    	auton = getAutonomousMode();
    	current = 0;
    	auton[current].enter();
    }
    
    public Stage[] getAutonomousMode() {
    	return outerTop;
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
