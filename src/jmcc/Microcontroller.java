package jmcc;

public abstract class Microcontroller {

	private String microcontrollerName = ""; // e.g. "Pololu Micro Maestro" or "Arduino Uno"

	private String defaultProtocol = "";
	private static String MINI_SSC = "Mini-SSC";

	protected static final byte MINI_SSC_SET_TARGET_COMMAND = (byte) 0xFF; // Move to superclass Microcontroller

	public Microcontroller(String controllerName) {
		microcontrollerName = controllerName;
		defaultProtocol = MINI_SSC;
	}

	public String getMicrocontrollerName() {
		return microcontrollerName;
	}

	public void setMicrocontrollerName(String microcontrollerName) {
		this.microcontrollerName = microcontrollerName;
	}

	public String getDefaultProtocol() {
		return defaultProtocol;
	}

	public void setDefaultProtocol(String defaultProtocol) {
		this.defaultProtocol = defaultProtocol;
	}

	/**
	 * This generates a byte sequence for writing to serial port to move a servo to a target position This will handle
	 * choosing protocol
	 * 
	 * @param pin
	 * @param position
	 * @return
	 */
	public byte[] buildSetTargetCommand(byte pin, byte position) {
		// TODO Auto-generated method stub
		if (defaultProtocol.equals(MINI_SSC))
			return buildCommandSetTargetMiniSSC(pin, position);
		else
			// For native protocols
			return buildCommandSetTargetNative(pin, position);
	}

	/**
	 * @param pin
	 * @param target
	 * @return
	 */
	public byte[] buildCommandSetTargetMiniSSC(byte pin, byte target) {
		// Create byte array according to Pololu User's Guide to set target using MiniSSC protocol
		byte[] setTargetMiniSSCCmd = { MINI_SSC_SET_TARGET_COMMAND, (byte) pin,
				(byte) target };
		return setTargetMiniSSCCmd;
	}

	/**
	 * This builds native protocol command to set target servo
	 * 
	 * @param pin
	 * @param target
	 * @return
	 */
	public abstract byte[] buildCommandSetTargetNative(byte pin, short target);

	// Write versions of the same command for different method signatures for convenience.
	// They should convert to proper data type and call the above routines as the base so if
	// there is a change in the command, only the base functin needs to change.
}
