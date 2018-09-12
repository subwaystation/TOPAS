package io.fasta.fasta_corrector;

import java.util.List;

import core.sequence.ProteinSequence;

public class FastaProteinCorrector extends AFastaCorrector {

	public FastaProteinCorrector(String inputFile, String outputFile,
			int seqWidth, String newLineType) {
		super(inputFile, outputFile, seqWidth, newLineType);
	}

	@Override
	protected String correctAcid(String seqLine, List<String> errorList,
			int totalLines) {
		StringBuilder sL = new StringBuilder();
		String seqLineUp = seqLine.toUpperCase();
		char x = 'X';
		boolean illegal = false;
		for(int i = 0; i < seqLine.length(); i++) {
			char charAtI = seqLineUp.charAt(i);
			int index = ProteinSequence.PROTEIN_IUPAC_CODES.indexOf(charAtI);
			if(index == -1) {
				sL.append(x);
				illegal = true;
			} else {
				sL.append(charAtI);
			}
		}
		if(illegal) {
			errorList.add("[line " + totalLines + "] Illegal Character(s)");
		}
		String finalSeqLine = sL.toString();
		return finalSeqLine;
	}
}
