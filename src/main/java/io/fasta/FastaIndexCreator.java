package io.fasta;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.newline_type.NewLineType;
import core.fasta_index.FastaIndex;

/**
 * @author bli.blau.blubb
 *
 */
public class FastaIndexCreator {
	private String inputFile;
	private List<FastaIndex> indexList;

	/**
	 * @param inputFile
	 * A FastaIndexCreator needs to know the location of the inputFile and must have a resulting indexList.
	 */
	public FastaIndexCreator(String inputFile) {
		this.inputFile = inputFile;
		this.indexList = new ArrayList<FastaIndex>();
	}

	/**
	 * @return
	 * Scans the input file and creates a list of fastaIndices.
	 * @throws Exception 
	 */
	public List<FastaIndex> createFastaIndex() throws Exception {

		BufferedReader fileReader = null;
		String line = null;
		// the current read header, in the beginning empty
		String header = "";
		// the current read sequence(s), in the beginning empty
		int seqLen = 0;

		// the number of bases in each fasta line
		int numberBases = 0;
		// the number of bytes in each fasta line
		int numberBytes = 0;
		// the LineType
		int lineType = NewLineType.calculateNewlineTypeInt(inputFile);
		// check, if the parsed Fasta-File was created in Unix/Windows
		if(lineType == -1) {
			System.out.println("Please insert a Unix/Windows formatted FASTA-File!");
			System.exit(1);
		}

		// the firstBaseOffset which is counted up the whole time
		long currentFirstBaseOffset = 0;
		// the FirstBaseOffset one is able to save
		long finalFirstBaseOffset = 0;

		// the number of emptyLines that are read in
		int emptyLines = 0;
		// the number of totalLines that are read in
		int totalLines = 0;
		// the number of commentLines that are read in
		int commentLines = 0;
		// the length of the last read sequence
		int sequenceLength = 0;

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

		System.out.println("Generating FastaIndex");


		try {
			fileReader = new BufferedReader(new FileReader(inputFile));
			do {
				line = fileReader.readLine();
				if (line != null) {
					// line = line.trim();
					// ignore empty line
					if (line.isEmpty()) {
						emptyLines++;
						totalLines++;
						fromEmptyLine = true;
						continue;
					}
					char firstChar = line.charAt(0);
					if (firstChar == '>') {
						// set the new states
						fromHeader = true;
						fromSequence = false;
						fromEmptyLine = false;
						fromCommentLine = false;
						sequenceSmaller = false;

						// save the previous index read
						addToIndexList(header, seqLen, numberBases, numberBytes, lineType, finalFirstBaseOffset);

						// new index read means zero number of bases and zero number of bytes and zero emptyLines
						numberBases = 0;
						numberBytes = 0;

						totalLines++;

						// the current FirstBaseOffste is the current lineLength in Bytes + the lineType + 
						// oldCurrentFirstBaseOffset + emptyLines*lineType + commentLines before first header
						currentFirstBaseOffset=line.getBytes().length + lineType + currentFirstBaseOffset + emptyLines*lineType + commentLines;
						// because we read a header line here, we now obtain the firstBaseOffset for the current sequence
						finalFirstBaseOffset=currentFirstBaseOffset;

						// commentLines are 0 now
						commentLines = 0;        				
						// emptyLines are 0 now
						emptyLines = 0;

						// now fetch the new header
						header = line;

						// check for uniqueness of header
						for(int i = 0; i < this.indexList.size(); i++) {
							if(this.indexList.get(i).getSequenceName().equals(header.substring(1))) {
								System.err.println("Only unique identifiers are allowed. Please run ValidateFasta to validate your FastaFile.");
								System.exit(1);
							}
						}

						System.out.println("Indexing sequence " + header.substring(1).split(" ")[0] + "...");

						// current sequence length is now 0
						seqLen = 0;

					} else if (firstChar == ';') {
						commentLines = commentLines + line.getBytes().length + lineType;
						totalLines++;
						fromCommentLine = true;
					} else {
						totalLines++;

						// check from which state we came
						if(fromEmptyLine && fromSequence) {
							System.err.println("An empty line is not allowed within a sequence content!" + "\n" + 
									"Error occured in line " + totalLines);
							System.exit(1);
						}
						if(fromSequence && fromCommentLine) {
							System.err.println("A comment line is not allowed within a sequence content!" + "\n" + 
									"Error occured in line " + totalLines);
							System.exit(1);
						}
						if(fromHeader && fromCommentLine) {
							System.err.println("A comment line is not allowed after a header!" + "\n" + 
									"Error occured in line " + totalLines);
							System.exit(1);
						}
						if(sequenceLength < line.length() && fromSequence) {
							System.err.println("A sequence line bigger than the preceding one is not allowed!" + "\n" + 
									"Error occured in line " + totalLines);
							System.exit(1);
						}

						// the number of bases per line per sequence is the maximum of all 
						// obtained lineSequenceLengths of the parsed sequence
						numberBases = Math.max(line.length(), numberBases);

						sequenceLength = line.length();

						// the number of bytes per line per sequence is the maximum of all obtained lineByteLengths
						// of the parsed sequence
						numberBytes = Math.max(line.getBytes().length + lineType, numberBytes);

						// add the length of the current sequenceLine
						seqLen = line.trim().length()+seqLen;

						// the currentFirstBaseOffset now is the oldCurrentFirstBaseOffset + length of the 
						// sequenceLine in Bytes + lineType
						currentFirstBaseOffset = currentFirstBaseOffset + line.getBytes().length + lineType;

						// set the new states
						fromSequence = true;
						fromHeader = false;
						fromEmptyLine = false;
						fromCommentLine = false;


					}
				} else {
					addToIndexList(header, seqLen, numberBases, numberBytes, lineType, finalFirstBaseOffset);
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
		return indexList;
	}
	
	/**
	 * @return
	 * Scans the input file and creates a list of fastaIndices.
	 * @throws Exception 
	 */
	public List<FastaIndex> createFastaIndexUnsafely() throws Exception {

		BufferedReader fileReader = null;
		String line = null;
		// the current read header, in the beginning empty
		String header = "";
		// the current read sequence(s), in the beginning empty
		int seqLen = 0;

		// the number of bases in each fasta line
		int numberBases = 0;
		// the number of bytes in each fasta line
		int numberBytes = 0;
		// the LineType
		int lineType = NewLineType.calculateNewlineTypeInt(inputFile);
		
		System.out.println(lineType);
		// check, if the parsed Fasta-File was created in Unix/Windows
		if(lineType == -1) {
			System.out.println("Please insert a Unix/Windows formatted FASTA-File!");
			System.exit(1);
		}

		// the firstBaseOffset which is counted up the whole time
		long currentFirstBaseOffset = 0;
		// the FirstBaseOffset one is able to save
		long finalFirstBaseOffset = 0;

		// the number of emptyLines that are read in
		int emptyLines = 0;
		// the number of commentLines that are read in
		int commentLines = 0;

		System.out.println("Generating FastaIndex");


		try {
			fileReader = new BufferedReader(new FileReader(inputFile));
			do {
				line = fileReader.readLine();
				if (line != null) {
					// line = line.trim();
					char firstChar = line.charAt(0);
					if (firstChar == '>') {

						// save the previous index read
						addToIndexList(header, seqLen, numberBases, numberBytes, lineType, finalFirstBaseOffset);

						// new index read means zero number of bases and zero number of bytes and zero emptyLines
						numberBases = 0;
						numberBytes = 0;

						// the current FirstBaseOffste is the current lineLength in Bytes + the lineType + 
						// oldCurrentFirstBaseOffset + emptyLines*lineType + commentLines before first header
						currentFirstBaseOffset=line.getBytes().length + lineType + currentFirstBaseOffset + emptyLines*lineType + commentLines;
						// because we read a header line here, we now obtain the firstBaseOffset for the current sequence
						finalFirstBaseOffset=currentFirstBaseOffset;
						System.out.println(finalFirstBaseOffset);

						// now fetch the new header
						header = line;

						System.out.println("Indexing sequence " + header.substring(1).split(" ")[0] + "...");

						// current sequence length is now 0
						seqLen = 0;

					} else {

						// the number of bases per line per sequence is the maximum of all 
						// obtained lineSequenceLengths of the parsed sequence
						numberBases = Math.max(line.length(), numberBases);

						// the number of bytes per line per sequence is the maximum of all obtained lineByteLengths
						// of the parsed sequence
						numberBytes = Math.max(line.getBytes().length + lineType, numberBytes);

						// add the length of the current sequenceLine
						seqLen = line.trim().length()+seqLen;

						// the currentFirstBaseOffset now is the oldCurrentFirstBaseOffset + length of the 
						// sequenceLine in Bytes + lineType
						currentFirstBaseOffset = currentFirstBaseOffset + line.getBytes().length + lineType;

					}
				} else {
					addToIndexList(header, seqLen, numberBases, numberBytes, lineType, finalFirstBaseOffset);
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
		return indexList;
	}

	/**
	 * @param header
	 * @param stringBuffer
	 * @param firstBaseOffset
	 * @param numberBases
	 * @param numberBytes
	 */
	private void addToIndexList(String header, int seqLen, int numberBases,
			int numberBytes, int lineType, long firstBaseOffset) {
		if(!(header.length() == 0)) {
			FastaIndex fI = null;
			//int firstBaseOffset = calculateFirstBaseOffset(header, lineType); old method
			fI = new FastaIndex(header, seqLen, firstBaseOffset, numberBases, numberBytes);
			this.indexList.add(fI);
		}

	}
}
