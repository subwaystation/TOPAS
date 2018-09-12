package io.fasta.fasta_corrector;

import java.util.List;

import core.sequence.DnaSequence;

public class FastaDnaCorrector extends AFastaCorrector {

	public FastaDnaCorrector(String inputFile, String outputFile, int seqWidth,
			String newLineType) {
		super(inputFile, outputFile, seqWidth, newLineType);
	}

	@Override
	protected String correctAcid(String seqLine, List<String> errorList,
			int totalLines) {
		StringBuilder sL = new StringBuilder();
		String seqLineUp = seqLine.toUpperCase();
		char n = 'N';
		boolean illegal = false;
		for(int i = 0; i < seqLine.length(); i++) {
			char charAtI = seqLineUp.charAt(i);
			int index = DnaSequence.DNA_IUPAC_CODES.indexOf(charAtI);
			if(index == -1) {
				sL.append(n);
				illegal = true;
			} else {
				sL.append(charAtI);
			}
		}
		if(illegal) {
			errorList.add("[line " + totalLines + "] Conversion of illegal Character(s) to 'N'");
		}
		String finalSeqLine = sL.toString();
		return finalSeqLine;
	}
}
