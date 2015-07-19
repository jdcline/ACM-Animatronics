/**
 * 
 */
package jacs;

import jmcc.MicrocontrollerConnection;

/**
 * @author Jared
 *
 */
public class PlayerInputs {

	private MicrocontrollerConnection mc;
	/**
	 * Can't be 0
	 */
	private int servoFramesPerSecond;
	/**
	 * Can't be 0 and must be > servoFramesPerSecond and > audioBytesPerSecond (176400)
	 */
	private int cyclesPerSecond;

	public PlayerInputs(MicrocontrollerConnection mc, int servoFramesPerSecond, int cyclesPerSecond)
			throws Exception {
		this.mc = mc;
		this.servoFramesPerSecond = servoFramesPerSecond;
		this.cyclesPerSecond = cyclesPerSecond;

		if (servoFramesPerSecond == 0)
			throw new Exception("servoFramesPerSecond too low");

		if (cyclesPerSecond > servoFramesPerSecond || cyclesPerSecond > 176400
				|| cyclesPerSecond == 0)
			throw new Exception("Cycles Per Second is too high");

		if (areBlank()) {
			throw new Exception("One or more inputs is blank.");
		}
	}

	public boolean areBlank() {
		if (mc.equals("") || servoFramesPerSecond == 0 || cyclesPerSecond == 0)
			return true;
		else
			return false;
	}

	/**
	 * @return the mc
	 */
	public MicrocontrollerConnection getMc() {
		return mc;
	}

	/**
	 * @return the servoFramesPerSecond
	 */
	public int getServoFramesPerSecond() {
		return servoFramesPerSecond;
	}

	/**
	 * @return the cyclesPerSecond
	 */
	public int getCyclesPerSecond() {
		return cyclesPerSecond;
	}
}
