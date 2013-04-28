import java.rmi.RemoteException;

public class SharedDirWatcher extends Thread {

	NodeRMI node;

	final int SLEEP = 3;

	public SharedDirWatcher(NodeRMI node) {
		this.node = node;
	}

	public void run() {
		// Sending the server my list
		while (true) {
			node.updateLocalFileList(); // :)
			try {
				node._server.updateList(node._node, node._filesList);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			try {
				sleep(SLEEP * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
