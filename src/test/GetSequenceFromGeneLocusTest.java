package test;

import java.io.IOException;

import feature_format.gff.gene_loci_mapper.GetSequenceFromGeneLocus;
import core.fasta_index.FastaIndex;

public class GetSequenceFromGeneLocusTest {
	
	private final static String test = "CP000239.fna";
	private final static String cp = "CP000239.faa";

	public static void main(String[] args) throws IOException {
		
		FastaIndex faidx = new FastaIndex("test1", 16, 7, 3, 4);
		FastaIndex faidx1 = new FastaIndex("gi|86553275|gb|CP000239.1|", 2932766, 72, 70, 71);
		
		GetSequenceFromGeneLocus getLocusSequence = new GetSequenceFromGeneLocus(test, faidx1, 1, 22);
		String s = getLocusSequence.getSequenceFromGeneLocus();
		// GAGAGCAGAGGCACCGGG
		
		System.out.println(s);
		
	}

}
