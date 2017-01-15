package test;

import java.io.IOException;

import io.fastq.FastqFormatter;

public class FastqFormatterTest {
	
	private static final String inputFile = "sample_1.fq";
	private static final String outputFile = "sample_1.formatted.fq";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		FastqFormatter fF = new FastqFormatter("test.fq", "test.formatted.fq", 22);
		fF.format();
		
		System.out.println((int)'~');
		System.out.println((char)126);
	}

}
