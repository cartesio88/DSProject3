import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileReceiver implements Runnable {

	int udpPort;

	DatagramSocket socket = null;

	final int START_PORT = 2000;
	final int END_PORT = 50000;

	String name;
	int length;
	byte[] checksum;
	String dirPath;

	public FileReceiver(String name, int length, byte[] checksum, String dirPath) {
		findFreePort();

		this.name = name;
		this.length = length;
		this.checksum = checksum;
		this.dirPath = dirPath;
	}

	@Override
	public void run() {
		try {
			// Listen to articles and pings
			byte buffer[] = new byte[length];
			DatagramPacket pkg = new DatagramPacket(buffer, length, null, 0);

			socket.receive(pkg);

			byte[] cks = computeChecksum(buffer);

			// Check checksum
			if (!checksum.equals(cks)) {
				System.out.println("ERROR Checksums are different! for file "
						+ name);
			}

			// Write file
			writeFile(buffer);

		} catch (IOException e) {
			System.out.println("ERROR receiving the file");
			e.printStackTrace();
		}

	}

	private void findFreePort() {
		udpPort = START_PORT;
		boolean portFound = false;

		while (!portFound) {
			try {
				socket = new DatagramSocket(udpPort);
				portFound = true;
			} catch (SocketException e) {
				udpPort++;
				if (udpPort == END_PORT)
					udpPort = START_PORT;
			}
		}

	}

	private byte[] computeChecksum(byte[] content) {
		MessageDigest md;
		byte[] cks = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(content);
			cks = md.digest();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("[FileReceiver] ERROR calculating the checksum");
			e.printStackTrace();
		}

		return cks;

	}

	private void writeFile(byte[] buffer) {
		try {
			File f = new File(dirPath + "/" + name);

			FileOutputStream fop = new FileOutputStream(f);
			
			fop.write(buffer);
			fop.close();
			
			System.out.println("File "+name+" saved succesfully! :)");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getUDPPort() {
		return udpPort;
	}
}
