import jmcc.MicrocontrollerConnection;
import jssc.SerialPortException;

/**
 * 
 */

/**
 * @author galford
 *
 */
public class MicroControllerConnectionTest {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		MicrocontrollerConnection mc = new MicrocontrollerConnection();

		// Request open

		try {
			mc.openPort();
			// Stream targets

			for (int i = 0; i < 254; i++) {
				mc.setTarget((byte) 2, (byte) i);
				Thread.sleep(5); // Give servo time to catch up between each command
			}
			for (int i = 254; i > 0; i--) {
				mc.setTarget((byte) 2, (byte) i);
				Thread.sleep(5); // Give servo time to catch up between each command
			}

			System.out.println("Send Data Successfully");

			// Request close
			mc.closePort();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} // main
}
