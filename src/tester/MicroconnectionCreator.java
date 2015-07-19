package tester;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import jmcc.MicrocontrollerConnection;
import jssc.SerialPortList;

public class MicroconnectionCreator implements ActionListener {

	JFrame frame;
	JComboBox<String> dropDown;
	JButton ok = new JButton("OK");
	MicrocontrollerConnection mc;

	MicroconnectionCreator() {
		dropDown = new JComboBox<String>(SerialPortList.getPortNames());
		ok.addActionListener(this);

		frame = new JFrame("Choose Serial Port");
		frame.setSize(400, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(new FlowLayout());
		frame.add(dropDown);
		frame.add(ok);

		frame.setVisible(true);
		frame.validate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mc = new MicrocontrollerConnection((String) dropDown.getSelectedItem());
		frame.setVisible(false);
		frame.dispose();
	}

	public MicrocontrollerConnection getMC() throws InterruptedException {
		while (mc == null) {
			Thread.sleep(500);
		}
		return mc;
	}
}