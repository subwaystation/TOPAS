package pretesting.consensus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TestConstructor {
	
	// the buffered reader with which the input file is read
	private BufferedReader buffReader;
	// the line
	private String line;
		
	public TestConstructor(String inputFile) throws IOException {
		this.buffReader = new BufferedReader(new FileReader(inputFile));
		this.line = readFirstLine();
	}

	private String readFirstLine() throws IOException {
		return this.buffReader.readLine();
	}
	
	public void readNextLine() throws IOException {
		String line = this.buffReader.readLine();
		if (line != null) {
			this.line = line;
		} else {
			this.line = line;
			this.buffReader.close();
		}
	}
	
	public String getLine() {
		return this.line;
	}
 }
