import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

public class ServerRMI extends UnicastRemoteObject implements ServerInterface {
	private int _serverPort;

	// HostRecord -> Files
	private HashMap<NodeRecord, LinkedList<FileRegister>> _filesRegister;

	protected ServerRMI(InetAddress ip, int port) throws RemoteException {
		super();

		_serverPort = port;

		String bindingName = ip.getHostAddress() + ":" + port;

		_filesRegister = new HashMap<NodeRecord, LinkedList<FileRegister>>();

		// Bind local RMI node
		Registry localRegistry = LocateRegistry.createRegistry(_serverPort);
		localRegistry.rebind(bindingName, this);

		System.out.println("Binding server with name: " + bindingName);

	}

	private static final long serialVersionUID = 1L;

	@Override
	public LinkedList<NodeRecord> find(String filename) throws RemoteException {
		System.out.println("Looking for the file "+filename);
		
		LinkedList<NodeRecord> hostList = new LinkedList<NodeRecord>();

		Iterator<Entry<NodeRecord, LinkedList<FileRegister>>> itH = _filesRegister
				.entrySet().iterator();

		while (itH.hasNext()) {
			Entry<NodeRecord, LinkedList<FileRegister>> e = itH.next();

			Iterator<FileRegister> itF = e.getValue().iterator();
			while (itF.hasNext()) {
				FileRegister f = itF.next();
				if (f.getName().equals(filename)) {
					hostList.add(e.getKey());
					break;
				}
			}
		}

		return hostList;
	}

	@Override
	public void updateList(NodeRecord node, ArrayList<FileRegister> list)
			throws RemoteException {
		LinkedList<FileRegister> l;

		//System.out.println("Updating the list from " + node);

		if ((l = _filesRegister.get(node)) == null) {
			l = new LinkedList<FileRegister>();
			_filesRegister.put(node, l);
		} else {
			l = new LinkedList<FileRegister>();
		}

		for (int i = 0; i < list.size(); i++) {
			//System.out.println(list.get(i));
			l.add(list.get(i));
		}
	}

	@Override
	public LinkedList<String> getFilesList() throws RemoteException {

		System.out.println("Sengind the list of files");
		
		LinkedList<String> filesList = new LinkedList<String>();

		Iterator<Entry<NodeRecord, LinkedList<FileRegister>>> itH = _filesRegister
				.entrySet().iterator();

		while (itH.hasNext()) {
			Entry<NodeRecord, LinkedList<FileRegister>> e = itH.next();

			Iterator<FileRegister> itF = e.getValue().iterator();
			while (itF.hasNext()) {
				FileRegister f = itF.next();
				if(!filesList.contains(f.getName())){
					filesList.add(f.getName());
				}
			}
		}

				
		return filesList;
	}

	@Override
	public FileRegister getFileInfo(String filename) throws RemoteException {
		System.out.println("Sending file info: "+filename);
		
		Iterator<Entry<NodeRecord, LinkedList<FileRegister>>> itH = _filesRegister
				.entrySet().iterator();

		while (itH.hasNext()) {
			Entry<NodeRecord, LinkedList<FileRegister>> e = itH.next();

			Iterator<FileRegister> itF = e.getValue().iterator();
			while (itF.hasNext()) {
				FileRegister f = itF.next();
				if (f.getName().equals(filename)) {
					System.out.println("File found!: "+f);
					return f;
				}
			}
		}

		
		System.out.println("File not found!");
		return null;
	}
}
