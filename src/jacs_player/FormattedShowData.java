/**
 * 
 */
package jacs_player;

/**
 * Pre-packaged data to be used with an {@linkplain AnimatronicsShowPlayer}
 * 
 * @author Jared Cline
 *
 */
public class FormattedShowData {

	private String audioFile;
	private byte[] pinNumbers;
	private byte[][] servoMotions;
	private int servoLag;
	private byte[] recordedPinNumbers;

	/**
	 * Creates a package of show data to be used with an
	 * {@linkplain AnimatronicsShowPlayer}
	 * 
	 * @param audioFile
	 *            the path to the audio file. "" denotes no audio file.<br>
	 *            Audio files must be a stereo .wav file (44100 Hz, 2 channels,
	 *            16 bits per channel).
	 * @param pinNumbers
	 *            the pin numbers for the servos controlled via the pre-loaded
	 *            motions
	 * @param servoMotions
	 *            the pre-loaded motions for the servos.<br>
	 *            Preloaded motions must come in a 2-dimensional array (one
	 *            array per motor) at 30 frames (bytes) per second. Each motion
	 *            byte must be a number from 0 to 254, representing a scaled
	 *            version of the absolute angle of the servo motor.
	 * @param recordedPinNumbers
	 *            the pin numbers for the servos whose motions are coming in
	 *            from a real-time recording device. If no recorded servos are
	 *            present, use an array of length 0.
	 * @param servoLag
	 *            the lag in milliseconds after each byte is sent to the
	 *            microcontroller. Note that with more servos, less lag is
	 *            needed because the bytes are distributed among more motors.
	 * @throws Exception
	 *             if motions aren't found for each pre-loaded motor, servo lag
	 *             is negative, if the motion array is not rectangular, or if
	 *             any fields are blank.
	 */
	public FormattedShowData(String audioFile, byte[] pinNumbers, byte[][] servoMotions, byte[] recordedPinNumbers,
			int servoLag) throws Exception {
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
