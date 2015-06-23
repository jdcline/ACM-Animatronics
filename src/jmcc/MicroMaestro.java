package jmcc;

public class MicroMaestro extends Maestro {

	public MicroMaestro() {
		super("Pololu MicroMaestro", 6);
	}

	public void initializePinInfo(int numberPins) {
		super.initializePinInfo(numberPins);
		setPin(0, new PinInfo());
		setPin(1, new PinInfo());
		setPin(2, new PinInfo());
		setPin(3, new PinInfo());
		setPin(4, new PinInfo());
		setPin(5, new PinInfo());

	}

}