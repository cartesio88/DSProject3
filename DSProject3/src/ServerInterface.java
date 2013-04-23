import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;


public interface ServerInterface extends Remote {
	// First one in array: checksum, rest: nodes (for example...)
	public LinkedList<HostRecord> find(String filename) throws RemoteException;
	
	public void updateList(HostRecord node, ArrayList<FileRegister> list) throws RemoteException;
	
}
