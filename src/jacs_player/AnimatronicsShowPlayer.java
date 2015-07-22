package jacs_player;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import jmcc.MicrocontrollerConnection;
import jssc.SerialPortException;

/**
 * Player for an animatronics show. Allows a user to play an audio file
 * synchronized to Servo motor motions (including specified starting and ending
 * points), a real-time audio feed, and a real time servo feed. An unlimited
 * number of servo motors are supported.
 * 
 * @author Jared Cline
 */
public class AnimatronicsShowPlayer {
	/**
	 * A number denoting the maximum show length in frames. Set to
	 * {@linkplain Integer#MAX_VALUE} / 3
	 */
	public static final int MAX_SHOW_LENGTH = Integer.MAX_VALUE / 3;

	MicrocontrollerConnection microConnection;

	TimingSettings timingSettings;

	// Serial Data
	byte[] serialDataStream; // Gets created in this class - one long stream of
							 // bytes ready to send to serial port

	private int showCurSerialByte = 0; // Keeps track of current byte position
	private int showCurAudioByte = 0;
	/**
	 * Flag for advanceShow method
	 */
	private boolean pausedShow = false;

	// Flag for output threads
	private boolean exitShow = true;

	private CyclicBarrier barrier;

	// Audio, Serial, and Timer
	private int numBarrierThreads = 3;

	private long prevCycleTime;

	private ServoPlayer servo;
	private Timer timer;
	private AudioPlayer audio;
	private Thread servoThread, audioThread, timerThread;

	private RecordedAudioPlayer recordedAudio;
	private Thread recordedAudioThread;

	private ArrayList<ArrayList<Byte>> recordedServoInputBuffer = new ArrayList<ArrayList<Byte>>();
	private byte[] recordedPinNumbers;

	private Lock recordedServoLock = new ReentrantLock();

	private boolean hasAudioFile = true;

	/**
	 * Sets up the microcontroller connection and the timing settings for a new
	 * player based on the input parameters.
	 * 
	 * @param inputs
	 *            Note: cycles per second must be <= servo frames per second --
	 *            see {@link PlayerInputs}
	 */
	public AnimatronicsShowPlayer(PlayerInputs inputs) {
		microConnection = inputs.getMc();
		timingSettings = new TimingSettings(inputs.getServoFramesPerSecond(), inputs.getCyclesPerSecond());
	}

	/**
	 * Calls the
	 * {@link AnimatronicsShowPlayer#playShow(FormattedShowData, long, long)
	 * playShow(FormattedShowData, long, long)} method with the default values
	 * <BLOCKQUOTE>startTime = 0, endTime = MAX_SHOW_LENGTH</BLOCKQUOTE>
	 * 
	 * @see AnimatronicsShowPlayer#playShow(FormattedShowData, long, long)
	 *      playShow(FormattedShowData, long, long)
	 * @param data
	 *            show data -- see {@link FormattedShowData}
	 * @throws Exception
	 *             if an error is found
	 */
	public void playShow(FormattedShowData data) throws Exception {
		playShow(data, 0, MAX_SHOW_LENGTH);
	}

	/**
	 * Begins playback of the show specified by the input parameter.
	 * 
	 * @param data
	 *            show data -- see {@link FormattedShowData}
	 * @param startTime
	 *            the number of seconds by which to offset the start of the show
	 *            from the beginning of the data
	 * @param endTime
	 *            the number of seconds by which to offset the end of the show
	 *            from the end of the data
	 * @throws Exception
	 *             if an error is found
	 */
	public void playShow(FormattedShowData data, long startTime, long endTime) throws Exception {
		if (pausedShow) {
			resumeShow();
			throw new Exception("Show resumed");
		} else if (!exitShow)
			throw new Exception("Show is currently playing");

		this.recordedPinNumbers = data.getRecordedPinNumbers();
		for (int i = 0; i < recordedPinNumbers.length; i++) {
			recordedServoInputBuffer.add(new ArrayList<Byte>());
		}

		exitShow = false;
		timingSettings.setServoLag(data.getServoLag());
		serialDataStream = createServoMotionStream(data.getPinNumbers(), data.getServoMotions());
		startSynchronizedShowTasks(data.getAudioFile(), data.getServoMotions().length, startTime, endTime,
				data.getRecordedPinNumbers());
	}

