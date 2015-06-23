import java.util.Arrays;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * 
 */

/**
 * @author galford
 *
 */
public class JMCCTesterPortSendTest {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		String error = "No errors";
		String[] portNames = SerialPortList.getPortNames();
		System.out.println(Arrays.toString(portNames));
		SerialPort port = null;

		if (portNames.length < 1) {
			error = "No port names found";
		} else // create port
		{
			System.out.println("Attempting to open to: " + portNames[0]);
			port = new SerialPort(portNames[0]);

			if (port == null)
				error = "Port not created";

			else // openPort Connection
			{
				// OPEN CONNECTION
				try {
					port.openPort();
					System.out.println("Opened Port Successfully");
				} catch (SerialPortException e) {
					error = "Error Opening Port: " + e;
					System.out.println("Error Opening Port: " + e);
				}
				// SEND BYTES To control a Servo on pin 2 to sweep using Mini-SSC protocol
				try {
					for (int i = 0; i < 254; i++) {
						port.writeByte((byte) 255);
						port.writeByte((byte) 2);
						port.writeByte((byte) i);
						Thread.sleep(1); // Give servo time to catch up
					}

					for (int i = 254; i > 0; i--) {
						port.writeByte((byte) 255);
						port.writeByte((byte) 2);
						port.writeByte((byte) i);
						Thread.sleep(1); // Give servo time to catch up
					}

					System.out.println("Send Data Successfully");
				} catch (SerialPortException e) {
					error = "Error Opening Port: " + e;
					System.out.println("Error Opening Port: " + e);
				}

				// CLOSE CONNECTION
				try {
					port.closePort();
					System.out.println("Closed Port Successfully");
				} catch (SerialPortException e) {
					error = "Error Closing Port: " + e;
					System.out.println("Error Closing Port: " + e);
				}
			} // else openPort connection
		} // else create port

	} // main

}
