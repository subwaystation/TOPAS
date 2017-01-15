package io.fastq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import utils.ArrayListUtils;
import utils.identifiers.IdentifierLine;
import utils.identifiers.IdentifiersUtils;
import utils.newline_type.NewLineType;
import core.fastq.EncodingUtils;
import core.fastq.QualityUtils;

/**
 * A FastqValidator validates a fastq file. The method validateFastq calculates the following:
 * -totalLines
 * -newline type of the file
 * -encoding of the reads (Sanger, Solexa/Illumina 1.0, Illumina 1.3, Illumina 1.5, Illumina 1.8)
 * -total reads in file
 * -the read length (if there are several different reads lengths, report all of them)
 * -overall mean read quality
 * -is it a multiline fastq file? -> if so, then calculate the amount of lines of a multiline and the line length of the reads
 * -the length of the quality string must be the same as the length of the sequence string, else it 
 * will result in an error report and the FastqValidator aborts
 * -the sequenceIds and the qualityIds per read must be unique 
 * -the highest read quality
 * -the lowest read quality
 * -the highest base quality
 * -the lowest base quality
 * @author bli.blau.blubb
 *
 */
public class FastqValidator {

	private String inputFile;
	private String outputFile;
	private List<String> errorList;
	private boolean validateUniqueness;

