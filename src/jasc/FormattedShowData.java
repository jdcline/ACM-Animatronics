/**
 * 
 */
package jasc;

/**
 * @author Jared
 *
 */
public class FormattedShowData {

	private String audioFile;
	private byte[] pinNumbers;
	private byte[][] servoMotions;
	private int servoLag;
	private byte[] recordedPinNumbers;

	/**
	 * @param audioFile
	 * @param pinNumbers
	 *            first pin number is recorded servo pin -- -1 if not needed
	 * @param servoMotions
	 * @throws Exception
	 */
	public FormattedShowData(String audioFile, byte[] pinNumbers, byte[][] servoMotions,
			byte[] recordedPinNumbers, int servoLag) throws Exception {
		this.audioFile = audioFile;
		this.pinNumbers = pinNumbers;
		this.servoMotions = servoMotions;
		this.servoLag = servoLag;
		this.recordedPinNumbers = recordedPinNumbers;

		checkData();
	}

	private void checkData() throws Exception {
		if (pinNumbers.length != servoMotions.length)
			throw new Exception("Motions not found for every servo");

		if (servoLag < 0)
			throw new Exception("Servo lag cannot be negative.");

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
	public byte[] getPinNumbers() {
		return pinNumbers;
	}

	/**
	 * @return the servoMotions
	 */
	public byte[][] getServoMotions() {
		return servoMotions;
	}

	/**
	 * @return the servoLag
	 */
	public int getServoLag() {
		return servoLag;
	}

	/**
	 * @return the recordedPinNumbers
	 */
	public byte[] getRecordedPinNumbers() {
		return recordedPinNumbers;
	}
}
