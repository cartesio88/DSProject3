import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FileSender extends Thread {

	FileRegister file;
	String dstIp;
	int dstPort;

	final int START_PORT = 2000;
	final int END_PORT = 50000;

	public FileSender(FileRegister file, String dstIp, int dstPort) {
		this.file = file;
		this.dstIp = dstIp;
		this.dstPort = dstPort;
	}

	@Override
	public void run() {

		int udpPort = START_PORT;
		boolean portFound = false;
		// Opening the socket
		DatagramSocket socket = null;

		while (!portFound) {
			try {
				socket = new DatagramSocket(udpPort);
				portFound = true;
			} catch (SocketException e) {
				udpPort++;
				if(udpPort == END_PORT) udpPort = START_PORT;
				//e.printStackTrace();
			}
		}

		// Listen to articles and pings
		DatagramPacket pkg;
		try {
			pkg = new DatagramPacket(file.getContent(),
					file.getContent().length, InetAddress.getByName(dstIp), dstPort);
		
			socket.send(pkg);
			
		} catch (UnknownHostException e) {
			System.out.println("ERROR sending the file");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR sending the file");
			e.printStackTrace();
		}

		

	}
}