import java.io.FileNotFoundException;

import jmcc.MicrocontrollerConnection;
import showController.AnimatronicsShowPlayer;
import showController.AnimatronicsUtilities;

/**
 * 
 */

/**
 * @author galford
 *
 */
public class ShowPlayerTest {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {

		MicrocontrollerConnection mc = new MicrocontrollerConnection();
		SerialFileReader serialReader = new SerialFileReader();

		AnimatronicsShowPlayer player = new AnimatronicsShowPlayer(mc, 30, 30);
		byte[][] servoMotions;
		try {
			servoMotions = AnimatronicsUtilities.readBytesMultipleServo("data/TalkSlowly30FPS.csv",
					1);
			player.playShow("data/TalkSlowly.wav", new int[] { 2 }, servoMotions);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} // main
}
