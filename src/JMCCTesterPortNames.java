import java.util.Arrays;

import jssc.SerialPortList;

/**
 * 
 */

/**
 * @author galford
 *
 */
public class JMCCTesterPortNames {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] ports = SerialPortList.getPortNames();
		System.out.println(Arrays.toString(ports));

	}

}
