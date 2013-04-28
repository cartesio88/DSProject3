import java.io.File;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class NodeRMI extends UnicastRemoteObject implements NodeInterface {

	private static final long serialVersionUID = 1L;

	private static final int MIN_LATENCY = 500;
	private static final int MAX_LATENCY = 1000;
	
	private File  _filesDir;
	private ArrayList<FileRegister> _filesList;
	private InetAddress _nodeIp;
	private int _nodePort = 0;
	private String _nodeName;
	private int _loadIndex;
	private HashMap<HostRecord, Integer> _latencyTimes;
	HostRecord _node;
	HostRecord _server;

	public NodeRMI(InetAddress ip, int port, String serverIp, int serverPort) throws RemoteException {
		super();

		_nodeIp = ip;
		_nodePort = port;
		_nodeName = ip+":"+port;

		_loadIndex = 0;
		
		String shareDir = System.getProperty("user.home")+"/5105/share/"+_nodeName;
		_filesDir = new File(shareDir);
		
		if(!_filesDir.exists()){
			System.out.println("ERROR locating the share directory "+shareDir);
			return;
		}
		
		updateLocalFileList(); // :)
				
		
		// Bind local RMI node
		Registry localRegistry = LocateRegistry.createRegistry(port);
		localRegistry.rebind(_nodeName, this);
		
		// Create the HostRecord object for this node
		_node = new HostRecord(ip.getHostAddress(), port);
		
		// Bind with the server
		_server = new HostRecord(serverIp, serverPort);			
			
	}



	/* mmmm use UDP to download and parallelize them? maybe tcp socket? */
	
	@Override
	public byte[] download(String filename) throws RemoteException {
		Iterator<FileRegister> itF = _filesList.iterator();
		while(itF.hasNext()){
			FileRegister f = itF.next();
			if(f.getName().equals(filename)) return f.getContent();
		}
		
		System.out.println("ERROR file not found "+filename);
		
		return null;
	}

	
	/*
	 * Instead of using a configuration file (which is not scalable)
	 * a node stores the latency to any other node.
	 * This latency is randomly calculated the first time that two nodes
	 * communicate with each other, and then stored and provided
	 * the same value for following times. 
	 * 
	 */
	@Override
	public int getLatency(HostRecord node) throws RemoteException {
		Integer latency;
		if((latency = _latencyTimes.get(node)) == null){
			latency = (int) Math.random()*(MAX_LATENCY - MIN_LATENCY) + MAX_LATENCY;
			_latencyTimes.put(node, latency);
		}
		
		return latency;
	}
	
	
	private void updateLocalFileList() {
		File[] list = _filesDir.listFiles();
		
		_filesList = new ArrayList<FileRegister>();
		
		for(int i = 0; i<list.length; i++){
			_filesList.add(new FileRegister(list[i]));
		}
	}

}
