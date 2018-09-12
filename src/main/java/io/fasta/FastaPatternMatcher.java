package io.fasta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastaPatternMatcher {

	private String inputFile;
	private String outputFile;
	private String pattern;

	/**
	 * @param inputFile
	 * @param outputFile
	 * @param pattern
	 */
	/**
	 * @param inputFile
	 * @param outputFile
	 * @param pattern
	 */
	public FastaPatternMatcher(String inputFile, String outputFile, String pattern) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.pattern = pattern;
	}

	public void scanFile() throws IOException {

		BufferedReader fileReader = null;
		String line = null;
		String header = "";

		boolean foundPattern = false;
		boolean firstHeader = true;
		boolean fromHeader = false;

		FileWriter fileWriter = new FileWriter(outputFile + ".pattern");
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		System.out.println("FastaPatternMatcher is working on: " + inputFile);

		// Create a Pattern object
		pattern = pattern.replace("\\|", "\\Q|\\E");
		Pattern pat = Pattern.compile(pattern);        

		try {
			fileReader = new BufferedReader(new FileReader(inputFile ));
			do {
				line = fileReader.readLine();
				if (line!=null) {
					line = line.trim();
					// ignore empty line
					if (line.isEmpty()) {
						continue;
					}
					char firstChar = line.charAt(0);
					if (firstChar == '>') {

						// now fetch the new header
						header = line.trim();

						Matcher m = pat.matcher(header);
						foundPattern = m.find();

						fromHeader = true;

					} else if (firstChar == ';') {
						// comment line, skip it
					} else {
						// write header and current sequence line
						if(foundPattern) {
							if(!firstHeader && fromHeader) {
								bufferedWriter.newLine();
								bufferedWriter.write(header);
								bufferedWriter.newLine();
								bufferedWriter.write(line.toUpperCase().trim());
								firstHeader = false;
							} else {
								if(!firstHeader && !fromHeader) {
									bufferedWriter.newLine();
									bufferedWriter.write(line.toUpperCase().trim());
									firstHeader = false;
								} else {
									if(firstHeader && fromHeader) {
										bufferedWriter.write(header);
										bufferedWriter.newLine();
										bufferedWriter.write(line.toUpperCase().trim());
										firstHeader = false;
									} else {
										if(firstHeader && !fromHeader) {
											bufferedWriter.newLine();
											bufferedWriter.write(line.toUpperCase().trim());
											firstHeader = false;
										}
									}
									
								}

							}
						}
						fromHeader = false;

					}
				} else {
					// do nothing here
					System.out.println("Finished PatternMatching");
					System.out.println("The resulting FastaFile can be found at: " + outputFile + ".pattern");
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
		bufferedWriter.close();
	}

}
