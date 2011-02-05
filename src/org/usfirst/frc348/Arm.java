package org.usfirst.frc348;

import edu.wpi.first.wpilibj.CANJaguar;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

public class Arm {
    public CANJaguar jag;
    protected Servo servo;
    protected DigitalInput limit;

    // Position to move the arm to based off of the controller
    protected static int positions[] = {0, 5, 4, 3, 2, 1};

    public Arm(int jagID, int servoPort, int limitPort) throws CANTimeoutException {
    	jag = Utils.getJaguar(jagID);
    	jag.configEncoderCodesPerRev(360);
    	jag.setSpeedReference(CANJaguar.SpeedReference.kQuadEncoder);
    	jag.setPositionReference(CANJaguar.PositionReference.kQuadEncoder);
    	// jag.setPID(.4, .0005, 20);
    	// jag.changeControlMode(CANJaguar.ControlMode.kPosition)
    	
    	servo = new Servo(servoPort);
    	limit = new DigitalInput(limitPort);
    }


}
