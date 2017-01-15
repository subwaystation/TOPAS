package test;

import io.fasta.FastaPatternMatcher;

import java.io.IOException;

import core.sequence.OutputSequenceFormatter;

public class FastaPatternMatcherTest {

	private static final String STAPHYLOCUCCUS_AUREUS = "StaphylococcusAureus.fasta";
	private static final String Cyanobacteria_bacterium_Yellowstone_A_Prime_uid16251 = "CP000239.faa";
	private static final String Test = "test.fasta";

	public static void main(String[] args) throws IOException {
		
		// test the FastaTabulator
		FastaPatternMatcher pat = new FastaPatternMatcher(
				FastaPatternMatcherTest.STAPHYLOCUCCUS_AUREUS,
				FastaPatternMatcherTest.STAPHYLOCUCCUS_AUREUS,
				"2");
		pat.scanFile();
		
		String st = "ACTCTADFFGFRFTRFF";
		String result = OutputSequenceFormatter.formatSequence(st, 5);
		
		System.out.println(result);
	}
	
	
}
