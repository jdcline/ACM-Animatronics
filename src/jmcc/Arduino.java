package jmcc;

public abstract class Arduino extends Microcontroller {

	// good place to constants
	public Arduino() {
		super("Arduino - generic");
	}

	public Arduino(String boardName) {
		super(boardName);
	}

}