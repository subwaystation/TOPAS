package test;

import io.fasta.fasta_validator.FastaDnaValidator;

public class FastaValidatorTest {
	
	private final static String Test = "/afs/informatik.uni-tuebingen.de/ps/share/users/heumos/public/bachelor_thesis/SS14_FINAL_29-10-12_FASTA.fasta";
	private static final String Test1 = "/home/heumos/Downloads/toc.csv";
	private static final String Staph = "StaphylococcusAureus.fasta";
	private static final String BigTest = "/afs/informatik.uni-tuebingen.de/ps/share/library/organisms/hsa_37.2/hsa37.61_complete.fa";
	
	public static void main(String[] args) throws Exception {
		
		FastaDnaValidator fastaDnaValidator = new FastaDnaValidator(
				"SS14_FINAL_29-10-12_FASTA.fasta",
				"SS14_FINAL_29-10-12_FASTA.fasta");
		fastaDnaValidator.validate();
//		
//		FastaDnaValidator fastaDnaValidator1 = new FastaDnaValidator(
//				FastaValidatorTest.Test1,
//				FastaValidatorTest.Test1);
//		fastaDnaValidator1.validate();
		
//		FastaProteinValidator fastaProteinValidator = new FastaProteinValidator(
//				FastaValidatorTest.Test1,
//				FastaValidatorTest.Test1);
//		fastaProteinValidator.validate();

	}
}
