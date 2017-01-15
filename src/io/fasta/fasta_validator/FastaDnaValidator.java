package io.fasta.fasta_validator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import core.sequence.DnaSequence;

public class FastaDnaValidator extends AFastaValidator {


	/**
	 * @param inputFile
	 * @param outputFile
	 */
	public FastaDnaValidator(String inputFile, String outputFile) {
		super(inputFile, outputFile);
	}

	@Override
	protected void validateAcid(String seqLine, List<String> errorList, int totalLines, long[] bases) {
		String seqLineUp = seqLine.toUpperCase();
		for(int i = 0; i < seqLine.length(); i++) {
			// char c = seqLineUp.charAt(i);
			int index = DnaSequence.DNA_IUPAC_CODES.indexOf(seqLineUp.charAt(i));
			if(index == -1) {
				bases[18] = bases[18] + 1;
			} else {
				bases[index] = bases[index] +1;
			}
		}
		if(bases[18] > 0) {
			errorList.add("[line " + totalLines + "] Illegal Character(s)");
		}
	}

	@Override
	protected void writeValidationFile(long[] totalBases, int commentLines,
			int emptyLines, int totalLines, List<String> warningList, List<String> errorList
			, List<String> notUniqueIdentifiers, String newLineType) {
		try{
			// Create  ValidationFile
			FileWriter fstream = new FileWriter(this.outputFile + ".valid");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("Validation of " + this.inputFile + " (DNA-Sequences)" + "\n" + "\n");
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
			long totalSeqLength = calcTotalChars(totalBases);
			out.write('\n' + "[TOTAL_AMOUNT_OF_BASES_INCLUDING_N]" + "\n" +  totalSeqLength + '\n');
			out.write("\n");
			out.write("[BASE_DISTRIBUTION]");
			out.write("\n");
			String t = "\t";
			out.write("A" + t + "C" + t + "T" + t + "G" + t + "U" + t + "R" + t + "Y" + t + "K" + t + "M" + t + "S" + t + "W" + t 
					+ "B" + t + "D" + t + "H" + t + "V" + t + "N" + t + "X" + t + "-" + t + "OtherCharacters");
			out.write("\n");
			String baseDistribution = "";
			for(int i = 0; i < totalBases.length; i++) {
				if(i == totalBases.length-1) {
					baseDistribution+=totalBases[i];
				} else {
					baseDistribution+=totalBases[i]+t;
				}

			}
			out.write(baseDistribution);
			out.write("\n");
			long totalGC = calcTotalGC(totalBases);
			out.write('\n' + "[TOTAL_GC_CONTENT]" + "\n" + totalGC);
			out.write("\n");
			double percantageGC = (double)totalGC/(double)totalSeqLength;
			out.write("[PERCENTAGE_OF_GC_CONTENT_INCLUDING_COUNTED_N]" + "\n" + percantageGC);
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

	/**
	 * @param totalBases
	 * @return the GC content of the nucleotides
	 */
	private long calcTotalGC(long[] totalBases) {
		long totalGC = 0;
		totalGC = totalBases[1] + totalBases[3];
		return totalGC;
	}

	@Override
	protected void addToStatsList(String header, int seqLen) {
		if(!(header.equals(""))) {
			DnaSequence dS = null;
			dS = new DnaSequence(header, seqLen);
			this.sequenceList.add(dS);			
		}
	}

	@Override
	protected long[] generateTotalChars() {
		return new long[19];
	}
}
