package io.fasta.fasta_validator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import core.sequence.ProteinSequence;

public class FastaProteinValidator extends AFastaValidator {

	/**
	 * @param inputFile
	 * @param outputFile
	 */
	public FastaProteinValidator(String inputFile, String outputFile) {
		super(inputFile, outputFile);
	}

	@Override
	protected long[] generateTotalChars() {
		return new long[29];
	}
	
	@Override
	protected void writeValidationFile(long[] totalChars, int commentLines,
			int emptyLines, int totalLines, List<String> warningList,
			List<String> errorList, List<String> notUniqueIdentifiers, String newLineType) {
		try{
			// Create  ValidationFile
			FileWriter fstream = new FileWriter(this.outputFile + ".valid");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Validation of " + this.inputFile + " (Protein-Sequences)" + '\n' + '\n');
			out.write("[NEWLINE_TYPE]" + "\n" + newLineType);
			out.write("\n");
			out.write("[TOTAL_LINES_IN_FILE] " + "\n" +  totalLines);
			out.write("\n");
			out.write("[TOTAL_EMPTY_LINES_IN_FILE]" + "\n" +  emptyLines);
			out.write("\n");
			out.write("[TOTAL_COMMENT_LINES_IN_FILE]" + "\n" +  commentLines);
			out.write("\n");
			out.write("[TOTAL_SEQUENCES_IN_FILE]" + "\n" + this.sequenceList.size());
			out.write("\n");
			long totalSeqLength = calcTotalChars(totalChars);
			out.write('\n' + "[TOTAL_AMOUNT_OF_PROTEINS]" + "\n" +  totalSeqLength + '\n');
			out.write("\n");
			out.write("[PROTEIN_DISTRIBUTION]");
			out.write("\n");
			String t = "\t";
			out.write("A" + t + "B" + t + "C" + t + "D" + t + "E" + t + "F" + t + "G" + t + "H" + t + "I" + t + "J" + t + "K" + t 
					+ "L" + t + "M" + t + "N" + t + "O" + t + "P" + t + "Q" + t + "R" + t + "S" + t
					+ "T" + t + "U" + t + "V" + t + "W" + t + "Y" + t + "Z" + t + "X" + t + "*" + t + "-" + t + "OtherCharacters");
			out.write("\n");
			String baseDistribution = "";
			for(int i = 0; i < totalChars.length; i++) {
				if(i == totalChars.length-1) {
					baseDistribution+=totalChars[i];
				} else {
					baseDistribution+=totalChars[i]+t;
				}
				
			}
			out.write(baseDistribution);
			out.write("\n");
			out.write("\n"+ "[WARNING_LINES] (do not have to be corrected before running FastaIndexer)");
			out.write("\n");
			for(int i = 0; i < warningList.size(); i++) {
				out.write(warningList.get(i));
				out.write("\n");
			}
			out.write("\n" + "[NOT_UNIQUE_IDENTIFIERS] (do have to be corrected before running FastaIndexer)");
			out.write("\n");
			for(int i = 0; i < notUniqueIdentifiers.size(); i++) {
				out.write(notUniqueIdentifiers.get(i));
				out.write("\n");
			}
			out.write("\n"+ "[ERROR_LINES] (do have to be corrected before running FastaIndexer)");
			out.write("\n");
			for(int i = 0; i < errorList.size(); i++) {
				out.write(errorList.get(i));
				out.write("\n");
			}
			out.write("\n" + "[SEQUENCE_TYPE" + t + "IDENTIFIER" + t + "SEQUENCE_LENGTH] (The order is taken over from the Fasta-File.)");
			out.write("\n");
			for(int i = 0; i < this.sequenceList.size(); i++) {
				if(i == this.sequenceList.size()-1) {
					out.write(this.sequenceList.get(i).toString());
				} else {
					out.write(this.sequenceList.get(i).toString());
					out.write("\n");
				}
				
			}
			System.out.println("The validation file can be found at: " + this.outputFile + ".valid");
			//Close the output stream
			out.close();
		}catch (Exception e){
			//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
	}

	@Override
	protected void validateAcid(String seqLine, List<String> errorList,
			int totalLines, long[] proteins) {
		for(int i = 0; i < seqLine.length(); i++) {
			int index = ProteinSequence.PROTEIN_IUPAC_CODES.indexOf(seqLine.toUpperCase().charAt(i));
			if(index == -1) {
				proteins[28] = proteins[28] + 1;
			} else {
				proteins[index] = proteins[index] +1;
			}
		}
		if(proteins[28] > 0) {
			errorList.add("[line " + totalLines + "] Illegal Character(s)");
		}
	}

	@Override
	protected void addToStatsList(String header, int seqLen) {
		if(!(header.equals(""))) {
			ProteinSequence pS = null;
			pS = new ProteinSequence(header, seqLen);
			this.sequenceList.add(pS);			
		}
		
	}

}
