package showController;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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

	// bytesPerFrameSerial = xxx; //
	// bytesPerFrameAudio = xxx;
	// bytesPerSecionAdvance = xxx;

	// Cyclic Barrier
	// Number of threads to wait at barrier - computed at show start

	// Timer
	// Whether to use timer or not - Test with it on and off
	// Delay - how long to have timer wait before reaching barrier

	// Serial Data
	byte[] serialDataStream; // Gets created in this class - one long stream of
								// bytes ready to send to serial port

	// Show playback controls - need to establish consistent units
	private int showStart = 0;
	private int showEnd = 0; // Computed as last frame/section
	private int showCurSerialByte = 0; // Keeps track of current byte position
	private int showCurAudioByte = 0;
	private boolean pausedShow = false;
	private boolean exitShow = false;

	private CyclicBarrier barrier;

	public AnimatronicsShowPlayer(MicrocontrollerConnection mcm) {
		microConnection = mcm;
		// Default timing data (tunable and settable)
	}

	public void playShow(String audioFile, byte[][] servoMotions) {
		// Set start and stop positions to allow for start to finish play
		byte[] serialDataStream = createServoMotionStream(servoMotions, 0, servoMotions[0].length);
		startSynchronizedShowTasks();
	}

	public void playShow(String audioFile, byte[][] servoMotions, int startTime, int endTime) {
		// Set start and stop positions (to allow for beginning to end play or
		// segment play)

		byte[] serialDataStream = createServoMotionStream(servoMotions, startTime, endTime);
		startSynchronizedShowTasks();

	}

	public void startSynchronizedShowTasks() {
		// Create CyclicBarrier for 3 tasks threads
		barrier = new CyclicBarrier(3, new Runnable() {
			public void run() {
				advanceShow();
			}
		});

		// Audio
		// Serial
		// Timer
	}

	// Create an interleaved stream of bytes using correct protocol
	/**
	 * Return: byte 2n+1 = pin, byte 2n = movement
	 * 
	 * @param motions
	 * @param start
	 *            start byte number in rectangular 2d input array
	 * @param end
	 *            end byte number in rectangular 2d input array
	 * @return
	 */
	private byte[] createServoMotionStream(byte[][] motions, int start, int end) {
		return serialDataStream;
		// Get correct protocol using MicrocontrollerconnectionManager
		// Interleave bytes

		// adjust timing data based on number of servos
		// - number bytes per second to go with frames per second
		// - time of delay after each servo command

		// Set start and stop positions (to allow for beginning to end play or
		// segment play)

	}

	// routine to advance show that plays when all threads reach barrier
	private void advanceShow() {
		// if not paused
		// Advance pointers through servo byte array
		// Might not be a bad idea to gather some stats using system time

	}

	// Inner runnable class to play audio and wait on CyclicBarrier.

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

		public ServoPlayer(int bytesPerCycle, byte[] motions, MicrocontrollerConnection mc,
				CyclicBarrier barrier) {
			this.bytesPerCycle = bytesPerCycle;
			this.motions = motions;
			this.mc = mc;
			this.barrier = barrier;
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			for (int i = 0; i < bytesPerCycle; i++) {
				try {
					mc.setTarget(motions[showCurSerialByte + 2 * i], motions[showCurSerialByte + 1
							+ 2 * i]);
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

	// Inner runnable class to set Timer and wait on CyclicBarrier? Guarantees a
	// minimum wait time

	public void pauseShow() {
		pausedShow = true;
		try {
			microConnection.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public void resumeShow() {
		// clear isPaused flag used by advanceShow()
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