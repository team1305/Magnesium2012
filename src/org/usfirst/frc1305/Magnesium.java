

package org.usfirst.frc1305;


import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import org.usfirst.frc1305.Ports;
import org.usfirst.frc1305.Constants;

/*
 * Code for the 2012 robot, Magnesium
 * 
 * @author Paul Belanger <paulirino@gmail.com>
 * @version 1.0.0
 * @since 1.0.0
 */
public class Magnesium extends SimpleRobot {

	//set up all of the victors
	Victor leftDrive1  		= new Victor(Ports.PWM_LEFT_MOTOR1);
	Victor leftDrive2  		= new Victor(Ports.PWM_LEFT_MOTOR2);
	Victor rightDrive1 		= new Victor(Ports.PWM_RIGHT_MOTOR1);
	Victor rightDrive2 		= new Victor(Ports.PWM_RIGHT_MOTOR2);
	
	Victor mIntakeArm  		= new Victor(Ports.PWM_INTAKE_ARM);
	Victor mHoodTrim   		= new Victor(Ports.PWM_HOOD_TRIM);
	Victor mShooter1  		= new Victor(Ports.PWM_SHOOTER1);
	Victor mShooter2 		= new Victor(Ports.PWM_SHOOTER2);
	Victor mHoodRotation 	= new Victor(Ports.PWM_HOOD_ROTATION);
	Victor mDummyVictor 	= new Victor(0);
	
	//set up the relays
	Relay rRollerIntake = new Relay(Ports.RELAY_INTAKE_ROLLER);
	Relay rRollerTower 	= new Relay(Ports.RELAY_TOWER_ROLLER);
	Relay rCameraLights = new Relay(Ports.RELAY_CAMERALIGHTS);
	
	//set up the camera servo
	Servo mCameraServo		= new Servo(Ports.PWM_CAMERA_SERVO);
	
	//set up encoders
	Encoder encLeftWheel 	= new Encoder(Ports.DIO_ENC_LEFTWHEEL1, Ports.DIO_ENC_LEFTWHEEL2);
	Encoder encRightWheel	= new Encoder(Ports.DIO_ENC_RIGHTWHEEL1, Ports.DIO_ENC_RIGHTWHEEL2);
	Encoder encTowerRotation= new Encoder(Ports.DIO_ENC_TOWER1, Ports.DIO_ENC_TOWER2);
	Encoder encRPMSensor	= new Encoder(Ports.DIO_ENC_RPMSENSOR, Ports.DIO_ENC_RPMSENSOR);
	
	//set up analog sensors
	AnalogChannel potHoodTrim 		= new AnalogChannel(Ports.AIO_HOODTRIM);
	AnalogChannel potIntakeWheel	= new AnalogChannel(Ports.AIO_INTAKEWHEEL);
	Gyro gyroRotation 				= new Gyro(Ports.AIO_GYRO);
	
	//set up solenoids
	Solenoid solShifter		= new Solenoid(Ports.SOL_GEARSHIFT);
	Solenoid solTowerPlate	= new Solenoid(Ports.SOL_TOWERPLATE);
	Solenoid solIntake		= new Solenoid(Ports.SOL_INTAKE);
	Solenoid solLightSensor = new Solenoid(Ports.SOL_LIGHT_SENSOR);
	
	//pid controllers
	PIDController pidHoodTrim 	  =	new PIDController(0.00925, 0.0009, 0.0000006, potHoodTrim, mHoodTrim);
	PIDController pidHoodRotation = new PIDController(0.014, 0.00049, 0.000081, encTowerRotation,mHoodRotation);	
	PIDController pidShooterWheel = new PIDController(0.00589, 0.00008, 0.00009, encRPMSensor, mDummyVictor);
	
	//other objects
	RobotDrive chassis 		= new RobotDrive(leftDrive1, leftDrive2, rightDrive1, rightDrive2);
	Joystick driverstick 	= new Joystick(1);
	Joystick shooterstick	= new Joystick(2);
	Compressor compressor 	= new Compressor(Ports.DIO_PRESSURESENSOR, Ports.RELAY_COMPRESSOR);
	
	//and here go a bunch of variables
	private boolean currentGear;
	private boolean intakeTrimOverride;
	private double  camPosition;
	
	//and now a bunch of variables go here as needed. 
	
	public Magnesium(){
		//first we enable the compressor
		compressor.start();
		//set the chassis expiration
		chassis.setExpiration(0.1);
		
		//defaults for various settings
		currentGear = false;
		intakeTrimOverride = false;
		//defaults for the camera servo
		camPosition = 0.80;
		mCameraServo.set(camPosition);
		
	}
	
	public void autonomous() {
        
    }
	
