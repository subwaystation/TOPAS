package test;

import java.io.IOException;

import io.fastq.FastqValidator;

public class FastqValidatorTest {
	
	private static final String fq = "/afs/informatik.uni-tuebingen.de/ps/share/projects/Krause/Syphilis/Samples_24January2014.nobackup/Sample_HAIB/HAIB_GTCTTGG_L007_R2_001.fastq";
	private static final String test = "test.fq";
	private static final String out = "/afs/informatik.uni-tuebingen.de/ps/share/users/heumos/public/bachelor_thesis/test.fq.valid";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		long before = System.currentTimeMillis();
		
		FastqValidator fV = new FastqValidator("test.formatted.fq", "test.formatted.fq.valid", true);
		fV.validateFastq();
		
		long after = System.currentTimeMillis();
		System.out.println();
		System.out.println("ValidationTime " + (after-before));

	}

}
