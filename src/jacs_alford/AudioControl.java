package jacs_alford;

public class AudioControl {

	private String fileName;
	private int startTimeOffset;
	private int duration;

	public AudioControl(String fileName, int startTimeOffset) {
		super();
		this.fileName = fileName;
		this.startTimeOffset = startTimeOffset;
	}

	public String getFileName() {
		return fileName;
	}

	public int getStartTimeOffset() {
		return startTimeOffset;
	}

	public Object getDuration() {
		// TODO Auto-generated method stub
		return null;
	}

}
