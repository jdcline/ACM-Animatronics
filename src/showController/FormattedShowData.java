/**
 * 
 */
package showController;

/**
 * @author Jared
 *
 */
public class FormattedShowData {

	private String audioFile;
	private int[] pinNumbers;
	private byte[][] servoMotions;

	public FormattedShowData(String audioFile, int[] pinNumbers, byte[][] servoMotions)
			throws Exception {
		this.audioFile = audioFile;
		this.pinNumbers = pinNumbers;
		this.servoMotions = servoMotions;

		checkData();
	}

	private void checkData() throws Exception {
		if (pinNumbers.length != servoMotions.length)
			throw new Exception("Motions not found for every servo");

		for (int i = 0; i < servoMotions.length; i++) {
			if (servoMotions[i].length != servoMotions[0].length)
				throw new Exception("Motion array not rectangular");
		}

		if (areBlank())
			throw new Exception("One or more data fields is blank");

	}

	public boolean areBlank() {
		if (audioFile.equals("") && (pinNumbers.length == 0 || servoMotions.length == 0))
			return true;
		else
			return false;
	}

	/**
	 * @return the audioFile
	 */
	public String getAudioFile() {
		return audioFile;
	}

	/**
	 * @return the pinNumbers
	 */
	public int[] getPinNumbers() {
		return pinNumbers;
	}

	/**
	 * @return the servoMotions
	 */
	public byte[][] getServoMotions() {
		return servoMotions;
	}
}
