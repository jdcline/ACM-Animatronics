package tester;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Version;

public class gameControllerTest {

	public static void main(String[] args) throws InterruptedException {
		System.out.println("JInput version: " + Version.getVersion());
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] cs = ce.getControllers();
		for (int i = 0; i < cs.length; i++)
			System.out.println(i + ". " + cs[i].getName() + ", " + cs[i].getType());

		System.out.println("\n" + cs[3].getName());
		while (true) {
			cs[3].poll();
			for (int i = 6; i <= 8; i++) {
				System.out.print(cs[3].getComponents()[i].getName() + ": ");
				System.out.println(cs[3].getComponents()[i].getPollData());
			}
			Thread.sleep(300);
		}

		/*
		 * Component[] comps = cs[3].getComponents();
		 * for (int i = 0; i < comps.length; i++) {
		 * System.out.println(i + ". " + comps[i].getName());
		 * }
		 * 
		 * System.out.println("\n" + comps[6].getName());
		 */

	}

}
