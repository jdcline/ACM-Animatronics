package jmcc;

public abstract class Maestro extends Microcontroller {

	// Pololu values from Pololu manual -
	protected static final byte DEVICE_NUMBER_DEFAULT = (byte) 255;
	protected static final byte NO_RESET_PIN = (byte) 255;

	// Pololu commands from Pololu User's Manual Chapter 5
	protected static final byte SET_TARGET_COMMAND = (byte) 0x84;
	protected static final byte SET_SPEED_COMMAND = (byte) 0x87;
	protected static final byte SET_ACCELERATION_COMMAND = (byte) 0x89;
	protected static final byte GET_POSITION_COMMAND = (byte) 0x90;
	protected static final byte GET_MOVING_STATE = (byte) 0x93;

	// Pololu pin information will vary with specific
	protected PinInfo[] pins;

	public Maestro(int numberPins) {
		super("Pololu Maestro");
		this.initializePinInfo(numberPins);
	}

	public Maestro(String product, int numberPins) {
		super(product);
		this.initializePinInfo(numberPins);
	}

	/**
	 * This will initialize the pin state for each unique card
	 * 
	 * @param numberPins
	 */
	public void initializePinInfo(int numberPins) {
		pins = new PinInfo[numberPins];

	}

	public void setPin(int pinIndex, PinInfo p) {
		if (pinIndex < pins.length)
			pins[pinIndex] = p;
	}

	public byte[] buildCommandSetTargetNative(byte pin, short target) {
		// Create byte array according to Pololu User's Guide to set target using Pololu protocol
		byte[] setTargetCmd = { SET_TARGET_COMMAND, (byte) pin, (byte) target }; // TODO POOJA/COURTNEY
		return setTargetCmd;

	}

	public byte[] buildCommandSetSpeed(byte pin, short target) {
		// Create byte array according to Pololu User's Guide to set speed using Pololu protocol
		byte[] setSpeedCmd = { SET_SPEED_COMMAND, (byte) pin, (byte) target }; // TODO POOJA/COURTNEY
		return setSpeedCmd;

	}

	public byte[] buildCommandSetAcceleration(byte pin, short target) {
		// Create byte array according to Pololu User's Guide to set acceleration using Pololu protocol
		byte[] getSetAccelerationCmd = { SET_ACCELERATION_COMMAND, (byte) 2,
				(byte) 127 }; // TODO POOJA/COURTNEY
		return getSetAccelerationCmd;
	}

	public byte[] buildCommandGetPosition(byte pin) {
		// Create byte array according to Pololu User's Guide to get Position using Pololu protocol
		byte[] getPositionCmd = { GET_POSITION_COMMAND, (byte) 2, (byte) 127 }; // TODO POOJA/COURTNEY
		return getPositionCmd;

	}

	public byte[] buildCommandGetMovingState() {
		// Create byte array according to Pololu User's Guide to set acceleration using Pololu protocol
		byte[] getMovingStateCmd = { GET_MOVING_STATE, (byte) 2, (byte) 127 }; // TODO POOJA/COURTNEY
		return getMovingStateCmd;
	}

	public void reset() {
		;
	} //

	// Write versions of the same command for different method signatures for convenience.
	// They should convert to proper data type and call the above routines as the base so if
	// there is a change in the command, only the base functin needs to change.
}