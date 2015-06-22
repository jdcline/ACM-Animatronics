package animatronics1;

import java.util.Arrays;
import java.util.concurrent.CyclicBarrier;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

/**
 * @author Jared Cline
 *
 */
public class AnimatronicsShowPlayer {

	private CyclicBarrier barrier;
	private Player[] players;
	private SerialPort port;

	/**
	 * @param portNums
	 *            must be same length as motorMovements
	 * @param motorMovements
	 *            must be same length as portNums
	 */
	public AnimatronicsShowPlayer(byte[] portNums, byte[][] motorMovements) {
		if (portNums.length != motorMovements.length)
			System.out.println("Input arrays of different length");

		else { // Valid Input
				// int numPlayers = portNums.length + 1;
			int numPlayers = portNums.length;
			players = new Player[numPlayers];
			// showLength = motorMovements[0].length;

			/*
			 * Create a new barrier housing the given number of motor players +
			 * an audio player
			 */
			barrier = new CyclicBarrier(numPlayers);

			// insert audio player at players[0]

			setupSerialPort();

			// initialize SerialPlayers for each motor
			for (int i = 0; i < numPlayers; i++) {
				players[i] = new SerialPlayer(port, portNums[i], motorMovements[i], barrier);
			}

			for (Player p : players) {
				new Thread(p).start();
			}
		}

	}

	private void setupSerialPort() {
		String[] portNames = SerialPortList.getPortNames();
		System.out.println(Arrays.toString(portNames));

		try {
			if (portNames.length < 1) {
				throw new Exception("No port names found");
			} else // create port
			{
				for (int i = 0; i < portNames.length && port == null; i++) {
					System.out.println("Attempting to open to: " + portNames[0]);
					port = new SerialPort(portNames[0]);
				}

				if (port == null)
					throw new SerialPortException(port.getPortName(), null, "Port not created");

				else {
					// OPEN CONNECTION
					port.openPort();
					System.out.println("Opened Port Successfully");
				}
			}
		} catch (SerialPortException e) {
			System.out.println("Error opening serial port " + e.getPortName() + ": "
					+ e.getExceptionType());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void closeSerialPort() {
		try {
			port.closePort();
			System.out.println("Closed Port Successfully");
		} catch (SerialPortException e) {
			System.out.println("Error Closing Port: " + e);
		}
	}
}
