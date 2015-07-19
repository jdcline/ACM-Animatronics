package tester;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class TestMic {
	AudioFormat format = new AudioFormat(44100, 16, 2, true, true);

	public TestMic() {

		DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);

		try {

			SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(format);
			sourceLine.start();

			byte[] sData = new byte[5000];
			// Byte[] sByteData = new Byte[1];

			MicReader m = new MicReader();
			new Thread(m).start();

			int i = 0;
			while (true) {
				m.dataLock.lock();
				try {
					sData = m.stream.toByteArray();
					m.stream.reset();
				} finally {
					m.dataLock.unlock();
				}

				sourceLine.write(sData, 0, sData.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new TestMic();
	}

	class MicReader implements Runnable {

		int numBytesRead = 0;
		TargetDataLine targetLine;

		byte[] data = new byte[5000];
		Lock dataLock = new ReentrantLock();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		public MicReader() throws LineUnavailableException {
			DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
			targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			targetLine.open(format);
			targetLine.start();
		}

		@Override
		public void run() {
			while (true) {
				numBytesRead = targetLine.read(data, 0, data.length);
				dataLock.lock();
				try {
					stream.write(data);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					dataLock.unlock();
				}
			}

		}

	}

}