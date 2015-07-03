import java.io.FileNotFoundException;

import jmcc.MicrocontrollerConnection;
import showController.AnimatronicsShowPlayer;
import showController.AnimatronicsUtilities;
import showController.FormattedShowData;
import showController.PlayerInputs;

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

		try {
			AnimatronicsShowPlayer player = new AnimatronicsShowPlayer(new PlayerInputs(mc, 30, 30));
			byte[][] servoMotions;
			servoMotions = AnimatronicsUtilities.readBytesMultipleServo("data/TalkSlowly30FPS.csv",
					1);
			player.playShow(new FormattedShowData("data/TalkSlowly.wav", new int[] { 2 },
					servoMotions));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} // main
}
