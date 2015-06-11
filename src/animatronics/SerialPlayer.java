package animatronics;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * The SerialPlayer class controls one serial motor and send the signals for
 * movement based on the given input array. It is synchronized through the given
 * CyclicBarrier.
 * 
 * @author Jared Cline
 *
 */
public class SerialPlayer extends Player implements Runnable {

	/**
	 * The time needed between movements for the motor to catch up. This time is
	 * applied after each write, before waiting at the barrier.
	 */
	final int CATCHUPTIME = 15;

	/**
	 * The number of movement bytes sent before waiting at the barrier.
	 */
	final int BYTESPERWRITE = 1;

	private SerialPort serialPort;
	private CyclicBarrier barrier;
	private byte[] movements;
	private int currentMovement;
	private byte cardPortNum;

	/**
	 * Creates a SerialPlayer based on the given parameters
	 * 
	 * @param cardPortNum
	 *            the number of the motor's port on the controller card
	 * @param movements
	 *            a sequential array of the motor's positions during the show
	 * @param barrier
	 *            the {@link CyclicBarrier} used to synchronize all of the
	 *            output threads
	 */
	public SerialPlayer(SerialPort serialPort, byte cardPortNum, byte[] movements,
			CyclicBarrier barrier) {
		this.barrier = barrier;
		this.movements = movements;
		this.cardPortNum = cardPortNum;
		this.serialPort = serialPort;

		currentMovement = 0;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		try {
			for (int j = 0; j < movements.length; j++) {
				barrier.await();
				for (int i = 0; i < BYTESPERWRITE; i++) {
					serialPort.writeByte((byte) 255);
					serialPort.writeByte(cardPortNum);
					serialPort.writeByte(movements[currentMovement]);
					currentMovement++;
					Thread.sleep(CATCHUPTIME);
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
