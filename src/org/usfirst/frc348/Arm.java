package org.usfirst.frc348;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SmartDashboard;
import edu.wpi.first.wpilibj.can.CANTimeoutException;
import edu.wpi.first.wpilibj.PIDController;

public class Arm implements PIDSource {
    public CANJaguar jag;
    protected Servo servo;
    protected DigitalInput limit, banner;
    protected PIDController pid;
    protected double target;
    protected boolean magicMode = true, placing = false;

    // Position to move the arm to based off of the controller
    protected static double positions[] = {-1, 1.82, 1.660, 0.915, 0.700, 0};

    public Arm(int jagID, int servoPort, int limitPort, int bannerPort)
    										throws CANTimeoutException {
    	jag = Utils.getJaguar(jagID);
    	jag.configEncoderCodesPerRev(360);
    	jag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
    	jag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
    	jag.configSoftPositionLimits(0, 1.8);
    	jag.enableControl(0);
    	jag.enableControl(0); // Jaguar bug nonsense

    	servo = new Servo(servoPort);
    	limit = new DigitalInput(limitPort);
    	banner = new DigitalInput(bannerPort);
    	
    	pid = new PIDController(-4, 0, 0, this, jag);
    	pid.setInputRange(0, 1.8);
    	pid.enable();
    }

    public void setManualMode() {
	if (magicMode) {
	    magicMode = false;
	    pid.disable();
	    try {
		jag.disableSoftPositionLimits();
	    } catch (CANTimeoutException e) { e.printStackTrace(); }
	}
    }

    public void setMagicMode() {
	if (!magicMode) {
	    magicMode = true;
	    pid.enable();
	    try {
		jag.configSoftPositionLimits(0, 1.8);
	    } catch (CANTimeoutException e) { e.printStackTrace(); }
	}
    }

    public void manualMove(int pos) throws CANTimeoutException {
	double out = 0;
	if (pos == 1) {
	    out = -1;
	} else if (pos == 2) {
	    out = -.5;
	} else if (pos == 4) {
	    out = .25;
	} else if (pos == 5) {
	    out = .75;
	}
	jag.setX(out);
    }

    public void moveToPosition(int pos) throws CANTimeoutException {
//    	SmartDashboard.log(pos, "Target Position");
	placing = false;
	pid.setSetpoint(positions[pos]);
    }

    public void placePiece() {
	if (!placing) {
	    placing = true;
	    open();
	    pid.setSetpoint(pid.getSetpoint() - 0.3);
	}
    }

    public void open() {
	servo.set(.2);
    }

    public void close() {
	servo.set(1);
    }

    public void updateDashboard() {
    	try {
    		System.out.println("Limit: "+limit.get()+" Banner: "+banner.get());
			SmartDashboard.log(jag.getPosition(), "Arm");
		    SmartDashboard.log(1.0 - servo.getPosition(), "Grabber");
		    SmartDashboard.log(limit.get(), "Arm Limit");
		    SmartDashboard.log(banner.get(), "Banner");
//		    SmartDashboard.log(pid.getError(), "PID Error");		    
    	} catch (CANTimeoutException e) { e.printStackTrace(); }

    }

    public double pidGet() {
			try {
				return jag.getPosition();
			} catch (CANTimeoutException e) { e.printStackTrace(); return 0; }
	}
}
