package org.usfirst.frc348;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.ServerSocketConnection;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.UDPDatagramConnection;
import edu.wpi.first.wpilibj.Dashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException;

public class BreakoutBox {
	protected DriverStationEnhancedIO bb;
	protected int armPos = 3;
	protected boolean openGrabber, closeGrabber, enableMinibot, releaseMinibot;
		
	public BreakoutBox() {
		bb = DriverStation.getInstance().getEnhancedIO();		
	}
	
	public void update() throws EnhancedIOException {
		 if (bb.getDigital(1)) {
			armPos = 1;
		 } else if (bb.getDigital(2)) {
			armPos = 2;
		 } else if (bb.getDigital(3)) {
			armPos = 4;
		 } else if (bb.getDigital(4)) {
			armPos = 5;
		 } else {
			armPos = 3;
		 }
		 
		 enableMinibot = bb.getDigital(5);
		 releaseMinibot = bb.getDigital(6);
		 
		 openGrabber = bb.getDigital(7);
		 closeGrabber = bb.getDigital(8);
	}

	public int getArmPosition() {
		return armPos;
	}

	
	
	public boolean canReleaseMinibot() {
		return enableMinibot;
	}
	
	public boolean releaseMinibotp() {
		return releaseMinibot;
	}

	public void debugPrint() {
		System.out.println("Arm position: "+armPos+" Open? "+openGrabber+" Close? "+closeGrabber+
				" Minibot enabled? "+enableMinibot+" Release? "+releaseMinibot);
	}
}