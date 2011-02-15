package org.usfirst.frc348.auton;

public interface Stage {
	public void enter(); // Initialize entering this stage 
	public void periodic(); // Run this stage
	public void exit(); // Exit this stage
	
	public boolean isDone(); // Is this stage done
	public boolean isError(); // It there an error
}
