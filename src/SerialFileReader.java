import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 */

/**
 * @author Jared
 *
 */
public class SerialFileReader {

	private Scanner scanner;
	private File inputFile;

	/**
	 * This method will take a csv file of servo motions, listed in columns, and put it into a 2D array of bytes
	 * 
	 * @param fileName
	 *            the name of the input file
	 * @param numServos
	 *            the number of columns (motors) in the file
	 * @return a 2D array of motions, one array per motor
	 * @throws FileNotFoundException
	 */
	public byte[][] getBytes(String fileName, int numServos) throws FileNotFoundException {

		inputFile = new File(fileName);
		scanner = new Scanner(inputFile);
		scanner.useDelimiter(",");

		// Build 2D ArrayList of Bytes
		ArrayList<ArrayList<Byte>> outerList = new ArrayList<ArrayList<Byte>>();
		for (int i = 0; i < numServos; i++) {
			outerList.add(new ArrayList<Byte>());
		}

		// Populate ArrayList
		int counter = 0;
		while (scanner.hasNext()) {
			String s = scanner.next();
			s = s.replace(',', ' ').trim();
			if (!s.isEmpty()) {
				outerList.get(counter).add((byte) Integer.parseInt(s));
				counter++;

				if (counter == numServos) {
					counter = 0;
				}
			}
		}

		// cast to primitive
		byte[][] outputArray = new byte[numServos][outerList.get(0).size()];
		for (int i = 0; i < numServos; i++) {
			for (int j = 0; j < outerList.get(0).size(); j++) {
				outputArray[i][j] = (byte) outerList.get(i).get(j);
			}
		}

		return outputArray;
	}
}
