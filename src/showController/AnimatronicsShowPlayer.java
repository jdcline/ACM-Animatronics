package showController;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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
 * This method manages all threads and timing for playback of animatronics show
 * 
 * Plays a single stream of audio bytes and a single stream of servo bytes
 * (Provides utility to take multiple byte streams for multiple servo control and interlaces them)
 * (designed to allow multiple audio files but not supported)
 * Manages multiple synchronized threads
 * (Provides system clock control, which can be turned on and off, as fallback synchronization)
 * Catches interrupts and issues interrupts to clean up threads upon all sorts of errors
 * Provides callback to enable pausing and resuming a show
 * 
 * 
 * @author Jared
 */
public class AnimatronicsShowPlayer {
	public static final long MAX_SHOW_LENGTH = 2147483647;

	// Connection information about devices getting data - provides status about
	// connection; formats for current protocol
	// (Microcontroller/AudioPlayer/Video?)
	MicrocontrollerConnection microConnection;

	// Data that relates to the timing as relates to audio and servo motion
	// frame rate

	/**
	 * We need to define what these are
	 * 1. Frames Per Second for Servo motion (based on one servo per audio frame)
	 * 2. Number of Servos being controlled each frame
	 * 3. Number of Bytes to send to Audio Stream before waiting.
	 * 4. Number of bytes to send to Serial port per audio section (based on 1 & 2, related to 3 )
	 * 5. Serial Port Thread Delay (based on 1 & 2, related to 3)
	 * 6. Timer Thread Delay (based on trial and error and tunable via user)
	 */
	TimingSettings timingSettings;

	// bytesPerSecionAdvance = xxx;

	// Serial Data
	byte[] serialDataStream; // Gets created in this class - one long stream of
								// bytes ready to send to serial port

	// Show playback controls - need to establish consistent units
	private int showStart = 0;
	private int showEnd; // Computed as last frame/section
	private int showCurSerialByte = 0; // Keeps track of current byte position
	private int showCurAudioByte = 0;
	/**
	 * Flag for advanceShow method
	 */
	private boolean pausedShow = false;
	/**
	 * Flag for output threads
	 */
	private boolean exitShow = false;

	private CyclicBarrier barrier;
	/**
	 * Audio, Serial, and Timer
	 */
	private int numBarrierThreads = 3;

	private Thread servoThread, audioThread, timerThread;

	private long prevCycleTime;

	private ServoPlayer servo;

	private Timer timer;

	private AudioPlayer audio;

	public AnimatronicsShowPlayer(MicrocontrollerConnection mcm, int servoFramesPerSecond,
			int cyclesPerSecond) {
		microConnection = mcm;
		timingSettings = new TimingSettings(servoFramesPerSecond, cyclesPerSecond);
	}

	public void playShow(String audioFile, int[] pinNumbers, byte[][] servoMotions) {
		// Set start and stop positions to allow for start to finish play
		try {
			exitShow = false;
			serialDataStream = createServoMotionStream(pinNumbers, servoMotions, 0,
					servoMotions[0].length);
			startSynchronizedShowTasks(audioFile, servoMotions.length, 0, MAX_SHOW_LENGTH);
		} catch (SerialPortException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			// Array mismatch
			e.printStackTrace();
		}

	}

	public void playShow(String audioFile, int[] pinNumbers, byte[][] servoMotions, int startTime,
			int endTime) {
		// Set start and stop positions (to allow for beginning to end play or
		// segment play)

		try {
			exitShow = false;
			serialDataStream = createServoMotionStream(pinNumbers, servoMotions, startTime, endTime);
			startSynchronizedShowTasks(audioFile, servoMotions.length, startTime, endTime);
		} catch (SerialPortException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			// Array mismatch
			e.printStackTrace();
		}

	}

