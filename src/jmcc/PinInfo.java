package jmcc;

public class PinInfo {

	// This can be a class that keeps information about each pin
	// Each board can have an array of these
	// Each board is designed differently and some pins have different capabilities
	// e.g. Arduino Uno pins that have a ~ support PWM (pulse width modulation) and
	// are the only ones that can support a servo.
	// Pins can be configured to be input or output at any given time

	// Some of the features might be better as a static class with constants for each board
	// for things that do not change.

	private boolean enabled = false;
	// private boolean PWM = true;
	private String configured = "OUTPUT";

	// private ServoRange servoInfo = null;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getConfigured() {
		return configured;
	}

	public void setConfigured(String configured) {
		this.configured = configured;
	}

}