	/**
	 * Creates a FastqValidator.
	 * @param inputFile - the fastq file which will be validated
	 * @param outputFile - the resulting report
	 */
	public FastqValidator(String inputFile, String outputFile, boolean validateUniqueness) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.errorList = new ArrayList<String>();
		this.validateUniqueness = validateUniqueness;
	}

	public List<String> getErrorList() {
		return this.errorList;
	}

	public boolean validateFastq() throws IOException {

		System.out.println("Starting validation of fastq file " + this.inputFile);
		System.out.println();
		
		long before = System.currentTimeMillis();

		boolean validateFastq = true;

		BufferedReader bufferedReader = null;

		String line = null;

		String lineType = NewLineType.calculateNewlineTypeString(this.inputFile);

		String sequenceIdentifier = null;
		String sequence = null;
		String qualityIdentifier = null;
		String quality = null;

		StringBuilder sequenceBuilder = new StringBuilder();
		StringBuilder qualityBuilder = new StringBuilder();

		boolean fromSeqId = false;
		boolean fromQId = false;
		boolean done = false;
		boolean multiLine = false;

		int sequenceLines = 0;
		int reads = 0;
		int readBegin = 0;
		int totalLines = 0;

		double highestRQ = Double.MIN_VALUE;
		double lowestRQ = Double.MAX_VALUE;
		int highestBQ = Integer.MIN_VALUE;
		int lowestBQ = Integer.MAX_VALUE;
		double meanRQ = 0.0;
		
//		long freeMemory = Runtime.getRuntime().freeMemory();
//		long totalMemory = Runtime.getRuntime().totalMemory();
//		System.out.println("Start Used Memory: "+ (totalMemory-freeMemory));
//		long usedMemoryMax = Long.MIN_VALUE;

		String encoding = null;

		HashSet<Integer> readLengths = new HashSet<Integer>();
		HashSet<Integer> multiLines = new HashSet<Integer>();

		// read qualities
		List<Double> rQs = new ArrayList<Double>();

		HashMap<String, List<Integer>> sequenceIds = new HashMap<String, List<Integer>>();
		HashMap<String, List<Integer>> qualityIds = new HashMap<String, List<Integer>>();

		try {
			bufferedReader = new BufferedReader(new FileReader(this.inputFile));
			do {
				try {
					line = bufferedReader.readLine();
					if (line != null) {
						totalLines++;
						line = line.trim();
						char firstChar = line.charAt(0);

						// sequenceIdentifier
						if (firstChar == '@' && !fromSeqId) {
							if (!fromSeqId) {
								readBegin = totalLines;
								reads++;
								if (reads%1000000 == 0) {
									System.out.println("Validated reads " + reads);
								}
								sequenceIdentifier = line;
								if (this.validateUniqueness) {
									addToSeqIds(sequenceIdentifier.substring(1).trim(), totalLines, sequenceIds);
									//sequenceIds.add(new IdentifierLine(sequenceIdentifier.substring(1).trim(), totalLines));
								}
								fromSeqId = true;
							}			
						} else {
							// sequence line
							if (fromSeqId && firstChar != '+' && !fromQId) {
								sequenceLines++;
								sequenceBuilder.append(line);
								// qualityIdentifier
							} else {
								if (firstChar == '+' && !fromQId) {
									qualityIdentifier = line;
									if (this.validateUniqueness) {
										int qualityIdentifierLength = qualityIdentifier.substring(1).trim().length();
										if (qualityIdentifierLength > 0) {
											addToQualityIds(qualityIdentifier.substring(1).trim(), totalLines, qualityIds);
											//qualityIds.add(new IdentifierLine(qualityIdentifier.substring(1).trim(), totalLines));
										}
									}									
									fromQId = true;
								} else {
									// quality line
									if (fromQId) {
										if (sequenceLines > 1 && !multiLine) {
											multiLines.add(sequenceLines);
											multiLine = true;
										}
										qualityBuilder.append(line);
										sequenceLines = sequenceLines -1;
										if (sequenceLines == 0) {
											done = true;
										}
										// all quality lines were read
										if (done) {
											sequence = sequenceBuilder.toString();
											int seqLen = sequence.length();
											readLengths.add(seqLen);
											quality = qualityBuilder.toString();
											int qLen = quality.length();
											if (!validateSeqQualityLength(seqLen, qLen, readBegin)) {
												this.errorList.add("[ERROR] of read in line " + readBegin + " ." +
														"The sequence string and the quality string of this read differ in length.");
											}											
											double rQ = QualityUtils.calcRQ(quality);
											rQs.add(rQ);
											highestRQ = Math.max(rQ, highestRQ);
											lowestRQ = Math.min(rQ, lowestRQ);
											highestBQ = Math.max(QualityUtils.calcHighestBaseQuality(quality), highestBQ);
											lowestBQ = Math.min(QualityUtils.calcLowestBaseQuality(quality), lowestBQ);
											fromSeqId = false;
											fromQId = false;
											done = false;
											multiLine = false;
											sequenceBuilder.setLength(0);
											sequenceBuilder.trimToSize();
											qualityBuilder.setLength(0);
											qualityBuilder.trimToSize();
											sequenceLines = 0;
										}
									}									

								}
							}
						}
					} else {

					}
				} catch (IOException e) {
					System.out.println("An IO error has occured: " + e.getMessage());
					e.printStackTrace();
					System.exit(1);
				}				
			} while (line != null);
		} catch (FileNotFoundException e) {
			System.out.println("File " + inputFile + " not Found!");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Validated reads " + reads);
		System.gc();
		System.out.println();
		bufferedReader.close();
		// collect different multiLines
		System.out.println("Collecting different multiLines...");
		
		
		String multiLinesString = "";
		for (Iterator<Integer> multiLinesIt = multiLines.iterator(); multiLinesIt.hasNext(); ) {
			boolean first = false;
			if (!first) {
				first = true;
				multiLinesString += multiLinesIt.next().toString();
			} else {
				multiLinesString += "\t" + multiLinesIt.next().toString();
			}
		}
		System.out.println();

		// collect different readLengths
		System.out.println("Collecting different readLengths...");
				
		String readLengthString = "";
		for (Iterator<Integer> readLengthIt = readLengths.iterator(); readLengthIt.hasNext(); ) {
			boolean first = false;
			if (!first) {
				first = true;
				readLengthString += readLengthIt.next().toString();
			} else {
				readLengthString += "\t" + readLengthIt.next().toString();
			}
		}
		System.out.println();

		// calculate encoding 
		System.out.println("Calculating encoding...");
		encoding = EncodingUtils.encode(lowestBQ, highestBQ);
		System.out.println();

		// calculate mean read quality
		System.out.println("Calculating mean read quality...");
		meanRQ = ArrayListUtils.sum(rQs)/ (double) reads;
		System.out.println();
		
		List<String> notUniqueSeqIds = new ArrayList<String>();
		List<String> notUniqueQualityIds = new ArrayList<String>();
		
		if (validateUniqueness) {
			System.out.println("Validating Uniqueness...");
			// validate uniqueness of seqIds
			notUniqueSeqIds = IdentifiersUtils.calcNotUniqueIdentifiersFast(sequenceIds);

			// validate uniqueness of qualityIds
			notUniqueQualityIds = IdentifiersUtils.calcNotUniqueIdentifiersFast(qualityIds);
			System.out.println();
		}

		// write report file
		System.out.println("Writing report file...");
		BufferedWriter bW = new BufferedWriter(new FileWriter(this.outputFile));
		bW.write("FASTQ_VALIDATION_OF: " + this.inputFile + "\n\n");
		bW.write("[NEWLINE_TYPE]\n");
		bW.write(lineType + "\n\n");
		bW.write("[TOTAL_LINES_IN_FILE]\n");
		bW.write(totalLines + "\n\n");
		bW.write("[QUALITY_SCORE_ENCODING]\n");
		bW.write(encoding + "\n\n");
		bW.write("[TOTAL_READS_IN_FILE]\n");
		bW.write(reads + "\n\n");
		bW.write("[OBTAINED_READ_LENGTHS]\n");
		bW.write(readLengthString + "\n\n");
		bW.write("[OBTAINED_AMOUNT_OF_LINES_IN_A_MULTI_LINE] (if there are multilines in this file)\n");
		if (multiLinesString.equals("")) {
			bW.write("\n");
		} else {
			bW.write(multiLinesString +"\n\n");
		}		
		if (validateUniqueness) {
			bW.write("[NOT_UNIQUE_SEQUENCE_IDENTIFIERS]\n");
			for (int i = 0; i < notUniqueSeqIds.size(); i++) {
				bW.write(notUniqueSeqIds.get(i) + "\n");
			}
			bW.write("\n[NOT_UNIQUE_QUALITY_IDENTIFIERS]\n");
			for (int i = 0; i < notUniqueQualityIds.size(); i++) {
				bW.write(notUniqueQualityIds.get(i) + "\n");
			}
			bW.write("\n");
		}
		bW.write("[HIGHEST_READ_QUALITY]\n");
		bW.write(highestRQ + "\n\n");
		bW.write("[LOWEST_READ_QUALITY]\n");
		bW.write(lowestRQ + "\n\n");
		bW.write("[HIGHEST_BASE_QUALITY]\n");
		bW.write(highestBQ + "\n\n");
		bW.write("[LOWEST_BASE_QUALITY]\n");
		bW.write(lowestBQ + "\n\n");
		bW.write("[MEAN_READ_QUALITY]\n");
		bW.write(Double.toString(meanRQ));
		bW.close();
		System.out.println("Time passed in seconds: " + (System.currentTimeMillis()-before)/1000);
		System.out.println();
		System.out.println("The validation file can be found at " + this.outputFile);
		return validateFastq;
	}

	private void addToQualityIds(String qId, int totalLines, HashMap<String, List<Integer>> qualityIds) {
		if (qualityIds.containsKey(qId)) {
			List<Integer> l = qualityIds.get(qId);
			l.add(totalLines);
		} else {
			List<Integer> l = new ArrayList<Integer>();
			l.add(totalLines);
			qualityIds.put(qId, l);
		}
		
	}

	private void addToSeqIds(String seqId, int totalLines, HashMap<String, List<Integer>> sequenceIds) {
		addToQualityIds(seqId, totalLines, sequenceIds);		
	}

	private boolean validateSeqQualityLength(int seqLen, int qLen, int readBegin) {
		boolean valSQL = true;
		if (seqLen > qLen) {
			valSQL = false;
		}
		if (seqLen < qLen) {
			valSQL = false;
		}
		return valSQL;
	}
}
