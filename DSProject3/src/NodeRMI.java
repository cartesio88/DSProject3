import java.io.File;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class NodeRMI extends UnicastRemoteObject implements NodeInterface {

	private static final long serialVersionUID = 1L;

	private static final int MIN_LATENCY = 500;
	private static final int MAX_LATENCY = 1000;

	private File _filesDir;
	private ArrayList<FileRegister> _filesList;
	private String _nodeName;
	public int _loadIndex;
	private HashMap<NodeRecord, Integer> _latencyTimes;
	NodeRecord _node;
	ServerInterface _server;
	private String _shareDir = "";

	public NodeRMI(InetAddress ip, int port, String serverIp, int serverPort)
			throws RemoteException {
		super();

		_nodeName = ip.getHostAddress() + ":" + port;

		_loadIndex = 0;

		_shareDir = System.getProperty("user.home") + "/5105/share/"
				+ _nodeName;
		_filesDir = new File(_shareDir);
		_filesDir.mkdir();

		if (!_filesDir.exists()) {
			System.out.println("ERROR locating the share directory "
					+ _shareDir);
			return;
		}

		updateLocalFileList(); // :)

		// Bind local RMI node
		Registry localRegistry = LocateRegistry.createRegistry(port);
		localRegistry.rebind(_nodeName, this);

		System.out.println("Binding node with name: " + _nodeName);

		// Create the HostRecord object for this node
		_node = new NodeRecord(ip.getHostAddress(), port);

		// Bind with the server
		Registry serverRegistry;
		try {
			serverRegistry = LocateRegistry.getRegistry(serverIp, serverPort);
			_server = (ServerInterface) serverRegistry.lookup(serverIp + ":"
					+ serverPort);
		} catch (RemoteException e) {
			_server = null;
			e.printStackTrace();
		} catch (NotBoundException e) {
			System.out.println("Server not available " + toString());
			_server = null;
		}

		// Sending the server my list
		_server.updateList(_node, _filesList);

	}

	/*
	 * Instead of using a configuration file (which is not scalable) a node
	 * stores the latency to any other node. This latency is randomly calculated
	 * the first time that two nodes communicate with each other, and then
	 * stored and provided the same value for following times.
	 */
	@Override
	public int getLatency(NodeRecord node) throws RemoteException {
		Integer latency;
		if ((latency = _latencyTimes.get(node)) == null) {
			latency = (int) Math.random() * (MAX_LATENCY - MIN_LATENCY)
					+ MAX_LATENCY;
			_latencyTimes.put(node, latency);
		}

		return latency;
	}

	private void updateLocalFileList() {
		File[] list = _filesDir.listFiles();

		_filesList = new ArrayList<FileRegister>();

		for (int i = 0; i < list.length; i++) {
			_filesList.add(new FileRegister(list[i]));
		}
	}

	/*
	 * Create a FileSender :)
	 */

	@Override
	public void requestDownload(NodeRecord node, String filename, int rcvPort)
			throws RemoteException {
		FileRegister file = null;
		Iterator<FileRegister> itF = _filesList.iterator();
		while (itF.hasNext()) {
			FileRegister f = itF.next();
			System.out.println("Comparing " + f.getName() + " and " + filename);
			if (f.getName().equals(filename)) {
				file = f;
				break;
			}
		}

		if (file == null) {
			System.out.println("ERROR file not found " + filename);
			return;
		}

		FileSender fileSender = new FileSender(file, node.getIP(), rcvPort, this);

		fileSender.start();

	}

	// Calls from Node.java
	// Asks the server, decides and downloads
	public void getFile(String fileName) {
		// Lets check that I dont have the file
		for(int i=0; i<_filesList.size(); i++){
			if(_filesList.get(i).getName().equals(fileName)){
				System.out.println("The file is already in the filesystem");
				return;
			}
		}
		
		try {
			// Get list from server

			LinkedList<NodeRecord> nodesList = _server.find(fileName);

			// Pick up best (latency)
			if (nodesList.size() == 0) {
				System.out.println("File not found!");
				return;
			}

			FileRegister fileInfo = _server.getFileInfo(fileName);

			NodeRecord node = nodesList.get(0);
			node.bind();

			// Create a file receiver
			FileReceiver fileReceiver = new FileReceiver(fileInfo.getName(),
					fileInfo.getLength(), fileInfo.getChecksum(), _shareDir);
			int rcvPort = fileReceiver.getUDPPort();

			fileReceiver.start();

			// Send the request to download to the node
			node.rmi.requestDownload(_node, fileInfo.getName(), rcvPort);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		// That's it ;)
	}

	public void listLocalFiles() {
		for (int i = 0; i < _filesList.size(); i++) {
			System.out.println(_filesList.get(i));
		}
	}

	public void listAllFiles() {
		LinkedList<String> list;
		try {
			list = _server.getFilesList();
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i));
			}
		} catch (RemoteException e) {
			System.out
					.println("ERROR getting the list of files from the server");
			e.printStackTrace();
		}

	}

}