	public void startSynchronizedShowTasks(String audioFile, int numServos, int startTime,
			long maxShowLength) throws SerialPortException {
		barrier = new CyclicBarrier(numBarrierThreads, new Runnable() {
			public void run() {
				if (!(audio.getAudioExitFlag() && servo.getServoExitFlag()) && !exitShow) {
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
						audio.stop();
						servo.stop();
						timer.stop();
					} catch (SerialPortException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});

		servo = new ServoPlayer(timingSettings.getServoFramesPerCycle() * numServos,
				serialDataStream, microConnection, barrier, startTime
						* timingSettings.getServoFramesPerSecond() / 1000, maxShowLength
						* timingSettings.getServoFramesPerSecond() / 1000);

		timer = new Timer(timingSettings.getCyclesPerSecond(), barrier);

		audio = new AudioPlayer(barrier, timingSettings.getAudioBytesPerCycle());
		prevCycleTime = System.currentTimeMillis();

		servoThread = new Thread(servo);
		servoThread.start();
		timerThread = new Thread(timer);
		timerThread.start();
		audio.play(audioFile, startTime * timingSettings.getAudioBytesPerSecond() / 1000,
				maxShowLength * timingSettings.getAudioBytesPerSecond() / 1000);
		audioThread = new Thread(audio);
		audioThread.start();

	}

	// Create an interleaved stream of bytes using correct protocol
	/**
	 * Return: byte 2n = pin, byte 2n+1 = movement
	 * 
	 * @param pinNumbers
	 * 
	 * @param motions
	 *            rectangular 2d input array
	 * @param start
	 *            start byte number in rectangular 2d input array
	 * @param end
	 *            end byte number in rectangular 2d input array
	 * @return
	 * @throws Exception
	 *             if the length of pinNumbers does not equal the length of motions or if motions is not rectangular
	 */
	private byte[] createServoMotionStream(int[] pinNumbers, byte[][] motions, int start, int end)
			throws Exception {
		boolean isRectangular = true;
		for (int i = 1; i < motions.length; i++) {
			if (motions[i].length != motions[i - 1].length)
				isRectangular = false;
		}

		if (pinNumbers.length != motions.length || !isRectangular)
			throw new Exception("Input Array Error");

		else {
			byte[] returnArray = new byte[2 * motions.length * motions[0].length];
			// Interleave bytes
			for (int j = 0; j < motions[0].length; j++) {
				for (int i = 0; i < motions.length; i++) {
					returnArray[2 * (j * motions.length + i)] = (byte) pinNumbers[i];
					returnArray[2 * (j * motions.length + i) + 1] = motions[i][j];
				}
			}

			// Set start and stop positions (to allow for beginning to end play or
			// segment play)

			return returnArray;
		}

	}

	// routine to advance show that plays when all threads reach barrier
	private void advanceShow() {
		// Advance pointers through servo byte array
		showCurSerialByte = servo.getCurByte();
		showCurAudioByte = audio.getCurByte();
		System.out.println(showCurSerialByte + "," + showCurAudioByte);

		// Note: servoBytesPerCycle should be a factor of serialDataStream
		System.out.println(showCurSerialByte + "," + servo.getBytesPerCycle() + ","
				+ serialDataStream.length);
		/*
		 * if (audio.getAudioExitFlag() || servo.getServoExitFlag()) {
		 * System.out.println("Exit Show");
		 * exitShow = true;
		 * servoThread.interrupt();
		 * audioThread.interrupt();
		 * timerThread.interrupt();
		 * }
		 */
		// Might not be a bad idea to gather some stats using system time
		System.out.println("Cycle Time (millis):" + (System.currentTimeMillis() - prevCycleTime));
		prevCycleTime = System.currentTimeMillis();
	}

	class AudioPlayer implements Runnable {
		private byte[] bytesBuffer;
		private int bytesRead = -1;
		private AudioInputStream audioStream;
		private SourceDataLine audioLine;
		private CyclicBarrier barrier;
		private boolean audioExitFlag = false;
		private boolean runSwitch = true;
		private int curByte = 0;
		private long endingByte;

		public AudioPlayer(CyclicBarrier barrier, int bufferSize) {
			this.barrier = barrier;
			bytesBuffer = new byte[bufferSize];
		}

		/**
		 * Play a given audio file.
		 * 
		 * @param audioFilePath
		 *            Path of the audio file.
		 */
		void play(String audioFilePath, int bytesToSkip, long endingByte) {

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

				System.out.println("Playback started.");

			} catch (UnsupportedAudioFileException ex) {
				System.out.println("The specified audio file is not supported.");
				ex.printStackTrace();
			} catch (LineUnavailableException ex) {
				System.out.println("Audio line for playing back is unavailable.");
				ex.printStackTrace();
			} catch (IOException ex) {
				System.out.println("Error playing the audio file.");
				ex.printStackTrace();
			}
		}

		public void stop() {
			try {
				runSwitch = false;
				audioLine.drain();
				audioLine.close();
				audioStream.close();

				System.out.println("Playback completed.");
			} catch (IOException ex) {
				System.out.println("Error playing the audio file.");
				ex.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				while (runSwitch) {
					while (runSwitch && (bytesRead = audioStream.read(bytesBuffer)) != -1
							&& curByte < endingByte) {
						if (curByte + bytesRead > endingByte)
							bytesRead = (int) (endingByte - curByte);
						curByte += bytesRead;
						audioLine.write(bytesBuffer, 0, bytesRead);
						barrier.await();
					}
					this.audioExitFlag = true;
					barrier.await();
				}
			} catch (IOException ex) {
				System.out.println("Error playing the audio file.");
				ex.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * @return the audioExitFlag
		 */
		public Boolean getAudioExitFlag() {
			return audioExitFlag;
		}

		/**
		 * @return the curByte
		 */
		public int getCurByte() {
			return curByte;
		}

	}

	class ServoPlayer implements Runnable {

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

		public ServoPlayer(int motionsPerCycle, byte[] serialDataStream,
				MicrocontrollerConnection mc, CyclicBarrier barrier, int motionsToSkip,
				long endingMotion) throws SerialPortException {
			this.bytesPerCycle = motionsPerCycle * bytesPerPacket;
			this.motions = serialDataStream;
			this.mc = mc;
			this.barrier = barrier;
			this.endingByte = (int) (endingMotion * bytesPerPacket);
			curByte += motionsToSkip * bytesPerPacket;

			mc.openPort();
		}

		public void stop() throws SerialPortException {
			runSwitch = false;
			mc.closePort();
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				while (runSwitch) {
					while (runSwitch && curByte < motions.length && curByte < endingByte) {
						// System.out.println("RunSwitch: " + runSwitch + "\tcurByte " + curByte
						// + "\tmotions.length " + motions.length);
						// If fewer than bytesPerCycle left, adjust settings to play only what is left
						if (curByte + bytesPerCycle > serialDataStream.length)
							setBytesPerCycle(serialDataStream.length - curByte);
						else if (curByte + bytesPerCycle > endingByte)
							setBytesPerCycle(endingByte - curByte);

						// Thread.sleep(500);

						for (int i = 0; i < bytesPerCycle; i += 2) {
							mc.setTarget(motions[curByte + i], motions[curByte + i + 1]);
						}
						curByte += bytesPerCycle;
						barrier.await();
					}
					this.servoExitFlag = true;
					System.out.println("Servo finished");
					barrier.await();
				}
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			catch (InterruptedException e) {
				for (int i = 0; i < motions.length - showCurSerialByte; i += 2) {

					try {
						mc.setTarget(motions[showCurSerialByte + i], motions[showCurSerialByte + i
								+ 1]);
					} catch (SerialPortException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**
		 * @return the bytesPerCycle
		 */
		public int getBytesPerCycle() {
			return bytesPerCycle;
		}

		/**
		 * @param bytesPerCycle
		 *            the bytesPerCycle to set
		 */
		public void setBytesPerCycle(int bytesPerCycle) {
			this.bytesPerCycle = bytesPerCycle;
		}

		/**
		 * @return the servoExitFlag
		 */
		public Boolean getServoExitFlag() {
			return servoExitFlag;
		}

		/**
		 * @return the curByte
		 */
		public int getCurByte() {
			return curByte;
		}

	}

	class Timer implements Runnable {

		private int millisecondsWait = 0;
		private CyclicBarrier barrier;
		private boolean runSwitch = true;

		public Timer(int cyclesPerSecond, CyclicBarrier barrier) {
			// this.millisecondsWait = 1000 / cyclesPerSecond;
			this.barrier = barrier;
		}

		public void stop() {
			runSwitch = false;
		}

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

	public void pauseShow() {
		pausedShow = true;
	}

	public void resumeShow() {
		// Verify connection still valid or be sure to handle exceptions in
		// threads and exit cleanly
		microConnection.verifyPort();
		pausedShow = false;
	}

	public void stopShow() {
		exitShow = true;

	}

	/*
	 * Implemented using playShow method with start stop times
	 * public void setStartStop() {
	 * // Think about implementing a way to set markers to play only from a
	 * // designated start and stop marker.
	 * // What are the correct units for this?
	 * // Probably shouldn't allow this when show is in active play
	 * // Also tied to construction and creation of servoMotion stream - make
	 * // sure this functionality is in place that makes sense.
	 * }
	 */

}