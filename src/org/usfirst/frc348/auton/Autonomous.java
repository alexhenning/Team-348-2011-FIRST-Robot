package org.usfirst.frc348.auton;

import org.usfirst.frc348.JagBot;

public class Autonomous {
    protected Stage[] auton, centerTop, outerTop;
    protected int current;

    public Autonomous(JagBot bot) {
    	outerTop[0] = new ZeroArm(bot);
    	outerTop[1] = new DriveForward(bot, 19.5, 0.4);
    	outerTop[2] = new MoveArm(bot, 2);
    	outerTop[3] = new PlacePiece(bot);
    	
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
		if (current < auton.length) {
		    auton[current].enter();
		}
	    } else if (auton[current].isError()) {
		System.out.println("Error: stopping autonomoust at stage "+current);
		auton[current].exit();
		current = auton.length + 1;
	    }
    	}
    }
}
