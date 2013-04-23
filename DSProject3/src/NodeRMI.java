import java.io.File;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class NodeRMI extends UnicastRemoteObject implements NodeInterface {

	private static final long serialVersionUID = 1L;

	private File  _filesDir;
	private String _nodeName = "";
	private InetAddress _nodeIp;
	private int _nodePort = 0;
	private int _loadIndex;
	HostRecord _server;

	public NodeRMI(InetAddress ip, int port, String name, InetAddress serverIp, int serverPort) throws RemoteException {
		super();

		_nodeIp = ip;
		_nodePort = port;
		_nodeName = name;
		
		
		String shareDir = System.getProperty("user.home")+"/5105/share/"+_nodeName;
		_filesDir = new File(shareDir);
		
		if(!_filesDir.exists()){
			System.out.println("ERROR locating the share directory "+shareDir);
			return;
		}
		
		File[] filesList = getLocalFileList();
				
		
		// Bind local RMI node
		Registry localRegistry = LocateRegistry.createRegistry(port);
		localRegistry.rebind(name, this);
		
		// Bind with the server
		_server = new HostRecord(serverIp.getHostAddress(), serverPort);
		
		
			
			
	}

	private File[] getLocalFileList() {
		File[] filesList = _filesDir.listFiles();
		
		for(int i = 0; i<filesList.length; i++){
			System.out.println(filesList[i]);
		}
		
		return filesList;
	}

	private void computerFileChecksum() {

	}

	private void loadConfigurationFile() {

	}

	@Override
	public String download(String filename) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getLatency() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}
}