	private void startSynchronizedShowTasks(String audioFile, int numServos, long startTime, long endTime,
			byte[] recordedPinNumbers) throws Exception {

		if (audioFile.equals("")) {
			numBarrierThreads--;
			hasAudioFile = false;
		}
		barrier = new CyclicBarrier(numBarrierThreads, new Runnable() {
			@Override
			public void run() {
				if (!((!hasAudioFile || audio.getAudioExitFlag()) && servo.getServoExitFlag()) && !exitShow) {
					while (pausedShow) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					advanceShow();
				} else {
					try {
						System.out.println("Exit Show");
						exitShow = true;
						closeThreads();
						recordedServoInputBuffer.clear();
					} catch (SerialPortException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});

		servo = new ServoPlayer(timingSettings.getServoFramesPerCycle() * numServos, serialDataStream, microConnection,
				barrier, (int) (startTime * timingSettings.getServoFramesPerSecond() / 1000),
				endTime * timingSettings.getServoFramesPerSecond() / 1000, recordedPinNumbers,
				timingSettings.getServoLag());
		timer = new Timer(timingSettings.getCyclesPerSecond(), barrier);
		audio = new AudioPlayer(barrier, timingSettings.getAudioBytesPerCycle());
		recordedAudio = new RecordedAudioPlayer();

		prevCycleTime = System.currentTimeMillis();

		servoThread = new Thread(servo);
		servoThread.start();

		timerThread = new Thread(timer);
		timerThread.start();

		if (!audioFile.equals("")) {
			audio.play(audioFile, (int) (startTime * timingSettings.getAudioBytesPerSecond() / 1000),
					endTime * timingSettings.getAudioBytesPerSecond() / 1000);
			audioThread = new Thread(audio);
			audioThread.start();
		}

		recordedAudio.play();
		recordedAudioThread = new Thread(recordedAudio);
		recordedAudioThread.start();

	}

	/**
	 * Create an interleaved stream of bytes using correct protocol
	 * 
	 * @return byte 2n = pin, byte 2n+1 = movement
	 */
	private byte[] createServoMotionStream(byte[] pinNumbers, byte[][] motions) {

		byte[] returnArray = new byte[2 * motions.length * motions[0].length];
		// Interleave bytes
		for (int j = 0; j < motions[0].length; j++) {
			for (int i = 0; i < motions.length; i++) {
				returnArray[2 * (j * motions.length + i)] = pinNumbers[i];
				returnArray[2 * (j * motions.length + i) + 1] = motions[i][j];
			}
		}

		// Set start and stop positions (to allow for beginning to end play or
		// segment play)

		return returnArray;

	}

	// routine to advance show that plays when all threads reach barrier
	private void advanceShow() {
		// Advance pointers through servo byte array
		showCurSerialByte = servo.getCurByte();
		showCurAudioByte = audio.getCurByte();
		System.out.println(showCurSerialByte + "," + showCurAudioByte);

		// Note: servoBytesPerCycle should be a factor of serialDataStream
		System.out.println(showCurSerialByte + "," + servo.getBytesPerCycle() + "," + serialDataStream.length);

		// Might not be a bad idea to gather some stats using system time
		System.out.println("Cycle Time (millis):" + (System.currentTimeMillis() - prevCycleTime));
		prevCycleTime = System.currentTimeMillis();
	}

	private class RecordedAudioPlayer implements Runnable {
		private byte[] data;
		private ByteArrayOutputStream stream = new ByteArrayOutputStream();
		private SourceDataLine audioLine;
		private boolean runSwitch = true;
		private boolean hasRecordedAudio = false;
		private Lock streamLock = new ReentrantLock();

		RecordedAudioPlayer() {
		}

		/**
		 * Play a given audio file.
		 * 
		 * @param audioFilePath
		 *            Path of the audio file.
		 */
		void play() {

			try {

				AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
				audioLine = (SourceDataLine) AudioSystem.getLine(info);
				data = new byte[audioLine.getBufferSize() / 10];
				audioLine.open(format);
				audioLine.start();

			} catch (LineUnavailableException ex) {
				System.out.println("Audio line for playing back is unavailable.");
				ex.printStackTrace();
			}
		}

		@Override
		public void run() {
			while (runSwitch) {
				while (runSwitch && hasRecordedAudio) {
					streamLock.lock();
					try {
						data = stream.toByteArray();
						stream.reset();

					} finally {
						streamLock.unlock();
					}
					audioLine.write(data, 0, data.length);
				}

				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		void setRecordedAudioInput(boolean b) {
			hasRecordedAudio = b;

		}

	}

	private class AudioPlayer implements Runnable {
		private byte[] bytesBuffer;
		private int bytesRead = -1;
		private AudioInputStream audioStream;
		private SourceDataLine audioLine;
		private CyclicBarrier barrier;
		private boolean audioExitFlag = false;
		private boolean runSwitch = true;
		private int curByte = 0;
		private long endingByte;
		private boolean hasAudio = false;

		AudioPlayer(CyclicBarrier barrier, int bufferSize) {
			this.barrier = barrier;
			bytesBuffer = new byte[bufferSize];
		}

		/**
		 * Play a given audio file.
		 * 
		 * @param audioFilePath
		 *            Path of the audio file.
		 * @throws Exception
		 */
		void play(String audioFilePath, int bytesToSkip, long endingByte) throws Exception {

			File audioFile = new File(audioFilePath);
			try {
				audioStream = AudioSystem.getAudioInputStream(audioFile);
				audioStream.skip(bytesToSkip);
				curByte += bytesToSkip;

				this.endingByte = endingByte;

				AudioFormat format = audioStream.getFormat();

				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

				audioLine = (SourceDataLine) AudioSystem.getLine(info);

				audioLine.open(format);

				audioLine.start();

				hasAudio = true;
				System.out.println("Playback started.");

			} catch (UnsupportedAudioFileException ex) {
				throw new Exception("The specified audio file is not supported.", ex.getCause());
			} catch (LineUnavailableException ex) {
				throw new Exception("Audio line for playing back is unavailable.", ex.getCause());
			} catch (IllegalArgumentException ex) {
				throw new Exception("Audio line for specified format unavailable.", ex.getCause());
			} catch (IOException ex) {
				throw new Exception("Error playing the audio file.", ex.getCause());
			}
		}

		@Override
		public void run() {
			try {
				while (runSwitch) {
					while (hasAudio && runSwitch && (bytesRead = audioStream.read(bytesBuffer)) != -1
							&& curByte < endingByte) {
						if (curByte + bytesRead > endingByte)
							bytesRead = (int) (endingByte - curByte);
						curByte += bytesRead;
						audioLine.write(bytesBuffer, 0, bytesRead);
						barrier.await();
					}
					this.audioExitFlag = true;
					if (runSwitch) {
						barrier.await();
					}
				}
			} catch (IOException ex) {
				System.out.println("Error playing the audio file.");
				ex.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}

		}

		/**
		 * @return the audioExitFlag
		 */
		Boolean getAudioExitFlag() {
			return audioExitFlag;
		}

		/**
		 * @return the curByte
		 */
		int getCurByte() {
			return curByte;
		}

	}

	private class ServoPlayer implements Runnable {

		/**
		 * Excludes counting signal bytes
		 */
		private int bytesPerCycle;
		private byte[] motions;
		private MicrocontrollerConnection mc;
		private CyclicBarrier barrier;
		private boolean servoExitFlag = false;
		private boolean runSwitch = true;
		private int curByte = 0;
		private int endingByte;
		private int bytesPerPacket = 2;
		private boolean hasRecordedServoInput;
		private int recordedBytesRead = 0;
		private byte[] recordedPinNumbers;
		private int lagMillis;

		ServoPlayer(int motionsPerCycle, byte[] serialDataStream, MicrocontrollerConnection mc, CyclicBarrier barrier,
				int motionsToSkip, long endingMotion, byte[] recordedPinNumbers, int lagMillis)
						throws SerialPortException {
			this.bytesPerCycle = motionsPerCycle * bytesPerPacket;
			this.motions = serialDataStream;
			this.lagMillis = lagMillis;
			this.mc = mc;
			this.barrier = barrier;
			this.endingByte = (int) endingMotion * bytesPerPacket;
			curByte += motionsToSkip * bytesPerPacket;

			hasRecordedServoInput = recordedPinNumbers.length > 0;
			this.recordedPinNumbers = recordedPinNumbers;

			mc.openPort();
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				while (runSwitch) {
					while (runSwitch && curByte < motions.length && curByte < endingByte) {
						// System.out.println("RunSwitch: " + runSwitch +
						// "\tcurByte " + curByte
						// + "\tmotions.length " + motions.length);
						// If fewer than bytesPerCycle left, adjust settings to
						// play only what is left
						if (curByte + bytesPerCycle > serialDataStream.length)
							setBytesPerCycle(serialDataStream.length - curByte);
						else if (curByte + bytesPerCycle > endingByte)
							setBytesPerCycle(endingByte - curByte);

						for (int i = 0; i < bytesPerCycle; i += 2) {
							mc.setTarget(motions[curByte + i], motions[curByte + i + 1]);
							sendRecordedData(1);
							Thread.sleep(lagMillis);
						}
						sendRecordedData(-1);
						curByte += bytesPerCycle;
						barrier.await();
					}
					this.servoExitFlag = true;
					System.out.println("Servo finished");
					if (runSwitch) {
						barrier.await();
					}

				}
			} catch (SerialPortException e) {
				System.out.println("Problem with serial port");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			} catch (BrokenBarrierException e) {
				System.out.println("Broken Barrier");
			}

		}

		/**
		 * @param numSignals
		 *            number of signals to send, -1 if all
		 * @throws SerialPortException
		 */
		private void sendRecordedData(int numSignals) throws SerialPortException {
			if (hasRecordedServoInput && !recordedServoInputBuffer.isEmpty()) {
				Byte[][] temp = new Byte[recordedPinNumbers.length][0];
				for (int i = 0; i < temp.length; i++) {
					if (!recordedServoInputBuffer.get(i).isEmpty()) {
						recordedServoLock.lock();
						try {
							temp[i] = recordedServoInputBuffer.get(i).toArray(temp[i]);
						} finally {
							recordedServoLock.unlock();
						}
					}
				}

				int totalLength = 0;
				for (int i = 0; i < temp.length; i++) {
					totalLength += temp[i].length;
				}

				if (numSignals == -1) {
					recordedBytesRead = totalLength;
				} else
					recordedBytesRead = Math.min(numSignals, totalLength);

				int counter = 0;
				int i = 0;
				int j = 0;
				for (j = 0; j < temp[i].length && counter < recordedBytesRead; j++) {
					for (; i < temp.length && counter < recordedBytesRead; i++) {
						mc.setTarget(recordedPinNumbers[i], temp[i][j]);
						counter++;
					}
					i = 0;
				}
				clearRecordedInput();
			}
		}

		private void clearRecordedInput() {
			int counter = 0, i;
			recordedServoLock.lock();
			try {
				for (i = 0; counter < recordedBytesRead; i++, i = i % recordedServoInputBuffer.size()) {
					if (recordedServoInputBuffer.get(i).size() > 0) {
						recordedServoInputBuffer.get(i).remove(0);
						counter++;
					}
				}
			} finally {
				recordedServoLock.unlock();
			}
		}

		/**
		 * @return the bytesPerCycle
		 */
		int getBytesPerCycle() {
			return bytesPerCycle;
		}

		/**
		 * @param bytesPerCycle
		 *            the bytesPerCycle to set
		 */
		void setBytesPerCycle(int bytesPerCycle) {
			this.bytesPerCycle = bytesPerCycle;
		}

		/**
		 * @return the servoExitFlag
		 */
		Boolean getServoExitFlag() {
			return servoExitFlag;
		}

		/**
		 * @return the curByte
		 */
		int getCurByte() {
			return curByte;
		}

		void setRecordedServoInput(boolean input) {
			hasRecordedServoInput = input;

		}

	}

	private class Timer implements Runnable {

		private int millisecondsWait = 0;
		private CyclicBarrier barrier;
		private boolean runSwitch = true;

		public Timer(int cyclesPerSecond, CyclicBarrier barrier) {
			// this.millisecondsWait = 1000 / cyclesPerSecond;
			this.barrier = barrier;
		}

		@Override
		public void run() {
			while (runSwitch) {
				try {
					Thread.sleep(millisecondsWait);
					barrier.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("timer interrupted");
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * Pauses playback of the show, preserving all current motions and audio
	 */
	public void pauseShow() {
		pausedShow = true;
	}

	/**
	 * Resumes the show from the paused state
	 */
	public void resumeShow() {
		// Verify connection still valid or be sure to handle exceptions in
		// threads and exit cleanly
		microConnection.verifyPort();
		pausedShow = false;
	}

	/**
	 * Stops playback of the show, ending all motions and audio
	 */
	public void stopShow() {
		pausedShow = false;
		exitShow = true;

	}

	private void closeThreads() throws Exception {
		timer.runSwitch = false;

		servo.runSwitch = false;
		servo.mc.closePort();

		try {
			audio.runSwitch = false;
			if (audio.hasAudio) {
				audio.audioLine.drain();
				audio.audioLine.close();
				audio.audioStream.close();
			}

			System.out.println("Playback completed.");
		} catch (IOException ex) {
			throw new Exception("Error playing the audio file.", ex.getCause());
		}

		recordedAudio.runSwitch = false;
		recordedAudio.audioLine.drain();
		recordedAudio.audioLine.close();
	}

	/**
	 * Adds the given audio input byte array to the recorded audio input stream
	 * for immediate playback. Show input must come in at 44,100 Hz, stereo
	 * audio at 16 bits per channel, signed and in big endian format. <br>
	 * <BLOCKQUOTE>Use the format:
	 * <code>AudioFormat format = new AudioFormat(44100, 16, 2, true, true);</code>
	 * </BLOCKQUOTE>
	 * 
	 * @param input
	 *            the bytes to be added to the recorded audio input stream
	 * @throws IOException
	 *             if an I/O error occurs writing to the byte stream
	 */
	public boolean addRecordedAudioInput(byte[] input) throws IOException {
		if (exitShow || recordedAudio == null)
			return false;

		recordedAudio.streamLock.lock();
		try {
			recordedAudio.setRecordedAudioInput(true);
			recordedAudio.stream.write(input);
		} finally {
			recordedAudio.streamLock.unlock();
		}

		return true;
	}

	/**
	 * Adds the given servo input for the given pin to the servo output stream
	 * for immediate playback. Show input must come in at the same rate as the
	 * servo frames per second in the {@link PlayerInputs}
	 * 
	 * @param input
	 *            the bytes to be added to the servo output stream
	 */
	public boolean addRecordedServoInput(byte pinNumber, byte[] input) {
		if (exitShow || servo == null)
			return false;
		servo.setRecordedServoInput(true);
		recordedServoLock.lock();
		try {
			for (int i = 0; i < recordedPinNumbers.length; i++) {
				if (recordedPinNumbers[i] == pinNumber) {
					for (byte b : input) {
						recordedServoInputBuffer.get(i).add(b);
					}
				}
			}
		} finally {
			recordedServoLock.unlock();
		}

		return true;
	}

}