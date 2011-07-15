package org.usfirst.frc348;

// Imports, just standard boilerplate
import org.usfirst.frc348.auton.Autonomous;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SmartDashboard;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.can.CANTimeoutException;

/*
  JagBot: The class that represents the robot and contains it's
  various subsystems. It implements it's primary control structure.
 */
public class JagBot extends IterativeRobot {
    // A signature to be printed when debugging so that text from the
    //     robot can be differentiated from the CRIO output.
    private String SIG = "[JagBot] ";

    // Whether or not to print debug information
    boolean DEBUG = true; int debugCounter = 0;

    // A handle for the breakout box aka the secondary driver controls
    //    and handles input from the secondary driver for control of
    //    the arm and minibot deployment
    public BreakoutBox breakout;

    // A handle for the left and right joysticks that allows reading
    // their values for use as the primary driver controls.
    public Joystick leftJoy, rightJoy;

    // A handle for controlling the drive train 
    public DriveTrain dt;

    // A handle for controlling th arm
    public Arm arm;

    // A handle for controlling the minibot deployment
    protected Deployment deployment;

    // The interface to the autonomous which is contained in the
    // `auton' directory.
    public Autonomous auton;

    // A handle for the Axis Camera which sends video to the dashboard
    public AxisCamera ac;

    /*
      The constructor for the Robot, throws CANTimeoutException when
      CAN acts up. In that case, CAN is not configured properly and
      needs to be fixed. In some instances, the rebooting the robot
      may be enough.
    */
    public JagBot() throws CANTimeoutException {
	// Print to the netconsole that the robot is being initiated
	// and display on the driver station that the robot is
	// initializing.
	System.out.println(SIG+"Creating JagBot");
	DriverStationLCD.getInstance().println(DriverStationLCD.Line.kMain6, 1, 
		"Not Ready --- Initializing...");
	DriverStationLCD.getInstance().updateLCD();

	// Initiate the controls, the joystick enumerated as 1 by the
	// driver station software should control the left side of the
	// robot and that the joystick enumerated as 2 should control
	// the left side of the robot.
	breakout = new BreakoutBox();
	leftJoy = new Joystick(1);
	rightJoy = new Joystick(2);

	// Initiate the various subsystems with appropriate
	// bindings. Look at the each of the classes to see what the
	// numbers represent. If you ever do this again, use named
	// constants to make this more readable.
	dt = new DriveTrain(3, 4);
	arm = new Arm(2, 1, 1, 2);
	deployment = new Deployment(2, 0.2);
	auton = new Autonomous(this, 4, 3);

	// Send camera video and dashboard data to the control system
	ac = AxisCamera.getInstance();
	updateDashboard();
    }

    /*
      This function will be called at the very beginning of the
      autonomous mode.
    */
    public void autonomousInit() {
	// Reset autonomous mode, this allows it to be run multiple
	// times without having to restart the robot for testing
	// purposes.
    	auton.restart();
	
	// Make sure the minibot doesn't deploy during autonomous
	deployment.disable();
    }

    /*
      This function is repeatedly called as long as it's still
      autonomous mode and the robot is enabled. The entire loop should
      run fast so that the robot is responsive to disabling. Any long
      running tasks should be implemented as a state machine, doing a
      little bit each loop.
     */
    public void autonomousPeriodic() {
	// Autonomous handles it's own periodic
    	auton.periodic();

	// The arms periodic method needs to be called to make sure it
	// doesn't go to high or low.
	arm.periodic();

	// Send the dashboard data to the driver station.
	updateDashboard();
	
	// The deployments periodic method needs to be called to make
	// sure it works properly.
	deployment.periodic();
    }

    public long startTime; // Ignore

    /*
      This function is called when beginning tele-operated mode aka
      the human operated period.
     */
    public void teleopInit() {
	startTime = System.currentTimeMillis(); // Ignore

	// Make sure deployment does not deploy until told to by the
	// secondary driver.
	deployment.disable();
    }

