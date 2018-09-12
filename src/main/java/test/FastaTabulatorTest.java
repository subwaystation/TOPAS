package test;

import java.io.IOException;

import io.fasta.FastaTabulator;

public class FastaTabulatorTest {
	
	private static final String STAPHYLOCUCCUS_AUREUS = "StaphylococcusAureus.fasta";
	private static final String Cyanobacteria_bacterium_Yellowstone_A_Prime_uid16251 = "CP000239.faa";
	private static final String Test = "test.fasta";

	public static void main(String[] args) throws IOException {
		
		// test the FastaTabulator
		FastaTabulator tab = new FastaTabulator(
				FastaTabulatorTest.STAPHYLOCUCCUS_AUREUS,
				"/afs/informatik.uni-tuebingen.de/ps/share/users/heumos/public/bachelor_thesis/test1.fasta");
		tab.tabulate();
	}

}
