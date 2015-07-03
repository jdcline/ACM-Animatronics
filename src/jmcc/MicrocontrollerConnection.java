package jmcc;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

// Uses jssc for SerialPort
// Uses jmcc for Microcontroller
//   could use other

public class MicrocontrollerConnection {
	private Microcontroller microcontroller;
	private SerialPort port;

	// Settings

	public MicrocontrollerConnection() {
		microcontroller = new MicroMaestro();
		String[] ports = SerialPortList.getPortNames();
		if (ports.length > 0)
			port = new SerialPort(ports[0]);

	}

	public MicrocontrollerConnection(SerialPort port, Microcontroller mc) {
		microcontroller = mc;
		this.port = port;
	}

	public MicrocontrollerConnection(String portName, Microcontroller mc) {
		microcontroller = mc;
		this.port = new SerialPort(portName);
	}

	public void openPort() throws SerialPortException {
		try {
			port.openPort();
			port.setParams(9600, 8, 1, 0);
		} catch (SerialPortException e) {
			throw new SerialPortException(null, null, null);
		}
	}

	public void closePort() throws SerialPortException {
		try {
			port.closePort();
		} catch (SerialPortException e) {
			throw new SerialPortException(null, null, null);
		}
	}

	// For testing that the correct COM port was selected
	public boolean verifyPort() {
		// this.port = new SerialPort(portName);
		// Open
		// verify connection
		// Disconnect

		return true;
	}

	public void resetPort(String portName) {
		this.port = new SerialPort(portName);
		// reset board?
		// verify connection
		// return status?
	}

	public SerialPort getPort() {
		return port;
	}

	public void setPort(SerialPort port) {
		this.port = port;
	}

	// reset Microcontroller

	// Routines for one at a time commands
	public void setTarget(byte pin, byte position) throws SerialPortException {
		// Prepare bytes for this command
		byte[] command = microcontroller.buildSetTargetCommand((byte) pin, (byte) position);
		sendSingleCommand(command);
		// return status to user

	}

	private void sendSingleCommand(byte[] command) throws SerialPortException {
		if (port.isOpened())
			try {
				// System.out.println("Command serial: " + command[0] + " " + command[1] + " "
				// + command[2]);
				port.writeBytes(command);
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {
			this.openPort();
			sendSingleCommand(command);
		}

	}

	/*
	 * private void connectAndSendSingleCommand(byte[] command) {
	 * try {
	 * this.openPort();
	 * } catch (SerialPortException e1) {
	 * // TODO Auto-generated catch block
	 * e1.printStackTrace();
	 * }
	 * try {
	 * port.writeBytes(command);
	 * } catch (SerialPortException e) {
	 * e.printStackTrace();
	 * }
	 * try {
	 * this.closePort();
	 * } catch (SerialPortException e) {
	 * // TODO Auto-generated catch block
	 * e.printStackTrace();
	 * }
	 * 
	 * }
	 */

}
