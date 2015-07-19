package jacs_alford;

public abstract class ShowComponent {

	public static final int JACS_TIME_TRIGGER = 0;
	public static final int JACS_EVENT_TRIGGER = 1; // future

	private String description = "";
	private boolean playEnabled = true;
	private int triggerType = JACS_TIME_TRIGGER;

	private int startTimeOffest = 0; // in milliseconds
	private int duration;

	public ShowComponent() {
		setDefaults();
	}

	public ShowComponent(String description) {
		setDefaults();
		this.description = description;
	}

	public ShowComponent(String description, boolean playEnabled,
			int triggerType, int startTimeOffest) {
		super();
		this.description = description;
		this.playEnabled = playEnabled;
		this.triggerType = triggerType;
		this.setStartTimeOffest(startTimeOffest);
	}

	private void setDefaults() {
		this.description = "Demo";
		this.playEnabled = true;
		this.triggerType = JACS_TIME_TRIGGER;
		this.setStartTimeOffest(0);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDuration() {
		return duration;
	}

	public boolean isPlayEnabled() {
		return playEnabled;
	}

	public void setPlayEnabled(boolean playEnabled) {
		this.playEnabled = playEnabled;
	}

	public int getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}

	public int getStartTimeOffest() {
		return startTimeOffest;
	}

	public void setStartTimeOffest(int startTimeOffest) {
		this.startTimeOffest = startTimeOffest;
	}

}
