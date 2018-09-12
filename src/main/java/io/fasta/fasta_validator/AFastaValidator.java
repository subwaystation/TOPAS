package io.fasta.fasta_validator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.sequence.ISequence;
import utils.identifiers.IdentifierLine;
import utils.identifiers.IdentifiersUtils;
import utils.newline_type.NewLineType;

/**
 * An abstract class providing the general methods to validate a fasta file.
 * The validation method is validate() and validates the following:
 * -an empty line between a header and an end of a sequence should be removed
 * -empty lines and comment lines are not allowed within a sequence content
 * -a comment line is not allowed after a header
 * -an empty line between header and beginning of a sequence content
 * -a sequence line bigger than the preceding one is not allowed
 * -are all seqIds unique?
 * -the newline type of the fasta file
 * -total lines in file
 * -total empty lines in file
 * -total comment lines in file
 * -total sequences in file
 * -total amount of acids in file
 * -acid distribution, acids are checked for IUPAC-format, if not, they are declared as 'other characters'
 * -total gc content in numbers + percent
 * -short listing of the fasta file as sequencetype + identifier + sequencelength
 * 
 * @author heumos
 *
 */
public abstract class AFastaValidator {

	protected String inputFile;
	// header + sequence length
	protected List<ISequence> sequenceList;
	protected String outputFile;

	/**
	 * A FastaValidator needs to know the location of the input file and the prefixLocation of the
	 * output file. 
	 * It
	 * @param inputFile
	 * @param outputFile
	 */
	public AFastaValidator(String inputFile, String outputFile) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.sequenceList = new ArrayList<ISequence>();
	}

	/**
	 * create a long array with the size of the accepted chars of the corresponding FastaValidator
	 * @return
	 */
	protected abstract long[] generateTotalChars();

	/**
	 * writes the ValidationFile of the corresponding FastaValidator
	 * @param totalBases
	 * @param commentLines
	 * @param emptyLines
	 * @param totalLines
	 * @param warningList
	 * @param errorList
	 * @param lineType 
	 */
	protected abstract void writeValidationFile(long[] totalBases, int commentLines,
			int emptyLines, int totalLines, List<String> warningList,
			List<String> errorList, List<String> notUniqueIdentifiers, String lineType);

	/**
	 * validates the current read sequence line, adds errors and warnings to the corresponding lists
	 * @param trim
	 * @param errorList
	 * @param totalLines
	 * @return
	 */
	protected abstract void validateAcid(String seqLine, List<String> errorList,
			int totalLines, long[] totalChars);

	/**
	 * adds the header of the current read sequence and the sequenceLength to the
	 * sequenceList
	 * @param header
	 * @param seqLen
	 */
	protected abstract void addToStatsList(String header, int seqLen);

	/**
	 * validate a fasta file
	 * @throws Exception
	 * 
	 */
	public void validate() throws Exception {

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

		// counted amount of bases/proteins storing them in a long array
		long[] totalChars = generateTotalChars();

		// length of each sequence
		int seqLen = 0;

		// the length of the last read sequence
		int sequenceLength = 0;

		// the newLineType of the file 
		String lineType = NewLineType.calculateNewlineTypeString(inputFile);

		// list of error-messages
		List<String> errorList = new ArrayList<String>();
		// list of warning-messages
		List<String> warningList = new ArrayList<String>();
		// list of identifiers
		List<IdentifierLine> identifiers = new ArrayList<IdentifierLine>();
		
		// last occurrence of an empty line
		int occEmpty = -1;
		// last occurrence of a comment line
		int occCom = -1;

		System.out.println("Validating: " + this.inputFile); 

		try {
			fileReader = new BufferedReader(new FileReader(inputFile));
			do {
				line = fileReader.readLine();
				if (line != null) {
					totalLines++;
					// line = line.trim();
					// ignore empty line
					if (line.isEmpty()) {
						// we read one more line
						emptyLines++;
						occEmpty = totalLines;
						fromEmptyLine = true;
						continue;
					} else {
						char firstChar = line.charAt(0);
						if (firstChar == '>') {
							if(fromSequence && fromEmptyLine) {
								warningList.add("[line " + occEmpty + "] An empty line between a header and an end of a sequence should be removed");
							}
							// set the new states
							fromHeader = true;
							fromSequence = false;
							fromEmptyLine = false;
							fromCommentLine = false;
							sequenceSmaller = false;

							// save the previous index read
							addToStatsList(header, seqLen);

							// the header is the current line
							header = line.trim();

							// add the 
							identifiers.add(new IdentifierLine(header.substring(1).split(" ")[0], totalLines));

							seqLen = 0;

						} else if (firstChar == ';') {
							if (fromSequence && fromEmptyLine) {
								errorList.add("[line " + occEmpty + "] An empty line between a sequence and a comment line is not allowed");
							}
							commentLines++;
							occCom = totalLines;
							fromCommentLine = true;
						} else {

							// check from which state we came
							if(fromEmptyLine && fromSequence) {
								errorList.add("[line " + occEmpty + "] An empty line is not allowed within a sequence content");
							}
							if(fromSequence && fromCommentLine) {
								errorList.add("[line " + occCom + "] A comment line is not allowed within a sequence content");
							}
							if(fromHeader && fromCommentLine) {
								errorList.add("[line " + occCom + "] A comment line is not allowed after a header");
							}
							if(fromEmptyLine && fromHeader) {
								warningList.add("[line " + occCom + "] An empty line between a header and the beginning of a sequence"
										+ " should be removed");
							}
							if(sequenceLength < line.length() && fromSequence) {
								errorList.add("[line " + totalLines + "] A sequence line bigger than the preceding one is not allowed");
							}

							sequenceLength = line.length();

							// set the new states
							fromSequence = true;
							fromHeader = false;
							fromEmptyLine = false;
							fromCommentLine = false;

							// update length of the sequence
							seqLen = line.trim().length() + seqLen;

							// calculate the amount of bases for each line
							validateAcid(line.trim(), errorList, totalLines, totalChars);

						}
					}
				} else {
					addToStatsList(header, seqLen);
				}
			} while (line != null);
		} catch (FileNotFoundException e) {
			System.out.println("File " + inputFile + " not Found!");
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
		List<String> notUniqueIdentifiersList = IdentifiersUtils.calcNotUniqueIdentifiers(identifiers);
		System.out.println("Validation completed.");
		writeValidationFile(totalChars, commentLines, emptyLines, totalLines, warningList, errorList, notUniqueIdentifiersList, lineType);
	}	

	/**
	 * writes the given string on the console and in the given bufferedWriter
	 * @param bwprot
	 * @param string
	 * @throws IOException
	 */
	protected static void twoWrite(BufferedWriter bwprot, String string) throws IOException {
		System.out.println(string);
		bwprot.write(string);
		bwprot.write('\n');

	}

	/**
	 * @param totalChars
	 * @return the total number of Chars including N for Bases
	 */
	protected long calcTotalChars(long[] totalChars) {
		long totalCharsN = 0;
		for(int i = 0; i < totalChars.length-1; i++) {
			totalCharsN += totalChars[i];
		}
		return totalCharsN;
	}
}
