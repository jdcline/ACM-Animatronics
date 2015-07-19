package jacs_alford;

public class ServoDeviceInfo {

	private static final int STANDARD_SERVO = 0;
	private static final int CONTINUOUS_ROTATION_SERVO = 1;

	private String model = "";
	private int type = STANDARD_SERVO;

	public ServoDeviceInfo(String model) {
		this.model = model;
	}

}
