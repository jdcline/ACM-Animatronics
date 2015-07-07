package jasc;

public class TimingSettings {

	private int servoFramesPerSecond;
	/**
	 * Must be <= servoFramesPerSecond and <= audioBytesPerSecond
	 */
	private int cyclesPerSecond;
	private int audioBytesPerSecond = 176400; // 44100 samples/second * 16 bits per sample / 8 bits per byte * 2
	// channels

	private int servoFramesPerCycle;
	private int audioBytesPerCycle;
	private int servoLag = 5;

	public TimingSettings(int servoFramesPerSecond, int cyclesPerSecond) {
		this.servoFramesPerSecond = servoFramesPerSecond;
		this.cyclesPerSecond = cyclesPerSecond;

		calculatePerCycleInfo(servoFramesPerSecond, cyclesPerSecond);
	}

	/**
	 * @param servoFramesPerSecond
	 * @param cyclesPerSecond
	 */
	private void calculatePerCycleInfo(int servoFramesPerSecond, int cyclesPerSecond) {
		servoFramesPerCycle = servoFramesPerSecond / cyclesPerSecond;
		audioBytesPerCycle = audioBytesPerSecond / cyclesPerSecond;
	}

	/**
	 * @return the servoFramesPerSecond
	 */
	public int getServoFramesPerSecond() {
		return servoFramesPerSecond;
	}

	/**
	 * @param servoFramesPerSecond
	 *            the servoFramesPerSecond to set
	 */
	public void setServoFramesPerSecond(int servoFramesPerSecond) {
		this.servoFramesPerSecond = servoFramesPerSecond;
		calculatePerCycleInfo(servoFramesPerSecond, cyclesPerSecond);
	}

	/**
	 * @return the cyclesPerSecond
	 */
	public int getCyclesPerSecond() {
		return cyclesPerSecond;
	}

	/**
	 * @param cyclesPerSecond
	 *            the cyclesPerSecond to set
	 */
	public void setCyclesPerSecond(int cyclesPerSecond) {
		this.cyclesPerSecond = cyclesPerSecond;
		calculatePerCycleInfo(servoFramesPerSecond, cyclesPerSecond);
	}

	/**
	 * @return the audioBytesPerSecond
	 */
	public int getAudioBytesPerSecond() {
		return audioBytesPerSecond;
	}

	/**
	 * @param audioBytesPerSecond
	 *            the audioBytesPerSecond to set
	 */
	public void setAudioBytesPerSecond(int audioBytesPerSecond) {
		this.audioBytesPerSecond = audioBytesPerSecond;
		calculatePerCycleInfo(servoFramesPerSecond, cyclesPerSecond);
	}

	/**
	 * @return the servoFramesPerCycle
	 */
	public int getServoFramesPerCycle() {
		return servoFramesPerCycle;
	}

	/**
	 * @return the audioBytesPerCycle
	 */
	public int getAudioBytesPerCycle() {
		return audioBytesPerCycle;
	}

	public int getServoLag() {
		return servoLag;
	}

	/**
	 * @param servoLag
	 *            the servoLag to set
	 */
	public void setServoLag(int servoLag) {
		this.servoLag = servoLag;
	}

}
