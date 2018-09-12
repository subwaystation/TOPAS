package io.fastq;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import core.sequence.OutputSequenceFormatter;

/**
 * A FastqFormatter formats the length of the sequence of a read per line.
 * Therefore it also formats the length of the corresponding quality string.
 * @author bli.blau.blubb
 *
 */
public class FastqFormatter {

	private String inputFile;
	private String outputFile;
	private int lineLength;

	/**
	 * Creates a FastqFormatter. The FastqFormatter 
	 * @param inputFile - the fastq file which reads will be formatted
	 * @param outputFile - the resulting fastq file with the formatted reads
	 * @param lineLength - the length of the sequences (and quality strings) per line
	 */
	public FastqFormatter(String inputFile, String outputFile, int lineLength) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.lineLength = lineLength;
	}

	/**
	 * Reads line by line from a fastq file, until one full fastq read was obtained, then this
	 * fastq read is formatted and directly written to a new fastq file.
	 * @throws IOException
	 */
	public void format() throws IOException {
		
		System.out.println("Starting formatting of fastq file " + this.inputFile);
		
		BufferedReader bufferedReader = null;

		String line = null;

		String sequenceIdentifier = null;
		String sequence = null;
		String qualityIdentifier = null;
		String quality = null;

		StringBuilder sequenceBuilder = new StringBuilder();
		StringBuilder qualityBuilder = new StringBuilder();

		boolean fromSeqId = false;
		boolean fromQId = false;
		boolean firstRead = true;
		boolean done = false;

		int sequenceLines = 0;

		int reads = 0;

		BufferedWriter bW = new BufferedWriter(new FileWriter(this.outputFile));

		try {
			bufferedReader = new BufferedReader(new FileReader(this.inputFile));
			do {
				try {
					line = bufferedReader.readLine();
					if (line != null) {

						line = line.trim();
						char firstChar = line.charAt(0);

						// sequenceIdentifier
						if (firstChar == '@' && !fromSeqId) {
								reads++;
								sequenceIdentifier = line;
								fromSeqId = true;		
						} else {
							// sequence line
							if (fromSeqId && firstChar != '+' && !fromQId) {
								sequenceLines++;
								sequenceBuilder.append(line);
								// qualityIdentifier
							} else {
								if (firstChar == '+' && !fromQId) {
									qualityIdentifier = line;
									fromQId = true;
								} else {
									// quality line
									if (fromQId) {
										qualityBuilder.append(line);
										sequenceLines = sequenceLines -1;
										if (sequenceLines == 0) {
											done = true;
										}
										// all quality lines were read, write out fastq read
										if (done) {
											if (firstRead) {
												bW.write(sequenceIdentifier);
												bW.newLine();
												sequence = sequenceBuilder.toString();
												quality = qualityBuilder.toString();
												if (this.lineLength != -1) {
													sequence = OutputSequenceFormatter.formatSequence(sequence, this.lineLength);
													quality = OutputSequenceFormatter.formatSequence(quality, this.lineLength);
												}
												bW.write(sequence);
												bW.write("\n");
												bW.write(qualityIdentifier);
												bW.write("\n");
												bW.write(quality);
												fromSeqId = false;
												fromQId = false;
												done = false;
												sequenceBuilder.setLength(0);
												qualityBuilder.setLength(0);
												sequenceLines = 0;
												firstRead = false;
											} else {
												bW.newLine();
												bW.write(sequenceIdentifier);
												bW.newLine();
												sequence = sequenceBuilder.toString();
												quality = qualityBuilder.toString();
												if (this.lineLength != -1) {
													sequence = OutputSequenceFormatter.formatSequence(sequence, this.lineLength);
													quality = OutputSequenceFormatter.formatSequence(quality, this.lineLength);
												}
												bW.write(sequence);
												bW.write("\n");
												bW.write(qualityIdentifier);
												bW.write("\n");
												bW.write(quality);
												fromSeqId = false;
												fromQId = false;
												done = false;
												sequenceBuilder.setLength(0);
												qualityBuilder.setLength(0);
												sequenceLines = 0;
											}

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
		bufferedReader.close();
		bW.close();
		System.out.println();
		System.out.println("Formatted " + reads + " reads.");
		System.out.println();
		System.out.println("The formatted fastq file can be found at " + this.outputFile);
	}

}
