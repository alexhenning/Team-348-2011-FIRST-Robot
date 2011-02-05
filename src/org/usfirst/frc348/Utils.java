package org.usfirst.frc348;

import edu.wpi.first.wpilibj.CANJaguar;

public class Utils {
	public static CANJaguar getJaguar(int id) {
		CANJaguar jag = null;
		while (jag == null) {
			try {
				System.out.println("Trying to connect to CAN port "+id+"...");
				jag = new CANJaguar(id);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) { e.printStackTrace();	}
		}
		System.out.println("Connected to CAN port "+id);
		return jag;
	}
}
