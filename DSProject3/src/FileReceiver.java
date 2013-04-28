import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileReceiver extends Thread {

	int udpPort;

	DatagramSocket socket = null;

	final int START_PORT = 2000;
	final int END_PORT = 50000;

	String name;
	int length;
	String checksum;
	String dirPath;
	NodeRMI node;

	public FileReceiver(String name, int length, String checksum, String dirPath, NodeRMI node) {
		findFreePort();

		this.name = name;
		this.length = length;
		this.checksum = checksum;
		this.dirPath = dirPath;
		this.node = node;
	}

	@Override
	public void run() {
		
		node._loadIndex ++;
		
		try {
			// Listen to articles and pings
			byte buffer[] = new byte[length];
			DatagramPacket pkg = new DatagramPacket(buffer, length, null, 0);

			socket.receive(pkg);

			String cks = computeChecksum(buffer);

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
		
		node._loadIndex --;
		
		node.updateLocalFileList();
		try {
			node._server.updateList(node._node, node._filesList);
		} catch (RemoteException e) {
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

	private String computeChecksum(byte[] content) {
		MessageDigest md;
		String cks = "";
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(content);
			byte [] buffer;
			buffer = md.digest();
			for(int i=0; i<buffer.length; i++){
				cks += buffer[i];
			}
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
