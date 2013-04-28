import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileRegister implements Serializable {

	private static final long serialVersionUID = 1L;

	File _file;
	private String _name = "";
	private int _length = 0;
	private String _checksum = "";
	private byte[] _content;

	public FileRegister(File file) {
		_file = file;
		load();
	}

	public FileRegister(String name) {
		_name = name;
	}

	public FileRegister(String name, int length, String checksum) {
		_name = name;
		_length = length;
		_checksum = checksum;
	}

	public String getName() {
		return _name;
	}

	public int getLength() {
		return _length;
	}

	public String getChecksum() {
		return _checksum;
	}

	public byte[] getContent() {
		return _content;
	}

	public String toString() {
		return getName() + " Checksum: " + _checksum + " Length: " + _length;
	}

	private void computeChecksum() {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(_content);
			byte[] buffer;
			buffer = md.digest();
			_checksum = "";
			for (int i = 0; i < buffer.length; i++) {
				_checksum += buffer[i];
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println("ERROR calculating the checksum of the file "
					+ _file.getName());
			e.printStackTrace();
		}

	}

	public void load() {
		try {

			_name = _file.getName();
			FileInputStream fis = new FileInputStream(_file.getAbsoluteFile());
			_content = new byte[(int) _file.length()];
			fis.read(_content);
			_length = _content.length;
			fis.close();

			computeChecksum();

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			System.out.println("ERROR reading the file "
					+ _file.getAbsolutePath());
			e.printStackTrace();
		}
		;

	}

	public boolean equals(Object o) {
		FileRegister fr = (FileRegister) o;
		return getName().equals(fr.getName());
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {

		String str = _name + "@" + _checksum + "@" + _length;

		out.writeUTF(str);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		String str = in.readUTF();

		String fields[] = str.split("@");

		_name = fields[0];
		_checksum = fields[1];
		_length = Integer.parseInt(fields[2]);

	}
}
