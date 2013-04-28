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
	NodeRMI node;

	final int START_PORT = 2000;
	final int END_PORT = 50000;
	
	final int MIN_SEC_DOWNLOAD = 1;
	final int MAX_SEC_DOWNLOAD = 30;

	public FileSender(FileRegister file, String dstIp, int dstPort, NodeRMI node) {
		this.file = file;
		this.dstIp = dstIp;
		this.dstPort = dstPort;
		this.node = node;
	}

	@Override
	public void run() {

		node._loadIndex ++;
		
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
			}
		}

		try {
			long seconds = (long)(Math.random()*MAX_SEC_DOWNLOAD + MIN_SEC_DOWNLOAD);
			System.out.println("["+file.getName()+"] Elapsed time of download "+seconds+" seconds.");
			sleep(seconds*1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
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

		node._loadIndex --;

	}
}