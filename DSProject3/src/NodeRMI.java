import java.io.File;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class NodeRMI extends UnicastRemoteObject implements NodeInterface {

	private static final long serialVersionUID = 1L;

	private File  _filesDir;
	private ArrayList<FileRegister> _filesList;
	private InetAddress _nodeIp;
	private int _nodePort = 0;
	private String _nodeName;
	private int _loadIndex;
	HostRecord _node;
	HostRecord _server;

	public NodeRMI(InetAddress ip, int port, InetAddress serverIp, int serverPort) throws RemoteException {
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
		_server = new HostRecord(serverIp.getHostAddress(), serverPort);			
			
	}



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

	@Override
	public float getLatency() throws RemoteException {
		
		
		return 0;
	}
	
	
	private void updateLocalFileList() {
		File[] list = _filesDir.listFiles();
		
		_filesList = new ArrayList<FileRegister>();
		
		for(int i = 0; i<list.length; i++){
			_filesList.add(new FileRegister(list[i]));
		}
	}

	private void loadConfigurationFile() {

	}
}
