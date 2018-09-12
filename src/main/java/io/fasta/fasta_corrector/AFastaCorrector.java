package io.fasta.fasta_corrector;

import io.fasta.FastaWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.identifiers.IdentifierLine;
import utils.identifiers.IdentifiersUtils;
import utils.newline_type.NewLineType;

public abstract class AFastaCorrector {
	
	protected String inputFile;
	protected String outputFile;
	protected int seqWidth;
	protected String newLineType;
	
	public AFastaCorrector(String inputFile, String outputFile, int seqWidth, String newLineType) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.seqWidth = seqWidth;
		this.newLineType = newLineType;
	}
	
	/**
	 * @param seqLine
	 * @param errorList
	 * @param totalLines
	 * @return
	 */
	protected abstract String correctAcid(String seqLine, List<String> errorList, int totalLines);
	
	/**
	 * correct a fasta file
	 * @throws Exception
	 * 
	 */
	public void correct() throws Exception {

		BufferedWriter bW = new BufferedWriter(new FileWriter(this.outputFile));

		BufferedReader fileReader = null;
		String line = null;

		// header of each sequence
		String header = "";

		// the number of totalLines that are read in
		int totalLines = 0;
		// the number of commentLines that are read in
		int commentLines = 0;
		// the number of emptyLines that are read in
		int emptyLines = 0;

		// state declaring if the last read line was a headerLine
		boolean fromHeader = false;
		// state declaring if the last read line was a sequenceLine
		boolean fromSequence = false;
		// state declaring if the last read line was an emptyLine
		boolean fromEmptyLine = false;
		// state declaring if the last read line was a commentLine
		boolean fromCommentLine = false;
		// state declaring if the last read in sequence was smaller than the previous read
		@SuppressWarnings("unused")
		boolean sequenceSmaller = false;
		// is header unique?
		boolean uniqueHeader = true;
		
		// amount of sequences in corrected fasta file
		int sequences = 0;

		// length of each sequence
		int seqLen = 0;

		// the length of the last read sequence
		int sequenceLengthInLine = 0;

		// the newLineType of the file 
		String lineType = NewLineType.calculateNewlineTypeString(this.inputFile);
		if (lineType.equals("\\n")) {
			lineType = "10";
		} else {
			if (lineType.equals("\\r")) {
				lineType = "13";
			} else {
				if (lineType.equals("\\r\\n")) {
					lineType = "1310";
				}
			}
		}
		
		if (this.newLineType == null) {
			this.newLineType = lineType;
		}

		// list of error-messages
		List<String> errorList = new ArrayList<String>();
		// list of warning-messages
		List<String> warningList = new ArrayList<String>();
		// list of identifiers
		List<IdentifierLine> identifiers = new ArrayList<IdentifierLine>();

		// initialize the fasta writer
		FastaWriter fW = new FastaWriter(bW, null, true, this.seqWidth, this.newLineType);
		
		// last occurrence of an empty line
		int occEmpty = -1;
		// last occurrence of a comment line
		int occCom = -1;

		System.out.println("Starting Fasta Correction of " + this.inputFile); 

		try {
			fileReader = new BufferedReader(new FileReader(this.inputFile));
			do {
				line = fileReader.readLine();
				if (line != null) {
					// line = line.trim();
					// ignore empty line
					if (line.isEmpty()) {
						// we read one more line
						totalLines++;
						emptyLines++;
						fromEmptyLine = true;
						occEmpty = totalLines;
						continue;
					}
					char firstChar = line.charAt(0);
					if (firstChar == '>') {
						if(fromSequence && fromEmptyLine) {
							warningList.add("[line " + occEmpty + "] An empty line between a header and an end of a sequence was removed.");
						}
						// set the new states
						fromHeader = true;
						fromSequence = false;
						fromEmptyLine = false;
						fromCommentLine = false;
						sequenceSmaller = false;

						// the header is the current line
						header = line.trim();
//						header = header.substring(1).split(" ")[0];

						// is the identifier already in the list?
						if (containHeader(identifiers, header)) {
							uniqueHeader = false;
						}

						// add the identifier to the identifiersList
						identifiers.add(new IdentifierLine(header, totalLines+1));

						// one more line was read
						totalLines++;
						
						fW.setIsHeaderLine(true);

						// write out the current header
						if (!uniqueHeader) {
							fW.setLine(header + "_" + totalLines);
							fW.writeLine();
						} else {
							fW.setLine(header);
							fW.writeLine();
						}							

						seqLen = 0;
						
						sequences++;

					} else if (firstChar == ';') {
						if (fromSequence && fromEmptyLine) {
							errorList.add("[line " + occEmpty + "] An empty line between a sequence and a comment line was removed.");
						}
						commentLines++;
						// we read one more line
						totalLines++;
						fromCommentLine = true;
						occCom = totalLines;
					} else {
						// one more line was read
						totalLines++;

						// check from which state we came
						if(fromEmptyLine && fromSequence) {
							errorList.add("[line " + occEmpty + "] An empty line within a sequence content was removed.");
						}
						if(fromSequence && fromCommentLine) {
							errorList.add("[line " + occCom + "] A comment line within a sequence content was removed.");
						}
						if(fromHeader && fromCommentLine) {
							errorList.add("[line " + occCom + "] A comment line after a header was removed.");
						}
						if(fromEmptyLine && fromHeader) {
							warningList.add("[line " + occEmpty + "] An empty line between a header and the beginning of a sequence"
									+ " was removed");
						}
						if(sequenceLengthInLine < line.length() && fromSequence) {
							errorList.add("[line " + totalLines + "] A sequence line bigger than the preceding one is not allowed."
									+ " This was corrected.");
						}

						sequenceLengthInLine = line.length();

						// set the new states
						fromSequence = true;
						fromHeader = false;
						fromEmptyLine = false;
						fromCommentLine = false;

						// update length of the sequence
						seqLen = line.trim().length() + seqLen;
						
						// calculate SequenceLine
						String seqLine = correctAcid(line.trim(), errorList, totalLines);
						
						// write the sequence line
						fW.setIsHeaderLine(false);
						// write the corrected seq line
						fW.setLine(seqLine);
						fW.writeLine();
					}
				} else {
					fW.setFirstHeader(true);
					fW.setIsHeaderLine(true);
					fW.setLine("");
					fW.writeLine();
				}
			} while (line != null);
		} catch (FileNotFoundException e) {
			System.out.println("File " + this.inputFile + " not Found!");
			System.exit(1);
		} catch (IOException e) {
			System.out.println("An IO error has occured: " + e.getMessage());
			System.exit(1);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					// do nothing here
				}
			}
		}
		bW.close();
		List<String> notUniqueIdentifiersList = IdentifiersUtils.calcNotUniqueIdentifiers(identifiers);
		printCorrection(sequences,commentLines, emptyLines, totalLines, warningList, errorList, notUniqueIdentifiersList, lineType);
	}
	
	protected void printCorrection(int sequences, int commentLines,
			int emptyLines, int totalLines, List<String> warningList,
			List<String> errorList, List<String> notUniqueIdentifiers,
			String newLineType) {
		System.out.println();
		System.out.println("Correction of " + this.inputFile);
		System.out.println();
		System.out.println("[NEWLINE_TYPE_IN_CORRECTED_FASTA_FILE]" + "\n" + newLineType);
		System.out.println("[TOTAL_REMOVED_EMPTY_LINES_IN_FILE]" + "\n" +  emptyLines);
		System.out.println("[TOTAL_REMOVED_COMMENT_LINES_IN_FILE]" + "\n" +  commentLines);
		System.out.println("\n"+ "[CORRECTED_WARNING_LINES]");
		for(int i = 0; i < warningList.size(); i++) {
			System.out.println(warningList.get(i));
		}
		System.out.println("\n" + "[CORRECTED_SEQUENCE_HEADERS_WITH_NOT_UNIQUE_IDENTIFIERS]");
		for(int i = 0; i < notUniqueIdentifiers.size(); i++) {
			System.out.println(notUniqueIdentifiers.get(i));
		}
		System.out.println("\n"+ "[CORRECTED_ERROR_LINES]");
		for(int i = 0; i < errorList.size(); i++) {
			System.out.println(errorList.get(i));
		}
	}
	
	/**
	 * @param identifiers
	 * @param header
	 * @return
	 */
	private boolean containHeader(List<IdentifierLine> identifiers,
			String header) {
		boolean found = false;
		for (int i = 0; i < identifiers.size(); i++) {
			IdentifierLine iL = identifiers.get(i);
			String identifier = iL.getIndentifier();
			if (identifier.equals(header)) {
				found = true;
				return found;
			}
		}
		return found;
	}

}
