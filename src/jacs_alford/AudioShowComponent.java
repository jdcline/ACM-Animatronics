package jacs_alford;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class AudioShowComponent extends ShowComponent {

	private String filename = "";
	// private int duration = 0; //inherited
	private int startTimeOffset = 0;

	public AudioShowComponent(String filename) {
		this.filename = filename;
		// Verify

	}

	public void verify() throws FileNotFoundException {
		// if file found and is supported type
		super.setPlayEnabled(true);
		// else
		{
			// super.setPlayEnabled(false);
			// throw exception (file not found...or file not supported type warning
		}
	}

	public String getFilename() {
		return filename;
	}

	public int getStartOffset() {
		return startTimeOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startTimeOffset = startOffset;
	}

	// / UTILITIES //////////////
	public void pitchShift(double shift) {
	}

	public ArrayList<Byte> byteArrayListSynchedToAudio(int framesPerSecond) {
		return null;

	}

}
