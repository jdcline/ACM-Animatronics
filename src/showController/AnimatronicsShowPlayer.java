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
 * @author Jared
 */
public class AnimatronicsShowPlayer {
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

	private static final int AUDIO_BUFFER_SIZE = 32000;

	public AnimatronicsShowPlayer(MicrocontrollerConnection mcm) {
		microConnection = mcm;
		// Default timing data (tunable and settable)
	}

	public void playShow(String audioFile, int[] pinNumbers, byte[][] servoMotions) {
		// Set start and stop positions to allow for start to finish play
		try {
			serialDataStream = createServoMotionStream(pinNumbers, servoMotions, 0,
					servoMotions[0].length);
			startSynchronizedShowTasks(audioFile);
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
			serialDataStream = createServoMotionStream(pinNumbers, servoMotions, startTime, endTime);
			startSynchronizedShowTasks(audioFile);
		} catch (SerialPortException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			// Array mismatch
			e.printStackTrace();
		}

	}

	public void startSynchronizedShowTasks(String audioFile) throws SerialPortException {
		barrier = new CyclicBarrier(numBarrierThreads, new Runnable() {
			public void run() {
				advanceShow();
			}
		});

		// Audio

		microConnection.openPort();
		ServoPlayer servo = new ServoPlayer(timingSettings.getServoBytesPerCycle(),
				serialDataStream, microConnection, barrier);

		Timer timer = new Timer(timingSettings.getMillisecondsPerCycle(), barrier);

		AudioPlayer audio = new AudioPlayer(barrier, AUDIO_BUFFER_SIZE);

		servoThread = new Thread(servo);
		servoThread.start();
		timerThread = new Thread(timer);
		timerThread.start();
		audio.play(audioFile);
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

			// adjust timing data based on number of servos
			timingSettings.setNumServos(pinNumbers.length);

			// Set start and stop positions (to allow for beginning to end play or
			// segment play)

			return returnArray;
		}

	}

	// routine to advance show that plays when all threads reach barrier
	private void advanceShow() {
		if (!pausedShow) {
			// Advance pointers through servo byte array
			showCurSerialByte += timingSettings.getServoBytesPerCycle();
			showCurAudioByte += AUDIO_BUFFER_SIZE;

			// Note: servoBytesPerCycle should be a factor of serialDataStream
			if (showCurSerialByte + timingSettings.getServoBytesPerCycle() > serialDataStream.length) {
				exitShow = true;
			}
		}
		// Might not be a bad idea to gather some stats using system time
	}

	class AudioPlayer implements Runnable {
		private int bufferSize;
		private byte[] bytesBuffer = new byte[bufferSize];
		private int bytesRead = -1;
		private AudioInputStream audioStream;
		private SourceDataLine audioLine;
		private CyclicBarrier barrier;

		public AudioPlayer(CyclicBarrier barrier, int bufferSize) {
			this.barrier = barrier;
			this.bufferSize = bufferSize;
		}

		/**
		 * Play a given audio file.
		 * 
		 * @param audioFilePath
		 *            Path of the audio file.
		 */
		void play(String audioFilePath) {
			File audioFile = new File(audioFilePath);
			try {
				audioStream = AudioSystem.getAudioInputStream(audioFile);

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
				while ((bytesRead = audioStream.read(bytesBuffer)) != -1) {
					audioLine.write(bytesBuffer, 0, bytesRead);
					audioLine.flush();
					synchronized (audioLine) {
						barrier.await();
					}
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
	}

	class ServoPlayer implements Runnable {

		private int bytesPerCycle;
		private byte[] motions;
		private MicrocontrollerConnection mc;
		private CyclicBarrier barrier;

		/**
		 * The time needed between movements for the motor to catch up. This time is
		 * applied after each write, before waiting at the barrier.
		 */
		private final int CATCH_UP_TIME = 15;

		public ServoPlayer(int bytesPerCycle, byte[] serialDataStream,
				MicrocontrollerConnection mc, CyclicBarrier barrier) {
			this.bytesPerCycle = bytesPerCycle;
			this.motions = serialDataStream;
			this.mc = mc;
			this.barrier = barrier;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (!exitShow) {
				for (int i = 0; i < bytesPerCycle; i += 2) {
					try {
						mc.setTarget(motions[showCurSerialByte + i], motions[showCurSerialByte + i
								+ 1]);
						Thread.sleep(CATCH_UP_TIME);
						barrier.await();
					} catch (SerialPortException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}

	}

	class Timer implements Runnable {

		private int millisecondsWait;
		private CyclicBarrier barrier;

		public Timer(int millisecondsWait, CyclicBarrier barrier) {
			this.millisecondsWait = millisecondsWait;
			this.barrier = barrier;
		}

		public void run() {
			try {
				wait(millisecondsWait);
				barrier.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void pauseShow() {
		pausedShow = true;
		try {
			microConnection.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public void resumeShow() {
		// clear isPaused flag used by advanceShow() and then advanceShow()
		// Verify connection still valid or be sure to handle exceptions in
		// threads and exit cleanly
	}

	public void stopShow() {
		// Advance markers to stop playing after current frame - flag for
		// advanceShow() ?

	}

	public void setStartStop() {
		// Think about implementing a way to set markers to play only from a
		// designated start and stop marker.
		// What are the correct units for this?
		// Probably shouldn't allow this when show is in active play
		// Also tied to construction and creation of servoMotion stream - make
		// sure this functionality is in place that makes sense.
	}

}