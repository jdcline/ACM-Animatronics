/**
 * 
 */
package tester;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import jacs.AnimatronicsShowPlayer;
import jacs.AnimatronicsUtilities;
import jacs.FormattedShowData;
import jacs.PlayerInputs;
import jmcc.MicrocontrollerConnection;
import jssc.SerialPortList;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 * @author Jared
 *
 */
@SuppressWarnings("serial")
public class BigTestGUI extends JFrame implements ActionListener {
	private JPanel configPanel;
	private JLabel lblCardType;
	private JLabel lblNumMotors;
	private JComboBox<String> cardTypeBox;
	private JComboBox<Integer> numMotorsBox;
	private JLabel lblMotorPin1;
	private JTextField textField;
	private JTextField textField_1;
	private JLabel lblMotorPin2;
	private JTextField textField_2;
	private JLabel lblMotorPin3;
	private JTextField textField_3;
	private JLabel lblMotorPin4;
	private JTextField textField_4;
	private JLabel lblMotorPin5;
	private JTextField textField_5;
	private JLabel lblMotorPin6;
	private JComboBox[] motorInputs = new JComboBox[6];

	private DefaultComboBoxModel<String> cardOptions = new DefaultComboBoxModel<String>(
			new String[] { "Pololu", "Arduino" });

	private JLabel[] motorList = new JLabel[6];
	private JTextField[] motorFields = new JTextField[6];
	private JPanel showPanel;
	private JTabbedPane tabbedPane;
	private JLabel lblAudioFile;
	private JTextField audioFileField;
	private JCheckBox chckbxAudioFromFile;
	private JCheckBox chckbxRecordedAudioInput;
	private JLabel lblServoInput;
	private JComboBox<String> comboBox;
	private JLabel lblServoInput_1;
	private JComboBox<String> comboBox_1;
	private JLabel lblServoInput_2;
	private JComboBox<String> comboBox_2;
	private JLabel lblServoInput_3;
	private JComboBox<String> comboBox_3;
	private JLabel lblServoInput_4;
	private JComboBox<String> comboBox_4;
	private JLabel lblServoInput_5;
	private JComboBox<String> comboBox_5;

	private JLabel lblServoFile;
	private JTextField servoFileField;
	private JLabel lblServoLag;
	private JSpinner spinner;
	private JButton btnPlay;
	private JButton btnStop;
	private JButton btnPause;
	private JButton btnResume;
	private JLabel lblPreloadTest;
	private JComboBox<String> chooseTestBox;
	private JLabel lblChooseSerialPort;
	private JComboBox<String> serialPortBox;
	private JLabel lblMessages;
	private JTextArea message;
	private AnimatronicsShowPlayer show;
	private MicrocontrollerConnection mc;
	private boolean showFlag = false;
	private JLabel[] motorInputLabels = new JLabel[6];
	private JScrollPane scrollPane;
	private JButton btnGoToPlayer;
	private JLabel lblServoRecordedInput;
	private JComboBox<String> servoInputControllerBox;
	private JLabel lblControllerAxis;
	private JComboBox servo1AxisBox;
	private JComboBox servo2AxisBox;
	private JComboBox servo3AxisBox;
	private JComboBox servo4AxisBox;
	private JComboBox servo5AxisBox;
	private JComboBox servo6AxisBox;
	private JComboBox[] servoAxesBoxes = new JComboBox[6];
	private ControllerEnvironment controllerEnv;
	private Controller[] controllerList;

