import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileRegister {

	private File _file;
	private byte[] _checksum;
	private byte[] _content;
	
	public FileRegister(File file){
		_file = file;
		loadContent();
		computeChecksum();
	}
	
	public String getName(){
		return _file.getName();
	}
	
	public byte[] getChecksum(){
		return _checksum;
	}
	
	public byte[]  getContent(){
		return _content;
	}
	
	private void computeChecksum() {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(_content);
			_checksum = md.digest();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("ERROR calculating the checksum of the file "+_file.getName());
			e.printStackTrace();
		}
		
	}
	
	private void loadContent(){
		try {
			FileInputStream fis = new FileInputStream(_file.getAbsoluteFile());
			_content = new byte[(int) _file.length()];
			fis.read(_content);
			fis.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR loading the file "+_file.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR reading the file "+_file.getAbsolutePath());
			e.printStackTrace();
		};
		
	}
	
	public boolean equals(Object o){
		FileRegister fr = (FileRegister) o;
		return getName().equals(fr.getName());
	}

}
