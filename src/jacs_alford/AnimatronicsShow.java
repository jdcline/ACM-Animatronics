/**
 * 
 */
package jacs_alford;

import jacs.FormattedShowData;

import java.util.ArrayList;

/**
 * @author galford
 *
 */
public class AnimatronicsShow {

	private String showName = "Demo";

	private ArrayList<ShowComponent> showComponents = new ArrayList<ShowComponent>();

	// Prepare and format for playback - pull only enabled components from ShowComponents
	// Format for Player...
	private ArrayList<String> enabledAudio = new ArrayList<String>();
	private ArrayList<ServoShowComponent> enabledServos = new ArrayList<ServoShowComponent>();

	// Compute show duration using offsets (future)
	private int showDuration;

	public AnimatronicsShow() {
	}

	public AnimatronicsShow(String file) {
		// Open files
		// load data
	}

	public void saveShow(String file) {
		// Dump data to file
		// close file
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	// ///// Add audio /////////////////////////////////////////////////////////
	public void addAudioTrack(String filename) {
		showComponents.add(new AudioShowComponent(filename));
	}

	// ////// Add servo /////////////////////////////////////////////////////////
	public void addServoTrack(int pin, int[] motions) {
		showComponents.add(new ServoShowComponent("ServoOnPin " + pin, pin, motions));
	}

	public void addServoTrack(int pin) {
		showComponents.add(new ServoShowComponent("ServoOnPin " + pin, pin));
	}

	public void addServoTrackSynchedToAudio(int pin, String filename) {
		showComponents.add(new ServoShowComponent("ServoOnPin " + pin, pin, filename));

	}

	// This will extract all enabled playback data from current configuration
	public FormattedShowData getPlaybackFormattedData() {
		return null;
		// Get Audio
		// Get Enabled Servos
		// Get Pins on enabled servos
		// Get motions from enabled servos
	}

	// public void getEnabledAudio() {
	// ArrayList<String> audioToPlay = new ArrayList<String>();
	// for (AudioTrack atrack : showAudio)
	// if (!atrack.isMute()) {
	// audioToPlay.add(atrack.getFilename());
	// }
	// }

	// Pull out all enabled audio files
	public void getEnabledAudio() {
		ArrayList<String> audioToPlay = new ArrayList<String>();
		for (ShowComponent s : showComponents)

			if (s instanceof AudioShowComponent && s.isPlayEnabled()) {
				audioToPlay.add(((AudioShowComponent) s).getFilename());

			} else if (s instanceof ServoShowComponent
					&& ((ServoShowComponent) s).getServoControlType() == ServoShowComponent.AUDIO_CONTROLLED) {
				if (((ServoShowComponent) s).getAudioSynchFile() != null)
					audioToPlay.add(((ServoShowComponent) s).getAudioSynchFile().getFilename());
			}
	}

	public int[] getEnabledServoPins() {
		ArrayList<Integer> pinsForEnabledServos = new ArrayList<Integer>();
		for (ShowComponent sc : showComponents)
			if (sc instanceof ServoShowComponent && sc.isPlayEnabled()) {
				pinsForEnabledServos.add(((ServoShowComponent) sc).getPin());
			}

		int[] pins = new int[pinsForEnabledServos.size()];
		for (int i = 0; i < pinsForEnabledServos.size(); i++)
			pins[i] = pinsForEnabledServos.get(i);
		return pins;

	}

	public byte[][] getServoTracks(byte[] pins) {
		byte[][] allCommands = new byte[pins.length][];
		for (int i = 0; i < pins.length; i++) {
			// byte[] singleServoCommands = Utilities
			// .ShortArrayToByteArray(getServoTrackOnPin(pins[i])
			// .getPositions());
		}
		return allCommands;

	}

	public byte[] getServoTrackOnPin(int pin) {
		int i = 0;
		while (i < showComponents.size()) {

			// Find servo on requested pin
			if (showComponents.get(i) instanceof ServoShowComponent
					&& showComponents.get(i).isPlayEnabled()
					&& ((ServoShowComponent) showComponents.get(i)).getPin() != pin)
				break;
			i++;
		}
		if (i < showComponents.size())
			return ((ServoShowComponent) showComponents.get(i)).getMotionsByteArray();
		else
			return null;
	}
}
