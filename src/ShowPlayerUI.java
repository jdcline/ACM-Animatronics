import jacs.AnimatronicsShowPlayer;
import jacs.AnimatronicsUtilities;
import jacs.FormattedShowData;
import jacs.PlayerInputs;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jmcc.MicrocontrollerConnection;

public class ShowPlayerUI extends JFrame implements ActionListener {

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

	ShowPlayerUI(AnimatronicsShowPlayer player) {

		this.show = player;

		this.setLayout(new FlowLayout());
		this.setVisible(true);
		this.setSize(400, 200);

		// Create a component
		audioLabel = new JLabel("AudioFile:");
		// audioFile = new JTextField("data/Pop.wav");
		audioFile = new JTextField("data/AlpacaOESISDemo.wav");

		motionLabel = new JLabel("MotionFile:");
		// motionFile = new JTextField("data/Pop30FPS.txt");
		motionFile = new JTextField("data/AlpacaOESISDemo2motor.csv");

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

		this.add(audioLabel); // Add component
		this.add(audioFile);
		this.add(motionLabel);
		this.add(motionFile);
		this.add(playButton);
		this.add(stopButton);
		this.add(pauseButton);
		this.add(resumeButton);
		this.add(message);

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
		// TODO Auto-generated method stub
		String action = e.getActionCommand();

		if (action.equals("PLAY")) {
			byte[] pins = { 3, 4 };
			byte[] recordedPins = { 2 };
			byte[][] motions;
			try {
				motions = AnimatronicsUtilities.readBytesMultipleServo(motionFile.getText(), 2);
				message.setText("playing show..." + motions[0].length);
				show.playShow(new FormattedShowData(audioFile.getText(), pins, motions,
						recordedPins, 5));

				new Thread(new RecordedInputSender(recordedPins[0])).start();

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

	}

	public static void main(String args[]) {
		try {
			MicrocontrollerConnection mc = new MicrocontrollerConnection();
			PlayerInputs inputs = new PlayerInputs(mc, 30, 30);
			AnimatronicsShowPlayer player;
			player = new AnimatronicsShowPlayer(inputs);
			new ShowPlayerUI(player);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
