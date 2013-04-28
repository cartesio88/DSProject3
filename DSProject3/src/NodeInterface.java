import java.rmi.Remote;
import java.rmi.RemoteException;


public interface NodeInterface extends Remote {
	// Serialize the file as a string (for example), maybe create a wrapper class File
	public  void requestDownload(NodeRecord node, String filename, int rcvPort) throws RemoteException;
	
	public int getLatency(NodeRecord node) throws RemoteException;

}
