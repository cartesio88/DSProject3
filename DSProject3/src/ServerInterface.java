import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ServerInterface extends Remote {
	// First one in array: checksum, rest: nodes (for example...)
	public String[] find(String filename) throws RemoteException;
	
	public void updateList(String list) throws RemoteException;
	
}
