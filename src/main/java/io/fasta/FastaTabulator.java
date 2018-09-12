package io.fasta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FastaTabulator {
	
	private String inputFile;
	private String outputFile;
	
	/**
	 * @param inputFile
	 * @param outputFile
	 */
	public FastaTabulator(String inputFile, String outputFile) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}
	
/**
 * @throws IOException
 */
public void tabulate() throws IOException {
		
		BufferedReader fileReader = null;
        String line = null;
        String header = "";
        
        boolean fromHeader = false;
        boolean firstHeader = true;
        
        FileWriter fileWriter = new FileWriter(outputFile + ".tsv");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        
        System.out.println("Beginning Tabulation of: " + inputFile);
        
        try {
        	fileReader = new BufferedReader(new FileReader(inputFile ));
        	do {
        		line = fileReader.readLine();
        		if (line!=null) {
        			line = line.trim();
        			// ignore empty line
        			char firstChar = line.charAt(0);
        			if (firstChar == '>') {
        				
        				// now fetch the new header
        				header = line.substring(1);
        				
        				System.out.println("Tabulating " + header);
        			        				
        				fromHeader = true;
        				
        			} else {
        				// write header and current sequence line
        				if(fromHeader && !firstHeader) {
        					bufferedWriter.newLine();
        					bufferedWriter.write(header);
        					bufferedWriter.write("\t" + line);
        				} else {
        					if(firstHeader) {
        						bufferedWriter.write(header);
            					bufferedWriter.write("\t" + line);
            					firstHeader = false;
        					} else {
        						bufferedWriter.write(line);
        					}
        					
        				}
        				fromHeader = false;
        			}
        		} else {
        			// do nothing here
        			System.out.println("Finished Tabulating");
        			System.out.println("Tabulated file can be found at: " + outputFile + ".tsv");
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
