package org.usfirst.frc348;

import edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SmartDashboard;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class JagBot extends IterativeRobot {
    private String SIG = "[JagBot] ";
    boolean DEBUG = true; int debugCounter = 0;
    boolean MANUAL_ARM_CONTROL = true;
    protected BreakoutBox breakout;
    protected Joystick leftJoy, rightJoy;
    protected DriveTrain dt;
    protected Arm arm;
    protected Gyro gyro;
    
    public JagBot() throws CANTimeoutException {
	System.out.println(SIG+"Creating JagBot");
	breakout = new BreakoutBox();
	leftJoy = new Joystick(1);
	rightJoy = new Joystick(2);
	
	dt = new DriveTrain(3, 4);
	arm = new Arm(2, 1, 1, 2);
	gyro = new Gyro(2);
    }
    
    int stage = 0; double leftEncoder, rightEncoder;
    public void autonomousInit() {
    	stage = 0;
    	leftEncoder = 0;
	rightEncoder = 0;
	arm.setManualMode();
    }
    
    public void autonomousPeriodic() {
    	try {
	    if (stage == 0) {
		arm.manualMove(5);
		arm.close();
		if (arm.atBottom()) { // Success
		    stage += 1;
		    arm.setMagicMode();
		}
	    } else if (stage == 1) {	    	
		arm.moveToPosition(2);
		arm.close();
		try {
			Thread.sleep(25);
		} catch (InterruptedException e) { e.printStackTrace();	}
		if (arm.pid.getError() < 0.3) { // Success
		    stage += 1;
		}
	    } else if (stage == 2) {
		arm.moveToPosition(2);
		arm.close();
		dt.drive(0.2, 0.2, gyro);
		if (arm.atPole()) { // Success
		    stage += 1;
		}
	    } else if (stage == 3) {
		arm.open();
	    }
    	} catch (CANTimeoutException e1) { e1.printStackTrace(); }

    	updateDashboard();
	arm.periodic();
    }
    
    public void teleopPeriodic() {
    	try {
	    breakout.update();
	} catch (EnhancedIOException e2) { e2.printStackTrace(); }
    	
	// Joystick values are backwards
	double left = -leftJoy.getY();
	double right = -rightJoy.getY();
	try {
	    if (leftJoy.getTrigger() || rightJoy.getTrigger()) {
		dt.turn180(gyro);
	    } else {
		dt.drive(left, right, gyro);
	    }
	} catch (CANTimeoutException e1) { e1.printStackTrace(); }

	if (breakout.closeGrabber || breakout.openGrabber) {
	    arm.open();
	} else {
	    arm.close();
	}
	
	MANUAL_ARM_CONTROL = breakout.enableMinibot;
	try {
	    if (!MANUAL_ARM_CONTROL) {
		arm.setMagicMode();
		if (breakout.openGrabber) {
		    arm.placePiece();
		} else {
		    arm.moveToPosition(breakout.armPos);
		}
	    } else {
		arm.setManualMode();
		arm.manualMove(breakout.armPos);
	    }
	} catch (CANTimeoutException e) { e.printStackTrace(); }

	arm.periodic();
	updateDashboard();
	// debug();
    }

    public void updateDashboard() {
	dt.updateDashboard();
	arm.updateDashboard();
	SmartDashboard.log(gyro.getAngle(), "Gyro");
    }
    
    public void debug() {
	debugCounter += 1;
	if (DEBUG && debugCounter%10 == 0) {
		
	    try {
		// System.out.println("Speed: "+dt.getSpeed()+" Gyro: "+gyro.getAngle()+" Turn Rate: "+dt.turnSpeed);
		// System.out.println("Left Currents: "+dt.leftJag.getOutputCurrent()+
		// 				"--- Right Current: "+dt.rightJag.getOutputCurrent()+"/");
		System.out.println("Left Encoder: "+dt.leftJag.getPosition()+"/"+dt.leftJag.getSpeed()+
				   " --- Right Encoder: "+dt.rightJag.getPosition()+"/"+dt.rightJag.getSpeed());
	    } catch (Exception e) {
		e.printStackTrace();
	    }			
	}
    }
}
