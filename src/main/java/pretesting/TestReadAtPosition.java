package pretesting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestReadAtPosition {

	private static final String Test = "test.fasta";
	private static final String STAPHYLOCUCCUS_AUREUS = "StaphylococcusAureus.fasta";
	
	public static void main(String[] args) throws IOException {
		int seqLength = 730;
		int firstBaseOffset =  990;
		int lineBytes = 62;
		int lineBases = 60;
		int numberByteLines = seqLength/lineBases;
		int numberBytes = seqLength%lineBases;
		int offsetLength = numberByteLines*lineBytes+numberBytes;
		int lineType = lineBytes-lineBases;
		System.out.println(offsetLength);
		FileInputStream in = new FileInputStream(new File(STAPHYLOCUCCUS_AUREUS));
				
		String h = parseBytes(in, firstBaseOffset, offsetLength);
		if(lineType == 1) {
			h = h.replace("\n", "");
		}
		if(lineType == 2) {
			h = h.replace("\n", "");
			h = h.replace("\r", "");
		}		
		in.close();
		System.out.println(h);
		
		seqLength = 730;
		firstBaseOffset =  112;
		lineBytes = 62;
		lineBases = 60;
		numberByteLines = seqLength/lineBases;
		numberBytes = seqLength%lineBases;
		offsetLength = numberByteLines*lineBytes+numberBytes;
		lineType = lineBytes-lineBases;
		System.out.println(offsetLength);
		FileInputStream in1 = new FileInputStream(new File(STAPHYLOCUCCUS_AUREUS));
				
		String j = parseBytes(in1, firstBaseOffset, offsetLength);
		if(lineType == 1) {
			j = j.replace("\n", "");
		}
		if(lineType == 2) {
			j = j.replace("\n", "");
			j = j.replace("\r", "");
		}		
		System.out.println();
		in.close();
		System.out.println(h);
		
	}
	
	private static String parseBytes(FileInputStream fileInputStream, int skipBytes, int offsetLength) throws IOException {
		
		byte[] array = new byte[offsetLength];  
		int offset = 0;
		while(offset < offsetLength) {
			fileInputStream.skip(skipBytes);
			offset += fileInputStream.read(array, offset, (offsetLength - offset));
		}
		String result = new String(array);		
		return result;
		
	}

}
