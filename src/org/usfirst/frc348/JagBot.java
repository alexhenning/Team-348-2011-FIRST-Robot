package org.usfirst.frc348;

import org.usfirst.frc348.auton.Autonomous;

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
    public BreakoutBox breakout;
    public Joystick leftJoy, rightJoy;
    public DriveTrain dt;
    public Arm arm;
    public Gyro gyro;
    public Autonomous auton;
    
    public JagBot() throws CANTimeoutException {
	System.out.println(SIG+"Creating JagBot");
	breakout = new BreakoutBox();
	leftJoy = new Joystick(1);
	rightJoy = new Joystick(2);
	
	dt = new DriveTrain(3, 4);
	arm = new Arm(2, 1, 1, 2);
	gyro = new Gyro(2);
	auton = new Autonomous(this);
    }
            
    public void autonomousPeriodic() {
    	auton.periodic();
	arm.periodic();
	updateDashboard();
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
