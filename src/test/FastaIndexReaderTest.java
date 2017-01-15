package test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;


import core.fasta_index.FastaIndex;
import io.fasta.FastaIndexReader;

public class FastaIndexReaderTest {
	
	private static final String Test = "test.fasta";
	private static final String Test1 = "test.fasta.fai";

	public static void main(String[] args) throws IOException {
		
		//FastaIndexReader f = new FastaIndexReader(Test);
		//List<FastaIndex> l1 = f.scanFile();
		
		FastaIndexReader f1 = new FastaIndexReader(Test1);
		List<FastaIndex> l2 = f1.readFastaIndex();
		
		System.out.println(l2);
		
		RandomAccessFile rAF = new RandomAccessFile(Test, "r");
		rAF.skipBytes(0);
		rAF.seek(0);
	}

}
