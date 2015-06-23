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
public class JMCCTesterPortConnect {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
				// port.setParams();
				try {
					port.openPort();
					System.out.println("Opened Port Successfully");
				} catch (SerialPortException e) {
					error = "Error Opening Port: " + e;
					System.out.println("Error Opening Port: " + e);
				}
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
