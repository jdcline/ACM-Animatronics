import java.util.concurrent.CyclicBarrier;

/**
 * @author Jared Cline
 *
 */
public class CyclicBarrierFramework {

	final CyclicBarrier barrier;

	/**
	 * @param threadArray
	 *            all of the parties to be bound to this barrier
	 */
	public CyclicBarrierFramework(Runnable... threadArray) {
		barrier = new CyclicBarrier(threadArray.length, new Runnable() {
			public void run() {
				// for each thread in threadArray, advance show
			}
		});

	}
}