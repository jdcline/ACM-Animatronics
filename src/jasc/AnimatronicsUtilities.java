package jasc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AnimatronicsUtilities {

	public static byte[] readBytesSingleServo(String fileName) throws FileNotFoundException {

		File inputFile = new File(fileName);
		Scanner scanner = new Scanner(inputFile);

		ArrayList<Byte> data = new ArrayList<Byte>();
		String s;

		// Populate ArrayList
		while (scanner.hasNext()) {
			s = scanner.next();
			s = s.trim();
			if (!s.isEmpty()) {
				data.add((byte) Integer.parseInt(s));
			}
		}
		scanner.close();
		return byteArrayListToByteArray(data);
	}

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
	public static byte[][] readBytesMultipleServo(String fileName, int numServos)
			throws FileNotFoundException {

		File inputFile = new File(fileName);
		Scanner scanner = new Scanner(inputFile);
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

		scanner.close();

		// cast to primitive
		byte[][] outputArray = new byte[numServos][outerList.get(0).size()];
		for (int i = 0; i < numServos; i++) {
			for (int j = 0; j < outerList.get(0).size(); j++) {
				outputArray[i][j] = (byte) outerList.get(i).get(j);
			}
		}

		return outputArray;
	}

	// ==============================================================
	// CONVERT between ARRAYLISTS and ARRAYS for various data types
	//
	// ==============================================================

	public static byte[] shortArrayListToByteArray(ArrayList<Short> shortArrayList) {
		byte[] temp = new byte[shortArrayList.size()];
		for (int i = 0; i < temp.length; i++)
			temp[i] = (byte) shortArrayList.get(i).byteValue();
		return temp;
	}

	public static byte[] byteArrayListToByteArray(ArrayList<Byte> indata) {
		byte[] temp = new byte[indata.size()];
		for (int i = 0; i < temp.length; i++)
			temp[i] = (byte) indata.get(i).byteValue();
		return temp;
	}

	public static ArrayList<Short> byteArrayToShortArrayList(byte[] byteArray) {
		ArrayList<Short> temp = new ArrayList<Short>();
		for (int i = 0; i < byteArray.length; i = i + 1000) {
			temp.add((short) byteArray[i]);
		}

		return temp;
	}

	public static byte[] resample(byte[] original, int factor) {
		byte[] temp = new byte[original.length / factor + 1];

		for (int i = 0; i < temp.length; i = i + factor)
			temp[i] = original[i * factor];

		return temp;

	}

	public static void invertByteArray(byte[] array) {

		for (int i = 0; i < array.length; i++) {
			short temp = array[i];
			// array[i] = (byte) ((short)Byte.MAX_VALUE - temp); // Compute as Short since Java uses signed bytes
		}

	}

}
