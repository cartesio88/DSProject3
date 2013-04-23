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
	private InetAddress _serverIp;
	private int _serverPort = 0;
	
	// HostRecord -> Files
	private HashMap<HostRecord, LinkedList<String>> _filesRegister;

	protected ServerRMI(InetAddress ip, int port, String name)
			throws RemoteException {
		super();

		_serverIp = ip;
		_serverPort = port;
	
		// Bind local RMI node
		Registry localRegistry = LocateRegistry.createRegistry(port);
		localRegistry.rebind(name, this);

	}

	private static final long serialVersionUID = 1L;

	@Override
	public LinkedList<HostRecord> find(String filename) throws RemoteException {
		LinkedList<HostRecord> hostList = new LinkedList<HostRecord>();
		
		Iterator<Entry<HostRecord, LinkedList<String>>> itH = _filesRegister.entrySet().iterator();
	
		while(itH.hasNext()){
			Entry<HostRecord, LinkedList<String>> e = itH.next();
			
			Iterator<String> itF = e.getValue().iterator();
			while(itF.hasNext()){
				String f = itF.next();
				if(f.equals(filename)){
					hostList.add(e.getKey());
					break;
				}
			}			
		}		
		
		return hostList;
	}

	@Override
	public void updateList(HostRecord node, ArrayList<FileRegister> list) throws RemoteException {
		LinkedList<String> l;
		
		if((l=_filesRegister.get(node)) == null){
			l = new LinkedList<String>();
			_filesRegister.put(node, l);
		}else{
			l = new LinkedList<String>();
		}
		
		for(int i=0; i<list.size(); i++){
			l.add(list.get(i).getName());
		}		
	}
}
