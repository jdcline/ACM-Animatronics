package animatronics1;

public class ShowControllerRunner {

	public static void main(String[] args) {
		byte[] portNums = { 2 };
		byte[][] movements = new byte[1][255];
		for (int i = 0; i < 255; i++) {
			movements[0][i] = (byte) (254 - i);
			// movements[1][i] = (byte) (254 - i);
		}
		new AnimatronicsShowPlayer(portNums, movements);

	}

}
