import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class NodeRMI extends UnicastRemoteObject implements NodeInterface {

	private static final long serialVersionUID = 1L;

	UUID _machId;
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
		
		// Bind local RMI node
		Registry localRegistry = LocateRegistry.createRegistry(port);
		localRegistry.rebind(name, this);
		
		// Bind with the server
		_server = new HostRecord(serverIp.getHostAddress(), serverPort);
		
			
			
	}

	private void getLocalFileList() {

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