	public BigTestGUI() {
		setSize(new Dimension(600, 500));
		setVisible(true);
		setTitle("Animatronics Show Tester");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		configPanel = new JPanel();
		tabbedPane.addTab("Configuration", null, configPanel, null);
		configPanel.setLayout(null);

		lblCardType = new JLabel("Card Type:");
		lblCardType.setBounds(23, 70, 74, 16);
		configPanel.add(lblCardType);

		lblNumMotors = new JLabel("Number of Motors:");
		lblNumMotors.setBounds(23, 101, 125, 16);
		configPanel.add(lblNumMotors);

		cardTypeBox = new JComboBox<String>();
		cardTypeBox.setModel(cardOptions);
		cardTypeBox.setBounds(186, 66, 151, 27);
		configPanel.add(cardTypeBox);

		lblMotorPin1 = new JLabel("Motor 1 Pin #:");
		lblMotorPin1.setBounds(23, 129, 95, 16);
		configPanel.add(lblMotorPin1);

		textField = new JTextField();
		textField.setText("2");
		textField.setBounds(115, 123, 26, 28);
		configPanel.add(textField);
		textField.setColumns(1);

		textField_1 = new JTextField();
		textField_1.setText("3");
		textField_1.setColumns(1);
		textField_1.setBounds(115, 154, 26, 28);
		configPanel.add(textField_1);

		lblMotorPin2 = new JLabel("Motor 2 Pin #:");
		lblMotorPin2.setBounds(23, 160, 95, 16);
		configPanel.add(lblMotorPin2);

		textField_2 = new JTextField();
		textField_2.setText("4");
		textField_2.setColumns(1);
		textField_2.setBounds(115, 188, 26, 28);
		configPanel.add(textField_2);

		lblMotorPin3 = new JLabel("Motor 3 Pin #:");
		lblMotorPin3.setBounds(23, 194, 95, 16);
		configPanel.add(lblMotorPin3);

		textField_3 = new JTextField();
		textField_3.setText("3");
		textField_3.setColumns(1);
		textField_3.setBounds(115, 222, 26, 28);
		configPanel.add(textField_3);

		lblMotorPin4 = new JLabel("Motor 4 Pin #:");
		lblMotorPin4.setBounds(23, 228, 95, 16);
		configPanel.add(lblMotorPin4);

		textField_4 = new JTextField();
		textField_4.setText("4");
		textField_4.setColumns(1);
		textField_4.setBounds(115, 256, 26, 28);
		configPanel.add(textField_4);

		lblMotorPin5 = new JLabel("Motor 5 Pin #:");
		lblMotorPin5.setBounds(23, 262, 95, 16);
		configPanel.add(lblMotorPin5);

		textField_5 = new JTextField();
		textField_5.setText("5");
		textField_5.setColumns(1);
		textField_5.setBounds(115, 290, 26, 28);
		configPanel.add(textField_5);

		lblMotorPin6 = new JLabel("Motor 6 Pin #:");
		lblMotorPin6.setBounds(23, 296, 95, 16);
		configPanel.add(lblMotorPin6);

		motorList[0] = lblMotorPin1;
		motorList[1] = lblMotorPin2;
		motorList[2] = lblMotorPin3;
		motorList[3] = lblMotorPin4;
		motorList[4] = lblMotorPin5;
		motorList[5] = lblMotorPin6;

		motorFields[0] = textField;
		motorFields[1] = textField_1;
		motorFields[2] = textField_2;
		motorFields[3] = textField_3;
		motorFields[4] = textField_4;
		motorFields[5] = textField_5;

		lblPreloadTest = new JLabel("Pre-load Test:");
		lblPreloadTest.setBounds(23, 338, 95, 16);
		configPanel.add(lblPreloadTest);

		chooseTestBox = new JComboBox<String>();
		chooseTestBox.setActionCommand("choose test");
		chooseTestBox.addActionListener(this);
		chooseTestBox.setBounds(125, 334, 186, 27);
		configPanel.add(chooseTestBox);

		lblChooseSerialPort = new JLabel("Choose Serial Port:");
		lblChooseSerialPort.setBounds(23, 37, 125, 16);
		configPanel.add(lblChooseSerialPort);

		serialPortBox = new JComboBox<String>();

		ArrayList<String> portNames = new ArrayList<String>();
		portNames.add("Select serial port");
		String[] inputNames = SerialPortList.getPortNames();
		for (String s : inputNames) {
			portNames.add(s);
		}
		String[] comboBoxInput = new String[1];
		comboBoxInput = portNames.toArray(comboBoxInput);
		serialPortBox.setModel(new DefaultComboBoxModel<String>(comboBoxInput));

		serialPortBox.setActionCommand("choose serial");
		serialPortBox.addActionListener(this);
		serialPortBox.setBounds(186, 33, 203, 27);
		configPanel.add(serialPortBox);

		showPanel = new JPanel();
		tabbedPane.addTab("Player", null, showPanel, null);
		showPanel.setLayout(null);

		lblAudioFile = new JLabel("Audio File:");
		lblAudioFile.setBounds(190, 12, 72, 16);
		showPanel.add(lblAudioFile);

		audioFileField = new JTextField();
		audioFileField.setBounds(263, 6, 193, 28);
		showPanel.add(audioFileField);
		audioFileField.setColumns(10);

		chckbxAudioFromFile = new JCheckBox("Audio from file");
		chckbxAudioFromFile.setSelected(true);
		chckbxAudioFromFile.setBounds(6, 6, 128, 23);
		showPanel.add(chckbxAudioFromFile);

		chckbxRecordedAudioInput = new JCheckBox("Recorded audio input");
		chckbxRecordedAudioInput.setBounds(6, 46, 172, 23);
		showPanel.add(chckbxRecordedAudioInput);

		lblServoInput = new JLabel("Servo 1 input:");
		lblServoInput.setBounds(16, 130, 97, 16);
		showPanel.add(lblServoInput);

		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] { "File", "Recorded" }));
		comboBox.setBounds(125, 126, 119, 27);
		showPanel.add(comboBox);

		lblServoInput_1 = new JLabel("Servo 2 input:");
		lblServoInput_1.setBounds(16, 162, 97, 16);
		showPanel.add(lblServoInput_1);

		comboBox_1 = new JComboBox<String>();
		comboBox_1.setModel(new DefaultComboBoxModel<String>(new String[] { "File", "Recorded" }));
		comboBox_1.setBounds(125, 158, 119, 27);
		showPanel.add(comboBox_1);

		lblServoInput_2 = new JLabel("Servo 3 input:");
		lblServoInput_2.setBounds(16, 194, 97, 16);
		showPanel.add(lblServoInput_2);

		comboBox_2 = new JComboBox<String>();
		comboBox_2.setModel(new DefaultComboBoxModel<String>(new String[] { "File", "Recorded" }));
		comboBox_2.setBounds(125, 190, 119, 27);
		showPanel.add(comboBox_2);

		lblServoInput_3 = new JLabel("Servo 4 input:");
		lblServoInput_3.setBounds(16, 226, 97, 16);
		showPanel.add(lblServoInput_3);

		comboBox_3 = new JComboBox<String>();
		comboBox_3.setModel(new DefaultComboBoxModel<String>(new String[] { "File", "Recorded" }));
		comboBox_3.setBounds(125, 222, 119, 27);
		showPanel.add(comboBox_3);

		lblServoInput_4 = new JLabel("Servo 5 input:");
		lblServoInput_4.setBounds(16, 258, 97, 16);
		showPanel.add(lblServoInput_4);

		comboBox_4 = new JComboBox<String>();
		comboBox_4.setModel(new DefaultComboBoxModel<String>(new String[] { "File", "Recorded" }));
		comboBox_4.setBounds(125, 254, 119, 27);
		showPanel.add(comboBox_4);

		lblServoInput_5 = new JLabel("Servo 6 input:");
		lblServoInput_5.setBounds(16, 290, 97, 16);
		showPanel.add(lblServoInput_5);

		comboBox_5 = new JComboBox<String>();
		comboBox_5.setModel(new DefaultComboBoxModel<String>(new String[] { "File", "Recorded" }));
		comboBox_5.setBounds(125, 286, 119, 27);
		showPanel.add(comboBox_5);

		lblServoFile = new JLabel("Servo File:");
		lblServoFile.setBounds(16, 328, 72, 16);
		showPanel.add(lblServoFile);

		servoFileField = new JTextField();
		servoFileField.setBounds(125, 322, 182, 28);
		showPanel.add(servoFileField);
		servoFileField.setColumns(10);

		lblServoLag = new JLabel("Servo Lag:");
		lblServoLag.setBounds(343, 328, 72, 16);
		showPanel.add(lblServoLag);

		spinner = new JSpinner();
		spinner.setValue(14);
		spinner.setBounds(427, 322, 51, 28);
		showPanel.add(spinner);

		btnPlay = new JButton("Play");
		btnPlay.setActionCommand("play");
		btnPlay.addActionListener(this);
		btnPlay.setBounds(49, 358, 117, 29);
		showPanel.add(btnPlay);

		btnStop = new JButton("Stop");
		btnStop.setActionCommand("stop");
		btnStop.addActionListener(this);
		btnStop.setBounds(170, 358, 117, 29);
		showPanel.add(btnStop);

		btnPause = new JButton("Pause");
		btnPause.setActionCommand("pause");
		btnPause.addActionListener(this);
		btnPause.setBounds(292, 358, 117, 29);
		showPanel.add(btnPause);

		btnResume = new JButton("Resume");
		btnResume.setActionCommand("resume");
		btnResume.addActionListener(this);
		btnResume.setBounds(414, 358, 117, 29);
		showPanel.add(btnResume);

		lblMessages = new JLabel("Messages:");
		lblMessages.setBounds(16, 399, 72, 16);
		showPanel.add(lblMessages);

		motorInputs[0] = comboBox;
		motorInputs[1] = comboBox_1;
		motorInputs[2] = comboBox_2;
		motorInputs[3] = comboBox_3;
		motorInputs[4] = comboBox_4;
		motorInputs[5] = comboBox_5;

		motorInputLabels[0] = lblServoInput;
		motorInputLabels[1] = lblServoInput_1;
		motorInputLabels[2] = lblServoInput_2;
		motorInputLabels[3] = lblServoInput_3;
		motorInputLabels[4] = lblServoInput_4;
		motorInputLabels[5] = lblServoInput_5;

		for (int i = 0; i < motorInputs.length; i++) {
			motorInputs[i].setActionCommand("motorInput" + i);
			motorInputs[i].addActionListener(this);
		}

		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(87, 396, 457, 30);

		message = new JTextArea();
		message.setEditable(false);
		message.setLineWrap(true);
		message.setWrapStyleWord(true);
		message.setBounds(87, 349, 457, 27);
		scrollPane.setViewportView(message);
		showPanel.add(scrollPane);

		lblServoRecordedInput = new JLabel("Servo Recorded Input Controller:");
		lblServoRecordedInput.setBounds(343, 130, 215, 16);
		showPanel.add(lblServoRecordedInput);

		servoInputControllerBox = new JComboBox<String>();
		servoInputControllerBox.setBounds(343, 158, 215, 27);
		ArrayList<String> controllerNames = new ArrayList<String>();
		controllerNames.add("Select controller");
		controllerEnv = ControllerEnvironment.getDefaultEnvironment();
		controllerList = controllerEnv.getControllers();
		for (Controller c : controllerList) {
			controllerNames.add(c.getName());
		}
		String[] controllerBoxInput = new String[1];
		controllerBoxInput = controllerNames.toArray(controllerBoxInput);
		servoInputControllerBox.setModel(new DefaultComboBoxModel<String>(controllerBoxInput));
		servoInputControllerBox.setActionCommand("servoInputController");
		servoInputControllerBox.addActionListener(this);
		showPanel.add(servoInputControllerBox);

		lblControllerAxis = new JLabel("Controller Axis");
		lblControllerAxis.setBounds(241, 98, 97, 16);
		showPanel.add(lblControllerAxis);

		servo1AxisBox = new JComboBox();
		servo1AxisBox.setBounds(255, 126, 63, 27);
		showPanel.add(servo1AxisBox);

		servo2AxisBox = new JComboBox();
		servo2AxisBox.setBounds(255, 158, 63, 27);
		showPanel.add(servo2AxisBox);

		servo3AxisBox = new JComboBox();
		servo3AxisBox.setBounds(255, 190, 63, 27);
		showPanel.add(servo3AxisBox);

		servo4AxisBox = new JComboBox();
		servo4AxisBox.setBounds(255, 222, 63, 27);
		showPanel.add(servo4AxisBox);

		servo5AxisBox = new JComboBox();
		servo5AxisBox.setBounds(255, 254, 63, 27);
		showPanel.add(servo5AxisBox);

		servo6AxisBox = new JComboBox();
		servo6AxisBox.setBounds(255, 286, 63, 27);
		showPanel.add(servo6AxisBox);

		servoAxesBoxes[0] = servo1AxisBox;
		servoAxesBoxes[1] = servo2AxisBox;
		servoAxesBoxes[2] = servo3AxisBox;
		servoAxesBoxes[3] = servo4AxisBox;
		servoAxesBoxes[4] = servo5AxisBox;
		servoAxesBoxes[5] = servo6AxisBox;

		numMotorsBox = new JComboBox<Integer>();
		numMotorsBox.setActionCommand("num motors");
		numMotorsBox.addActionListener(this);
		numMotorsBox.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 1, 2, 3, 4, 5, 6 }));
		numMotorsBox.setSelectedIndex(0);
		numMotorsBox.setBounds(186, 97, 58, 27);
		configPanel.add(numMotorsBox);

		btnGoToPlayer = new JButton("Go to Player");
		btnGoToPlayer.setActionCommand("go to player");
		btnGoToPlayer.addActionListener(this);
		btnGoToPlayer.setBounds(228, 368, 117, 29);
		configPanel.add(btnGoToPlayer);

		configPanel.repaint();
		showPanel.repaint();
		validate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
			case "num motors":
				displayMotorFields((int) numMotorsBox.getSelectedItem());
				break;
			case "choose test":
				break;
			case "choose serial":
				try {
					mc = new MicrocontrollerConnection((String) serialPortBox.getSelectedItem());
					show = new AnimatronicsShowPlayer(new PlayerInputs(mc, 30, 30));
					showFlag = true;
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				break;
			case "play":
				if (showFlag) {
					byte[][] motions;
					byte[] pins = new byte[6];
					byte[] recordedPins;
					ArrayList<Byte> recordedPinList = new ArrayList<Byte>();
					try {
						int counter1 = 0;
						for (int i = 0; i < motorInputs.length && i < (int) numMotorsBox.getSelectedItem(); i++) {
							if (((String) motorInputs[i].getSelectedItem()).equals("File")) {
								pins[counter1] = Byte.parseByte(motorFields[i].getText());
								counter1++;
							} else if (((String) motorInputs[i].getSelectedItem()).equals("Recorded")) {
								recordedPinList.add(Byte.parseByte(motorFields[i].getText()));
							}
						}
						pins = Arrays.copyOfRange(pins, 0, counter1);
						recordedPins = AnimatronicsUtilities.byteArrayListToByteArray(recordedPinList);
						motions = AnimatronicsUtilities.readBytesMultipleServo(servoFileField.getText(), pins.length);
						message.append("\nPlaying show..." + motions[0].length);
						if (!chckbxAudioFromFile.isSelected())
							audioFileField.setText("");
						show.playShow(new FormattedShowData(audioFileField.getText(), pins, motions, recordedPins,
								(int) spinner.getValue()));

						for (int i = 0; i < motorInputs.length; i++) {
							if (((String) motorInputs[i].getSelectedItem()).equals("Recorded")) {
								new Thread(new RecordedServoInputSender(recordedPinList.get(0), i)).start();
								recordedPinList.remove(0);
							}
						}

						if (chckbxRecordedAudioInput.isSelected())
							new Thread(new RecordedAudioInputSender()).start();

					} catch (FileNotFoundException e1) {
						message.append("\nFile not found");
					} catch (Exception e1) {
						message.append("\nError: " + e1.getMessage());
						e1.printStackTrace();
					}
				} else
					message.append("\nPlease select serial port");
				break;
			case "pause":
				if (showFlag) {
					show.pauseShow();
					message.append("\nPausing show...");
				} else
					message.append("\nPlease select serial port");
				break;
			case "stop":
				if (showFlag) {
					show.stopShow();
					message.append("\nStopping show...");
				} else
					message.append("\nPlease select serial port");
				break;
			case "resume":
				if (showFlag) {
					show.resumeShow();
					message.setText("\nResuming show...");
				} else
					message.append("\nPlease select serial port");
				break;
			case "go to player":
				tabbedPane.setSelectedIndex(1);
				break;
			case "servoInputController":
				ArrayList<String> axesNames = new ArrayList<String>();
				for (Component c : controllerList[servoInputControllerBox.getSelectedIndex() - 1].getComponents()) {
					axesNames.add(c.getName());
				}
				String[] modelArray = new String[axesNames.size()];
				modelArray = axesNames.toArray(modelArray);
				for (int i = 0; i < servoAxesBoxes.length; i++) {
					servoAxesBoxes[i].setModel(new DefaultComboBoxModel(modelArray));
				}
				break;
		}

	}

	private void displayMotorFields(int numToDisplay) {
		for (int i = 0; i < motorList.length; i++) {
			if (i > numToDisplay - 1) {
				motorList[i].setVisible(false);
				motorFields[i].setVisible(false);
				motorInputs[i].setVisible(false);
				motorInputLabels[i].setVisible(false);
				servoAxesBoxes[i].setVisible(false);
			} else {
				motorList[i].setVisible(true);
				motorFields[i].setVisible(true);
				motorInputs[i].setVisible(true);
				motorInputLabels[i].setVisible(true);
				servoAxesBoxes[i].setVisible(true);
			}
		}

	}

	class RecordedServoInputSender implements Runnable {
		byte pin;
		private int servoNumber;

		public RecordedServoInputSender(byte pin, int servoNumber) {
			this.pin = pin;
			this.servoNumber = servoNumber;
		}

		@Override
		public void run() {
			boolean check1 = true;
			boolean check2 = true;
			for (int i = 0; i < 2000000 && check1 && check2; i++) {
				try {
					check2 = controllerList[servoInputControllerBox.getSelectedIndex() - 1].poll();
					check1 = show.addRecordedServoInput(pin, new byte[] { (byte) (127
							+ Math.round(127 * controllerList[servoInputControllerBox.getSelectedIndex() - 1]
									.getComponents()[servoAxesBoxes[servoNumber].getSelectedIndex()].getPollData())) });
					Thread.sleep(33);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	class RecordedAudioInputSender implements Runnable {

		boolean showPlaying = true;

		AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
		DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
		TargetDataLine targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);

		public RecordedAudioInputSender() throws LineUnavailableException {
			targetLine.open(format);
			targetLine.start();

		}

		@Override
		public void run() {
			boolean check = true;

			int numBytesRead;
			byte[] targetData = new byte[targetLine.getBufferSize() / 5];

			while (showPlaying && check) {
				numBytesRead = targetLine.read(targetData, 0, targetData.length);

				if (numBytesRead == -1)
					break;

				try {
					check = show.addRecordedAudioInput(targetData);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public static void main(String[] args) {
		new BigTestGUI();
	}
}
