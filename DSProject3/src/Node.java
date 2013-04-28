import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.regex.Pattern;



public class Node {

	public static InetAddress ip;
	public static int port;
	public static String name;
	public static String serverIp;
	public static int serverPort;
	private static  final int MIN_DELAY = 500; // Half a second
	private static  final int MAX_DELAY = 1000; // Five seconds
	private static final String IPv4_REGEX = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	private static Pattern IPv4_PATTERN = Pattern.compile(IPv4_REGEX);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Starting node.");
		
		initRMI();
		
		
		// Get my port, server ip and port... read from args, and check port and stuff
		if (args.length != 3) {
			System.out
					.println("Usage: java -jar Node.jar [node port] [server ip] [server port]");
			return;
		}
		
		port = portCheck(args[0]);
		name = ip+":"+port;
		
		serverIp = checkIp(args[1]);
		serverPort = portCheck(args[2]);
		
		System.out.println("IP: "+ip.getHostAddress()+":"+port);
		System.out.println("Server IP: "+serverIp+":"+serverPort);
		
		
		try {
			NodeRMI nodeRMI = new NodeRMI(ip, port, serverIp, serverPort);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		Scanner scan = new Scanner(System.in);
		boolean done = false;
		
		//Registry registry = LocateRegistry.getRegistry(serverIp, serverPort);
		//String serverName = serverIp + ":" + serverPort;
		
		//ServerInterface server = null;
		//server = (ServerInterface) registry.lookup(serverName);
		
		while (!done) {

			try {

				System.out.println("\nChoose the option: \n" + "1) Display files\n"
						+ "2) List local files\n" + "3) Download file\n" + "0) Exit\n");

				String Choice = scan.nextLine();
				Integer Option = Integer.valueOf(Choice);
				System.out.println();
				
				switch (Option) {
				case 1: { // Display files
					System.out.println("Avaliable files: ");
					//NodeRMI.listAllFiles(); 
					break;
				}
				case 2: { // Dispaly local files
					System.out.println("Local files: ");
					//NodeRMI.listLocalFiles();
					break;
				}
				case 3: { // Download
					System.out.println("Enter name of a file:");
					String filename = scan.nextLine();
					//NodeRMI.getFile(filename);
					break;
				}
				case 0: // Exit
					System.out.println("Bye!");
					System.exit(0);
					break;

				}
			} catch (NumberFormatException e) {
			}
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
	private static int portCheck(String s) {

		Scanner scan = new Scanner(System.in);
		int p = 0;
		boolean badRange = true;
		boolean notInt = true;

		if (s.length() == 0)
			p = 1099;
		else {
			while (badRange || notInt) {

				try {
					Integer.valueOf(s);
					notInt = false;
					p = Integer.valueOf(s);
				} catch (NumberFormatException e) {
					notInt = true;
					System.out.println("Port has wrong format try again: ");
					s = scan.nextLine();
				}

				badRange = p < 0 || p > 55901;
				if (badRange) {
					System.out.println("Port is out of range try again: ");
					p = Integer.valueOf(scan.nextLine());
				}
			}
		}
		return p;
	}

	private static String checkIp(String IP) {
		Scanner scan = new Scanner(System.in);
		while (!IPv4_PATTERN.matcher(IP).matches()) {

			System.out.println("IP has wrong format try again: ");
			IP = scan.nextLine();
		}
		return IP;
	}

}
