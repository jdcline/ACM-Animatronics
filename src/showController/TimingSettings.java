package showController;

public class TimingSettings {

	private int servoFramesPerSecond;
	/**
	 * servoFramesPerSecond * millisecondsPerCycle >= 1000
	 * This is real time
	 */
	private int millisecondsPerCycle;

	private int numServos;
	private int servoBytesPerCycle;
	private int audioBytesPerCycle;

	public TimingSettings(int servoFramesPerSecond, int millisecondsPerCycle) {
		this.servoFramesPerSecond = servoFramesPerSecond;
		this.millisecondsPerCycle = millisecondsPerCycle;
	}

	public void setNumServos(int numServos) {
		this.numServos = numServos;

		servoBytesPerCycle = 2 * numServos * servoFramesPerSecond * millisecondsPerCycle / 1000;
		// - number bytes per second to go with frames per second
		// - time of delay after each servo command

	}

	public int getServoBytesPerCycle() {
		return servoBytesPerCycle;
	}

	/**
	 * @return the millisecondsPerCycle
	 */
	public int getMillisecondsPerCycle() {
		return millisecondsPerCycle;
	}

}
