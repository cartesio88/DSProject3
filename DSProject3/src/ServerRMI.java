import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerRMI extends UnicastRemoteObject implements ServerInterface {
	private String _serverName = "";
	private InetAddress _serverIp;
	private int _serverPort = 0;

	protected ServerRMI(InetAddress ip, int port, String name)
			throws RemoteException {
		super();

		_serverIp = ip;
		_serverPort = port;
		_serverName = name;

		// Bind local RMI node
		Registry localRegistry = LocateRegistry.createRegistry(port);
		localRegistry.rebind(name, this);

	}

	private static final long serialVersionUID = 1L;

	@Override
	public String[] find(String filename) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateList(String list) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
