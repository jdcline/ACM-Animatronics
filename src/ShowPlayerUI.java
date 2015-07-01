import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jmcc.MicrocontrollerConnection;
import showController.AnimatronicsShowPlayer;
import showController.AnimatronicsUtilities;

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
		audioFile = new JTextField("data/WatchMyMouthMove.wav");

		motionLabel = new JLabel("MotionFile:");
		// motionFile = new JTextField("data/Pop30FPS.txt");
		motionFile = new JTextField("data/WatchMyMouthMove30FPS.txt");

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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String action = e.getActionCommand();

		if (action.equals("PLAY")) {
			int[] pins = { 2 };
			byte[][] motions = new byte[1][];
			try {
				motions = AnimatronicsUtilities.readBytesMultipleServo(motionFile.getText(), 1);
				message.setText("playing show..." + motions[0].length);
				show.playShow(audioFile.getText(), pins, motions);

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
		MicrocontrollerConnection mc = new MicrocontrollerConnection();
		AnimatronicsShowPlayer player = new AnimatronicsShowPlayer(mc, 30, 30);
		new ShowPlayerUI(player);
	}

}
