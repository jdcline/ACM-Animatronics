package animatronics2;

import jssc.SerialPort;

// Uses jssc for SerialPort
// Uses jmcc for Microcontroller
//   could use other

public class MicrocontrollerConnection {
	private Microcontroller microcontroller;
	private SerialPort port;

	// Settings

	public MicrocontrollerConnection(SerialPort port, Microcontroller mc) {
		microcontroller = mc;
		this.port = port;
	}

	public MicrocontrollerConnection(String portName, Microcontroller mc) {
		microcontroller = mc;
		this.port = new SerialPort(portname);
	}

	public void resetPort(String portName) {
		this.port = new SerialPort(portname);
		// reset board?
	}

	// For testing that the correct COM port was selected
	public void verifyPort(String portName) {
		this.port = new SerialPort(portname);
		// Open
		// verify connection
		// Disconnect
	}

	public void resetPort(String portName) {
		this.port = new SerialPort(portname);
		// reset board?
		// verify connection
		// return status?
	}

	public SerialPort getPort() {
		return port;
	}

	public void setPort(SerialPort port);

	{
		this.port = port;
	}

	// reset Microcontroller

	// Routines for one at a time commands
	public void setTarget(int pin, int position) {
		// Prepare bytes for this command
		byte[] command = microcontroller.setTarget(pin, position);
		sendSingleCommand(command);
		// return status to user

	}

	public void sendSingleCommand(byte[] command) {
		// Connect
		port.writeBytes(command);
		// Close

		// Return status
	}

	// ////////////////////////////////////////////////////////////////////////////

	public void sendServoCommandStream(byte[] command) {
		// Verify connected
		port.writeBytes(command);
		// ? wait to finish moving?

		// Return status
		// stay connected
	}
}
