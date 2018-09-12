package pretesting.consensus;

import io.fasta.OutputFastaBufferedWriter;

public class TestOutputFastaBufferedWriter {
	
	private final static String iD1 = "iD1";
	private final static String iD2 = "iD2";
	private final static String iD3 = "iD2";
	
	private final static String SEQ1 = "ACTGACGTAGACGT";
	private final static String SEQ2 = "ACTGACGTAGACGT";
	private final static String SEQ3 = "ACTGA";

	public static void main(String[] args) {
		
		OutputFastaBufferedWriter oW = new OutputFastaBufferedWriter("test3.fasta", 7);
		oW.writeIdentifier(iD1);
		oW.addBases(SEQ1);
		oW.writeIdentifier(iD2);
		oW.addBases(SEQ2);
		oW.addBases(SEQ2);
		oW.writeIdentifier(iD3);
		oW.addBases(SEQ3);
		oW.finalizeWrite();

		StringBuilder sB = new StringBuilder("start");
		System.out.println(sB.toString());
		sB.replace(0, 2, "stTo");
		System.out.println(sB.toString());
	}

}
