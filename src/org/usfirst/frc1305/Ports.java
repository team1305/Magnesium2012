package org.usfirst.frc1305;

/**
 * @author paul
 *
 */
public final class Ports {

	//pwm ports
	static final int PWM_LEFT_MOTOR1 = 1;
	static final int PWM_LEFT_MOTOR2 = 2;
	static final int PWM_RIGHT_MOTOR1 = 3;
	static final int PWM_RIGHT_MOTOR2 = 4;
	
	static final int PWM_INTAKE_ARM = 5;
	static final int PWM_HOOD_TRIM = 6;
	static final int PWM_SHOOTER1 = 7;
	static final int PWM_SHOOTER2 = 8;
	static final int PWM_CAMERA_SERVO = 9;
	static final int PWM_HOOD_ROTATION = 10;
	
	//relay ports
	static final int RELAY_COMPRESSOR = 1;
	static final int RELAY_INTAKE_ROLLER = 2;
	static final int RELAY_TOWER_ROLLER = 3;
	static final int RELAY_CAMERALIGHTS = 4;
	
	//digital in/out
	
	static final int DIO_PRESSURESENSOR = 1;
	static final int DIO_ENC_LEFTWHEEL1 = 2;
	static final int DIO_ENC_LEFTWHEEL2 = 3; 
	static final int DIO_ENC_RIGHTWHEEL1 = 4;
	static final int DIO_ENC_RIGHTWHEEL2 = 5; 
	static final int DIO_ENC_TOWER1 = 7;
	static final int DIO_ENC_TOWER2 = 8; 
	static final int DIO_ENC_RPMSENSOR = 9;
	
	//solenoids
	static final int SOL_GEARSHIFT = 1;
	static final int SOL_TOWERPLATE = 2;
	static final int SOL_INTAKE = 3;
	static final int SOL_LIGHT_SENSOR = 4;
	
	//analog IO
	static final int AIO_HOODTRIM = 1;
	static final int AIO_INTAKEWHEEL = 2;
	static final int AIO_GYRO = 3;
	static final int AIO_WHEELSPEED = 4;

}
