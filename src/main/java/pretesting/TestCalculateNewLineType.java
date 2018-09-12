package pretesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TestCalculateNewLineType {
	
	private static final String S = "SS14_FINAL_29-10-12_FASTA.fasta";
	
	public static void main(String[] args) throws Exception {
		int i = calculateNewlineType(S);
		System.out.println(i);
	}
	
	/**
	 * @param inputFile
	 * @return
	 * @throws IOException
	 * Calculates the Type of Line of a given inputFile
	 * parses a given file 200 bytes by 200 bytes
	 * returns 1 if the File was saved under Unix
	 * returns 2 if the File was saved under Windows
	 */
	private static int calculateNewlineType(String inputFile) throws IOException {
		int type = -1;
		File f = new File(inputFile);
		for(int skip = 0; skip < f.length(); skip = skip + 200) {
			type = parseBytes(f, skip, type);
			if (type == 3) {
				int secType = parseBytes(f, skip+1, type);
				if (secType != -1) {
					type = 2;
					return type;
				} else {
					type = 1;
					return type;
				}
			}
			if(type != -1) {
				return type;
			}
		}
		return type;
	}

	/**
	 * @param f
	 * @param skip
	 * @param type
	 * @return parses a given file by 200 bytes, with a specified starting point
	 * returns 1 if the character '\n' occurs, else 
	 * it returns 2 (characters '\r' + '\n' occurred)
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private static int parseBytes(File f, int skip, int type) throws IOException {
		int offsetLength = 200; 
		byte[] array = new byte[offsetLength];  
		InputStream in = new FileInputStream(f);  
		int offset = 0;
		while(offset < offsetLength) {
			in.skip(skip);
			offset += in.read(array, offset, (offsetLength - offset));
			for(int i = 0; i < array.length; i++) {
				if(array[i] == '\n') {
					//System.out.println(i+skip);
					type = 1;
					return type;
				}
				if(array[i] == '\r') {
					if (i < array.length-1) {
						if (array[i+1] == '\n') {
							type = 2;
							return type;
						} else {
							type = 1;
							return type;
						}
					} else {
						type = 3;
						return type;
					}
				}
			}
		}
		in.close();
		return type;
		
	}

}
