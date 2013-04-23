import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Enumeration;


public class Node {

	public static InetAddress ip;
	public static int port;
	public static String name;
	public static InetAddress serverIp;
	public static int serverPort;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting node.");
		
		initRMI();
		
		// Get my port, name, server ip and port... read from args, and check port and stuff
		port = 3030;
		name = ip+":"+port;
		
		
		try {
			NodeRMI nodeRMI = new NodeRMI(ip, port, serverIp, serverPort);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void initRMI(){
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.rmi.server.codebase", "file:./bin");
		System.setProperty("java.security.policy", "file:./policyfile");
		
		ip = getInterfaceIP();
	}
	
	private static InetAddress getInterfaceIP() {
		InetAddress ip = null;
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface
					.getNetworkInterfaces();

			while (nets.hasMoreElements()) {
				NetworkInterface ni = nets.nextElement();
				if (!ni.isLoopback() && ni.isUp()) {
					ip = ni.getInetAddresses().nextElement();
					break;
				}
			}
			System.setProperty("java.rmi.server.hostname", ip.getHostAddress());

		} catch (SocketException e) {
			System.out.println("ERROR getting the interfaces of the device");
			e.printStackTrace();
		}
		return ip;
	}
}
