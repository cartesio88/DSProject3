import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Enumeration;

public class Server {

	public static InetAddress ip;
	public static int port;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting server.");

		initRMI();

		// Get my port, server ip and port... read from args, and check port and
		// stuff
		if (args.length != 1) {
			System.out
					.println("Usage: java -jar Server.jar [port]");
			return;
		}
		
		port = Integer.parseInt(args[0]);
		System.out.println("Server IP: "+ip.getHostAddress()+":"+port);
		
		
		try {
			ServerRMI serverRMI = new ServerRMI(ip, port);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	private static void initRMI() {
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
