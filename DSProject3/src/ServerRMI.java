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
	private int _serverPort ;
	
	// HostRecord -> Files
	private HashMap<NodeRecord, LinkedList<String>> _filesRegister;

	protected ServerRMI(InetAddress ip, int port)
			throws RemoteException {
		super();

		_serverIp = ip;
		_serverPort = port;
		
		String bindingName = ip.getHostAddress()+":"+port;
		
		_filesRegister = new HashMap<NodeRecord, LinkedList<String>>();
	
		// Bind local RMI node
		Registry localRegistry = LocateRegistry.createRegistry(_serverPort);
		localRegistry.rebind(bindingName, this);
		
		System.out.println("Binding server with name: "+bindingName);

	}

	private static final long serialVersionUID = 1L;

	@Override
	public LinkedList<NodeRecord> find(String filename) throws RemoteException {
		LinkedList<NodeRecord> hostList = new LinkedList<NodeRecord>();
		
		Iterator<Entry<NodeRecord, LinkedList<String>>> itH = _filesRegister.entrySet().iterator();
	
		while(itH.hasNext()){
			Entry<NodeRecord, LinkedList<String>> e = itH.next();
			
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
	public void updateList(NodeRecord node, ArrayList<FileRegister> list) throws RemoteException {
		LinkedList<String> l;
		
		System.out.println("Updating the list from "+node);
		
		if((l=_filesRegister.get(node)) == null){
			l = new LinkedList<String>();
			_filesRegister.put(node, l);
		}else{
			l = new LinkedList<String>();
		}
		
		for(int i=0; i<list.size(); i++){
			System.out.println(list.get(i));
			l.add(list.get(i).getName());
		}		
	}
}
