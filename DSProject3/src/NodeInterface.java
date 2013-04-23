import java.rmi.Remote;
import java.rmi.RemoteException;


public interface NodeInterface extends Remote {
	// Serialize the file as a string (for example), maybe create a wrapper class File
	public  byte[] download(String filename) throws RemoteException;
	
	public float getLatency() throws RemoteException;

}
