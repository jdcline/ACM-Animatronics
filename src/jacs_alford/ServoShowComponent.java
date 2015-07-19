package jacs_alford;

import java.util.ArrayList;

import jacs_utilities.AnimatronicsUtilities;

public class ServoShowComponent extends ShowComponent {

	public static final int USER_CONTROLLED = 0;
	public static final int AUDIO_CONTROLLED = 1;

	private static final int DEFAULT_FRAMES_PER_SECOND = 30;

	private int pin = -1; // pin servo is attached to
	private String description = ""; // description of purpose
	private int servoControlType = USER_CONTROLLED;

	public int getServoControlType() {
		return servoControlType;
	}

	public void setServoControlType(int servoControlType) {
		this.servoControlType = servoControlType;
	}

	private int framesPerSecond = DEFAULT_FRAMES_PER_SECOND; // Number of positions per second
	private ArrayList<Byte> positions = new ArrayList<Byte>(); // Timed sequence of servo positions
	private short MIN_MOTION = 0;
	private short MAX_MOTION = 254; // Compute?

	private boolean audiosynch = false; // Necessary? Does non-null imply true
	private AudioShowComponent audioSynchFile = null; // Could be it just is associated in time

	// private boolean recordingEnabled = true; // Is recording active?

	private int timeDuration; // In milliseconds. Related to FPS.
								// Could be used for recording

	private ServoDeviceInfo servo = null; // Info about physical servo device specs
	private boolean mute;

	public ServoShowComponent(String description, int pin) {
		this.description = description;
		this.pin = pin;
	}

	public ServoShowComponent(String description, int pin, byte[] motions, int fps) {
		this.description = description;
		this.pin = pin;
		positions = AnimatronicsUtilities.byteArrayToByteArrayList(motions);
		timeDuration = (positions.size() + fps) / fps; // round up
	}

	// public ServoShowComponent(String description, int pin, byte[] motions,
	// int duration) {
	// this.description = description;
	// this.pin = pin;
	// positions = Utilities.byteArraytobyteArrayList(motions);
	// framesPerSecond = (positions.size()) / duration / 1000; // round up
	// }

	public ServoShowComponent(String description, int pin, String filename) {
		this.description = description;
		this.pin = pin;

		synchToAudioFile(filename);

		timeDuration = (positions.size() + framesPerSecond) / framesPerSecond; // round up

	}

	public ServoShowComponent(String string, int pin2, int[] motions) {
		// TODO Auto-generated constructor stub
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public int getFramesPerSecond() {
		return framesPerSecond;
	}

	public void setFramesPerSecond(int framesPerSecond) {
		this.framesPerSecond = framesPerSecond;
	}

	public int getTimeDuration() {
		return timeDuration;
	}

	public void setTimeDuration(int timeDuration) {
		this.timeDuration = timeDuration;
	}

	public AudioShowComponent getAudioSynchFile() {
		return audioSynchFile;
	}

	public void setAudioSynchFile(AudioShowComponent audioSynchFile) {
		this.audioSynchFile = audioSynchFile;
	}

	public void associateAudio(String audioFile) {
		this.audioSynchFile = new AudioShowComponent(audioFile);
		this.audiosynch = false;
	}

	public void synchToAudioFile(String audioFile) {
		associateAudio(audioFile);
		// verify audio

		this.servoControlType = AUDIO_CONTROLLED;
		this.audioSynchFile = new AudioShowComponent(audioFile);
		positions = this.audioSynchFile.byteArrayListSynchedToAudio(framesPerSecond);
		this.audiosynch = true;

		// positions = Utilities.ByteArraySynchedToAudio(filename,framesPerSecond);
	}

	public void invert() {
		// Invert all motion values
	}

	public void erase() {
		positions.clear();
	}

	public void setAudioFile(String audioFile) {
		this.audioSynchFile = new AudioShowComponent(audioFile);

		if (audioFile.equals(""))
			this.audiosynch = false;
		else {
			this.audiosynch = true;
			// TO DO Synch To AudioFile
		}
	}

	public void adjustSynchToAudioFile() {
		// Add smoothing parameters etc. here
	}

	public boolean isMute() {
		if (audioSynchFile != null)
			return audioSynchFile.isPlayEnabled();
		else
			return true;
	}

	public void setMute(boolean mute) {
		if (audioSynchFile != null)
			audioSynchFile.setPlayEnabled(mute);
	}

	public void addPosition(short pos) {
		// if (recordingEnabled)
		positions.add((byte) pos);
	}

	public void addPosition(byte pos) {
		// if (recordingEnabled)
		positions.add((byte) pos);
	}

	public void addPosition(int pos) {
		// if (recordingEnabled)
		positions.add((byte) pos);
	}

	public byte[] getMotions() {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getMotionsByteArray() {
		// TODO Auto-generated method stub
		return null;
	}

}
