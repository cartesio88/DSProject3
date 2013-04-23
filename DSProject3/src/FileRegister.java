import java.io.File;


public class FileRegister {

	private File _file;
	private String _checksum;
	
	public FileRegister(File file){
		_file = file;
		computeChecksum();
	}
	
	public String getName(){
		return _file.getName();
	}
	
	public String getChecksum(){
		return _checksum;
	}
	
	public String getContent(){
		// TODO
		return "TODO!!!";
	}
	
	private void computeChecksum() {

	}
	
	public boolean equals(Object o){
		FileRegister fr = (FileRegister) o;
		return getName().equals(fr.getName());
	}

}
