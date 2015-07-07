import jasc.AnimatronicsShowPlayer;
import jasc.AnimatronicsUtilities;
import jasc.FormattedShowData;
import jasc.PlayerInputs;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jmcc.MicrocontrollerConnection;

public class MacTesterUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JLabel audioLabel;
	JTextField audioFile;

	JLabel motionLabel;
	JTextField motionFile;

	JButton playButton;
	JButton stopButton;
	JButton pauseButton;
	JButton resumeButton;

	JLabel message;

	AnimatronicsShowPlayer show;

	JLabel testLabel;
	JComboBox<String> testSelector;
	String[] testOptions = { "2 motor with recorded servo", "3 motor" };
	JButton ok;

	JButton returnButton;

	JTextField servoLagField;

	ArrayList<JComponent> contentArray = new ArrayList<JComponent>();

	MacTesterUI(AnimatronicsShowPlayer player) {

		this.show = player;

		this.setLayout(new FlowLayout());
		this.setVisible(true);
		this.setSize(400, 200);

		testLabel = new JLabel("Choose a test:");
		testSelector = new JComboBox<String>(testOptions);
		ok = new JButton("OK");
		ok.setActionCommand("OK");
		ok.addActionListener(this);

		add(testLabel);
		add(testSelector);
		add(ok);

		this.validate();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	class RecordedInputSender implements Runnable {
		byte pin;

		public RecordedInputSender(byte b) {
			pin = b;
		}

		public void run() {
			boolean check = true;
			for (int i = 0; i < 2000000 && check; i++) {
				check = show.addRecordedServoInput(pin, new byte[] { (byte) i });
				try {
					Thread.sleep(33);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String action = e.getActionCommand();

		if (action.equals("PLAY")) {
			byte[][] motions;
			byte[] pins;
			byte[] recordedPins;
			try {
				switch ((String) testSelector.getSelectedItem()) {
				case "2 motor with recorded servo":
					pins = new byte[] { 2, 3 };
					recordedPins = new byte[] { 4 };
					motions = AnimatronicsUtilities.readBytesMultipleServo(motionFile.getText(), 2);
					message.setText("playing show..." + motions[0].length);
					show.playShow(new FormattedShowData(audioFile.getText(), pins, motions,
							recordedPins, Integer.parseInt(servoLagField.getText())));

					new Thread(new RecordedInputSender(recordedPins[0])).start();
					break;

				case "3 motor":
					pins = new byte[] { 2, 3, 4 };
					recordedPins = new byte[] {};
					motions = AnimatronicsUtilities.readBytesMultipleServo(motionFile.getText(), 3);
					message.setText("playing show..." + motions[0].length);
					show.playShow(new FormattedShowData(audioFile.getText(), pins, motions,
							recordedPins, Integer.parseInt(servoLagField.getText())));
				}

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
				message.setText("File not found" + e1.getMessage());
			} catch (Exception e2) {
				message.setText("Error: " + e2.getMessage());
			}

			System.out.println("play");
			System.out.println("Files:" + audioFile.getText() + " " + motionFile.getText());
		}

		else if (action.equals("STOP")) {
			show.stopShow();
			message.setText("stopping show...");
		}

		else if (action.equals("PAUSE")) {
			show.pauseShow();
			message.setText("pausing show...");
		}

		else if (action.equals("RESUME")) {
			show.resumeShow();
			message.setText("resuming show...");
		}

		else if (action.equals("OK")) {
			String s = (String) testSelector.getSelectedItem();

			createTestUI(s);
		}

		else if (action.equals("Return")) {
			returnToMenu();
		}

	}

	public static void main(String args[]) {
		try {
			MicrocontrollerConnection mc = new MicrocontrollerConnection();
			PlayerInputs inputs = new PlayerInputs(mc, 30, 30);
			AnimatronicsShowPlayer player;
			player = new AnimatronicsShowPlayer(inputs);
			new MacTesterUI(player);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	private void createTestUI(String testOption) {
		for (int i = 0; i < 3; i++) {
			contentArray.get(i).setVisible(false);
		}

		// Create a component
		audioLabel = new JLabel("AudioFile:");
		audioFile = new JTextField();
		audioFile.setEnabled(false);

		motionLabel = new JLabel("MotionFile:");
		motionFile = new JTextField();
		motionFile.setEnabled(false);

		switch (testOption) {
		case "2 motor with recorded servo":
			audioFile.setText("data/AlpacaOESISDemo.wav");
			motionFile.setText("data/AlpacaOESISDemo2motor.csv");
			break;

		case "3 motor":
			audioFile.setText("data/AlpacaOESISDemo.wav");
			motionFile.setText("data/AlpacaOESISDemo.csv");
			break;
		}

		servoLagField = new JTextField(2);
		servoLagField.setText("5");

		playButton = new JButton("PLAY");
		playButton.addActionListener(this);
		playButton.setActionCommand("PLAY");

		stopButton = new JButton("STOP");
		stopButton.addActionListener(this);
		stopButton.setActionCommand("STOP");

		pauseButton = new JButton("PAUSE");
		pauseButton.addActionListener(this);
		pauseButton.setActionCommand("PAUSE");

		resumeButton = new JButton("RESUME");
		resumeButton.addActionListener(this);
		resumeButton.setActionCommand("RESUME");

		message = new JLabel("messages");

		returnButton = new JButton("Return to menu");
		returnButton.setActionCommand("Return");
		returnButton.addActionListener(this);

		this.add(audioLabel); // Add component
		this.add(audioFile);
		this.add(motionLabel);
		this.add(motionFile);
		this.add(servoLagField);
		this.add(playButton);
		this.add(stopButton);
		this.add(pauseButton);
		this.add(resumeButton);
		this.add(message);
		this.add(returnButton);

		this.validate();
	}

	private void returnToMenu() {
		show.stopShow();
		for (int i = contentArray.size() - 1; i >= 0; i--) {
			if (i < 3)
				contentArray.get(i).setVisible(true);
			else
				contentArray.get(i).setVisible(false);
		}

	}

	public Component add(Component j) {
		super.add(j);
		contentArray.add((JComponent) j);
		return j;
	}

}
