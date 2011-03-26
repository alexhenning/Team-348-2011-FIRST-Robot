package org.usfirst.frc348;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException;

public class BreakoutBox {
	protected DriverStation ds;
	protected int armPos = 3;
	protected boolean openGrabber, closeGrabber, armControlMode, releaseMinibot;
		
	public BreakoutBox() {
		ds = DriverStation.getInstance();
	}
	
	public void update() throws EnhancedIOException {
		 if (!ds.getDigitalIn(1)) {
			armPos = 1;
		 } else if (!ds.getDigitalIn(2)) {
			armPos = 2;
		 } else if (!ds.getDigitalIn(3)) {
			armPos = 4;
		 } else if (!ds.getDigitalIn(4)) {
			armPos = 5;
		 } else {
			armPos = 3;
		 }
		 
		 armControlMode = !ds.getDigitalIn(6);
		 releaseMinibot = ds.getDigitalIn(8);
		 
		 openGrabber = !ds.getDigitalIn(7);
		 closeGrabber = !ds.getDigitalIn(5);
	}

	public void debugPrint() {
		System.out.println("Arm position: "+armPos+" Open? "+openGrabber+" Close? "+closeGrabber+
				" Minibot enabled? "+armControlMode+" Release? "+releaseMinibot);
	}
}