    public void operatorControl() {
    	chassis.setSafetyEnabled(true);
    	while(isOperatorControl()){
    		encTowerRotation.reset();
    		while(isEnabled()){
    			//MAIN DRIVER FUNCTIONS
    			
    			//drive using the two analog sticks
    			chassis.arcadeDrive(driverstick.getRawAxis(2), driverstick.getRawAxis(3));
    			
    			//set the grear using right shoulders
    			SetChassisGear(driverstick.getRawButton(6), driverstick.getRawButton(8));
    			
    			//set input trim using lleft triggers
    			SetIntakeRollerTrim(driverstick.getRawButton(5), driverstick.getRawButton(7));
    			
    			//emergency intake piston drop using the button 4(y)
    			SetIntakePistonState(shooterstick.getRawButton(4));
    			
    			//SECONDARY DRIVER FUNCTIONS
    			
    			//intake belt state toggle
    			SetIntakeBeltState(shooterstick.getRawButton(6));
    			
    			//hood trim using right analog up/down
    			SetHoodTrim(-1.0*shooterstick.getRawAxis(4));
    			
    			//camera elevation
    			SetCameraElevation(shooterstick.getRawAxis(2));
    			
    			//shoot wheel velocityy
//    			SetShootWheelVelocity(shooterstick.getRawButton(3), shooterstick.getRawButton(1),shooterstick.getRawButton(2), shooterstick.getRawButton(4));
    			
    			//Set tower rotation
    			SetTowerRotation(shooterstick.getRawAxis(1), shooterstick.getRawButton(0));
    			
    			//toggle the backplate
    			SetTowerPistonState(shooterstick.getRawButton(7));
    			
    			//elevator on button 8
    			SetTowerBeltState(shooterstick.getRawButton(8), shooterstick.getRawButton(5));
    			
    			//and finally a really ghetto pid
    			if(shooterstick.getRawButton(2)){
    				pidShooterWheel.enable();
    				SetWheelSpeed(73);
    			}
    			else if(shooterstick.getRawButton(3)){
    				pidShooterWheel.enable();
    				SetWheelSpeed(64);
    			}
    			else if(shooterstick.getRawButton(1)){
    				pidShooterWheel.enable();
    				SetWheelSpeed(20);
    			}
    			else if(shooterstick.getRawButton(4)){
    				pidShooterWheel.enable();
    				SetWheelSpeed(26);
    			}
    			else{
    				pidShooterWheel.reset();
    				pidShooterWheel.disable();
    				mShooter1.set(0);
    				mShooter2.set(0);
    			}
    		}
    	}
    }
    
    void SetIntakeRollerTrim(boolean upbutton, boolean downbutton){
    	if(downbutton) mIntakeArm.set(0.5);
    	else if(upbutton) mIntakeArm.set(-1.0);
    	else mIntakeArm.set(0.0);    	
    }
    void SetIntakeBeltState(boolean state){
    	if(state) 	rRollerIntake.set(Value.kForward);
    	else 		rRollerIntake.set(Value.kOff);
    }
    void SetIntakePistonState(boolean state){
    	if(state){
    		intakeTrimOverride = true;
    		mIntakeArm.set(0.9);
    		solIntake.set(true);
    	}
    	else if(intakeTrimOverride){
    		mIntakeArm.set(0.0);
    		solIntake.set(false);
    		intakeTrimOverride = false;
    	}
    }
    void SetTowerBeltState(boolean upstate, boolean downstate){
    	if(upstate)	rRollerTower.set(Value.kReverse);
    	else if(downstate)	rRollerTower.set(Value.kForward);
    	else	rRollerTower.set(Value.kOff);
    	
    }
    void SetHoodTrim(double value){
    	if(value > 0.0) value *= 0.4;
    	if(value < 0.0) value *= 0.3;
    	
    	if(potHoodTrim.getValue() > Constants.HOODTRIM_MIN_VALUE + 20 && 
    			potHoodTrim.getValue() < Constants.HOODROTATION_MAX_VALUE - 20){
    		mHoodTrim.set(value);
    	}
    	else if(potHoodTrim.getValue() > Constants.HOODTRIM_MIN_VALUE + 20 &&
    				value < 0){
    		mHoodTrim.set(value);
    	}
    	else if(potHoodTrim.getValue() < Constants.HOODTRIM_MAX_VALUE - 20 &&
    				value > 0){
    		mHoodTrim.set(value);
    	}
    	else{
    		mHoodTrim.set(0);
    	}
    	
    }
    void SetTowerRotation(double magnitude, boolean zerooverride){
    	if(!zerooverride) mHoodRotation.set(magnitude);
    	else if(zerooverride){
    		//TODO: turret centering here
    	}
    }
    void SetShootWheelVelocity(boolean but1, boolean but2, boolean but3, boolean but4){
    	if(but1){
    		mShooter1.set(-1.0);
    		mShooter2.set(-1.0);
    	}
    	else if(but2){
    		mShooter1.set(-0.83);
    		mShooter2.set(-0.83);
    	}
    	else if(but3){
    		mShooter1.set(-0.27);
    		mShooter2.set(-0.27);
    	}
    	else if(but4){
    		mShooter1.set(-0.335);
    		mShooter2.set(-0.335);
    	}
    	else{
    		mShooter1.set(0);
    		mShooter2.set(0);
    	}
    }
    void SetLightState(boolean state){
    	//TODO: put lights on some kind of relay
    }
    void SetTowerPistonState(boolean state){
    	solTowerPlate.set(!state);
    }
    void SetChassisGear(boolean upstate, boolean downstate){
    	if(upstate) 		currentGear = true;
    	else if(downstate) 	currentGear = false;
    	
    	solShifter.set(currentGear);
    }
    void SetWheelSpeed(double value){
    	pidShooterWheel.setSetpoint(value);
    	double deviation = pidShooterWheel.get();
    	if(deviation > 0.5) deviation = 0.5;
    	
    	if(mShooter1.get() - deviation > -0.2){
    		mShooter1.set(-0.2);
    		mShooter2.set(-0.2);
    	}
    	else{
    		mShooter1.set(mShooter1.get() - deviation);
    		mShooter2.set(mShooter2.get() - deviation);
    	}
    }
    void SetCameraElevation(double stick){
    	if(stick > 0.05 || stick < -0.05){
    		double dChangeFactor = -1 * stick / 100;
    		camPosition += dChangeFactor;
    		if(camPosition > 1.0) camPosition = 1.0;
    		if(camPosition < 0.5) camPosition = 0.5;
    		
    		mCameraServo.set(camPosition);
    	}
    }
    
}
