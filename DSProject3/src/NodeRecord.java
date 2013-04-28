

import java.io.IOException;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NodeRecord implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String ip;
	private int port;
	private String name;
	public NodeInterface rmi = null;
	
	public NodeRecord(String ip, int port){
		this.ip = ip;
		this.port = port;
		this.name = ip+":"+port;
		bind();
	}
	
	public String getIP(){return ip;}
	public int getPort(){return port;}
	public String getBiningName(){ return name; }
	public NodeInterface getRMI() { return rmi; }
	
	public void setIP(String ip){this.ip = ip;}
	public void setPort(int port){this.port = port;}
	
	public boolean bind(){
		Registry registry;
		try {
			registry = LocateRegistry.getRegistry(ip, port);
			rmi = (NodeInterface) registry.lookup(name);
		} catch (RemoteException e) {
			rmi = null;
			e.printStackTrace();
			return false;
		} catch (NotBoundException e) {
			System.out.println("Server not available "+toString());
			rmi = null;
			return false;
		}
		return true;
	}
	
	public String toString(){
		return name+"@"+ip+":"+port;
	}
	
	public boolean equals(Object o){
		NodeRecord c = (NodeRecord) o;
		return ip.equals(c.getIP()) && port == c.getPort();
	}
	
	 private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException{
		 
		 
		 String str = ip+"@"+port+"@"+name;
		 
		 System.out.println("[NodeRecord] write object: "+str);
		 
		 out.writeUTF(str);
	 }
	 
	 private void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException{
		 		 
		 String str = in.readUTF();
		 
		 System.out.println("[NodeRecord] Serializable readObject: "+str);

		 
		 
		 String fields[] = str.split("@");
		 
		 ip = fields[0];
		 port = Integer.parseInt(fields[1]);
		 name = fields[2];
		 
	 }
		 
}