    /*
      This function is repeatedly called as long as it's still teleop
      mode and the robot is enabled. The entire loop should run fast
      so that the robot is responsive to disabling. Any long running
      tasks should be implemented as a state machine, doing a little
      bit each loop.
     */    
    public void teleopPeriodic() {
	// Make sure the secondary driver controls are up to date,
	// unless there's an error. When there's an error, print it
	// and see if we can continue. Secondary controls may stop
	// responding.
    	try {
	    breakout.update();
	} catch (EnhancedIOException e2) { e2.printStackTrace(); }
    	
	// Joystick values are backwards, forwards is negative and
	// back is positive, so reverse it so that my mental model can
	// be more sane.
	double left = -leftJoy.getY();
	double right = -rightJoy.getY();
	// Pass the values to the drive train. When a
	// CANTimeoutException occurs print it and see if we can keep
	// going.
	try {
	    dt.drive(left, right);
	} catch (CANTimeoutException e1) { e1.printStackTrace(); }

	// If a piece is being placed or you are trying to get a piece
	// from the feeder station, make sure the grabber is
	// open. (The closeGrabber is a label from an old control
	// model, that input was repurposed) Otherwise, make sure the
	// grabber is closed to hold the game pieces in.
	if (breakout.closeGrabber || breakout.openGrabber) {
	    arm.open();
	} else {
	    arm.close();
	}

	// Make sure the arm is in the proper mode: Magic mode or
	// manual overide mode. Then pass the users commands to the
	// arm.
	try {
	    if (!breakout.armControlMode) {
		arm.setMagicMode();
		if (breakout.openGrabber) {
		    // If the user is trying to place a piece, place it. easy.
		    arm.placePiece();
		} else {
		    arm.moveToPosition(breakout.armPos);
		}
	    } else {
		arm.setManualMode();
		arm.manualMove(breakout.armPos);
	    }
	} catch (CANTimeoutException e) { e.printStackTrace(); }

	// If the user wants to release the minibot, obey.
	if (breakout.releaseMinibot) {
	    deployment.enable();
	} else {
	    deployment.disable();
	}
	// The deployments periodic method needs to be called to make
	// sure it works properly.
	deployment.periodic();

	// The arms periodic method needs to be called to make sure it
	// doesn't go to high or low.
	arm.periodic();
    }

    /*
      Called repeatedly while disabled, don't mess with any actuators
      (i.e. motors, servos, pneumatics). Just send back information
      for the dashboard.
     */
    public void disabledPeriodic() {
    	updateDashboard();
    }

    /*
      Keep the driver station and dashboard up to date with the latest
      information.
     */
    public void updateDashboard() {
	// Forward the drive trains data to the dashboard
	dt.updateDashboard();

	// Forward the arms data to the dashboard.
	arm.updateDashboard();

	// Send over the time the robots been active. (only works
	// right for teleop)
	SmartDashboard.log((System.currentTimeMillis() - startTime)/1000, "Time");

	// Update the driver station with the autonomous state
	DriverStationLCD.getInstance().println(DriverStationLCD.Line.kMain6, 1, 
			"Ready                      ");
	DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser3, 1, 
			"Autonomous Stage: "+auton.current);
	DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser2, 1, 
			"Mode: "+auton.getAutonomousName());
	DriverStationLCD.getInstance().updateLCD();
    }

    /*
      Print debug information to the netconsole
     */
    public void debug() {
	debugCounter += 1;

	// Only print one in ten loops to minimize network usage AND
	// if debug is enabled.
	if (DEBUG && debugCounter%10 == 0) {
	    try {
		// The last debug info I needed printed during the season.
		System.out.println("Left Encoder: "+dt.leftJag.getPosition()+"/"+dt.leftJag.getSpeed()+
				   " --- Right Encoder: "+dt.rightJag.getPosition()+"/"+dt.rightJag.getSpeed());
	    } catch (Exception e) {
		e.printStackTrace();
	    }			
	}
    }
}
