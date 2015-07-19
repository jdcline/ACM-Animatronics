/**
 * 
 */
package jacs_player;

import jmcc.MicrocontrollerConnection;

/**
 * Pre-packaged settings for an {@linkplain AnimatronicsShowPlayer}
 * 
 * @author Jared Cline
 *
 */
public class PlayerInputs {

	private MicrocontrollerConnection mc;

	private int servoFramesPerSecond;

	private int cyclesPerSecond;

	/**
	 * Pre-packaged settings for an {@linkplain AnimatronicsShowPlayer}
	 * 
	 * @param mc
	 *            a {@link jmcc.MicrocontrollerConnection}
	 * @param servoFramesPerSecond
	 *            recommended 30fps, cannot be 0
	 * @param cyclesPerSecond
	 *            Can't be 0 and must be > servoFramesPerSecond and >
	 *            audioBytesPerSecond (176400)
	 * @throws Exception
	 */
	public PlayerInputs(MicrocontrollerConnection mc, int servoFramesPerSecond, int cyclesPerSecond) throws Exception {
		this.mc = mc;
		this.servoFramesPerSecond = servoFramesPerSecond;
		this.cyclesPerSecond = cyclesPerSecond;

		if (servoFramesPerSecond == 0)
			throw new Exception("servoFramesPerSecond too low");

		if (cyclesPerSecond > servoFramesPerSecond || cyclesPerSecond > 176400 || cyclesPerSecond == 0)
			throw new Exception("Cycles Per Second is too high");

		if (areBlank()) {
			throw new Exception("One or more inputs is blank.");
		}
	}

	private boolean areBlank() {
		if (mc.equals("") || servoFramesPerSecond == 0 || cyclesPerSecond == 0)
			return true;
		else
			return false;
	}

	/**
	 * @return the MicrocontrollerConnection
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
