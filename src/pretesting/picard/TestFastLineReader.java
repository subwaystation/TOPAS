package pretesting.picard;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class TestFastLineReader {
	
	private static final String TEST = "/afs/informatik.uni-tuebingen.de/ps/share/projects/1000GenomesHaplotypes.nobackup/chr22_arsa_50000000-52000000.vcf";

	public static void main(String[] args) throws FileNotFoundException {
		
		long start = System.currentTimeMillis();
		
		InputStream iS = new FileInputStream(TEST);
		BufferedInputStream bIS = new BufferedInputStream(iS);
		
		FastLineReader fR = new FastLineReader(iS);
		
		byte[] outputBuffer = new byte[512000];
		
		int lines = 0;
		
		while (fR.readToEndOfOutputBufferOrEoln(outputBuffer, 0) != -1) {
			lines++;
			outputBuffer = new byte[512000];
			//fR.readToEndOfOutputBufferOrEoln(outputBuffer, 0);
			//System.out.println(Arrays.toString(outputBuffer));
			if (lines%1000 == 0) {
				System.out.println("read in " + lines + " lines.");
			}
//			for (int i = 0; i < outputBuffer.length; i++) {
//				System.out.println((char) outputBuffer[i]);
//			}
		}
		
		System.out.println("read in " + lines + " lines.");
		long end = System.currentTimeMillis();
		
		System.out.println("time: " + (end-start)/1000 + "seconds");
		

	}

}
