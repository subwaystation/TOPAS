package io.fasta;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.sequence.DnaSequence;
import core.sequence.ISequence;
import core.sequence.ProteinSequence;


/**
 * @author heumos
 *
 */
public class FastaReader {
	
	// TODO: validate acceptable parsed symbols based on sequence type;
	
	public enum FastaFileSequenceType {
		DNA_SEQUENCE, PROTEIN_SEQUENCE
	};
	
	private FastaFileSequenceType sequenceType;
	private String inputFile;
	private List<ISequence> sequenceList;
	
	/**
	 * @param sequenceType
	 * @param inputFile
	 * A fasta reader needs to know the sequenceType, the location of the inputFile and must have a resulting sequenceList.
	 */
	public FastaReader(FastaFileSequenceType sequenceType, String inputFile) {
		this.sequenceType = sequenceType;
		this.inputFile = inputFile;
		this.sequenceList = new ArrayList<ISequence>();
	}
			
	/**
	 * @return an arrayList of sequences (Header, SequenceContent)
	 */
	public List<ISequence> scanFile() {
		
		BufferedReader fileReader = null;
        String line = null;
        String header = "";
        StringBuffer sequenceDataBuffer = new StringBuffer();
        
        try {
        	fileReader = new BufferedReader(new FileReader(inputFile));
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
        				
        				// save the previous sequence read
        				addToSequenceList(header, sequenceDataBuffer);
        				
        				// now fetch the new header
        				header = line.substring(1).trim();
        				
        				// start a new sequenceDataBuffer buffer
        				sequenceDataBuffer = new StringBuffer();
        				
        			} else if (firstChar == ';') {
        				// comment line, skip it
        			} else {
        				sequenceDataBuffer.append(line.trim());
        			}
        		} else {
        			addToSequenceList(header, sequenceDataBuffer);
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
        return sequenceList;
	}
	
	/**
	 * @param header
	 * @param stringBuffer
	 */
	private void addToSequenceList(String header, StringBuffer stringBuffer) {
		if (stringBuffer.length() != 0) {
			ISequence iS = null;
			String sequenceData = stringBuffer.toString();
			if (this.sequenceType == FastaFileSequenceType.DNA_SEQUENCE) {
				iS = new DnaSequence(header, sequenceData);
			} else if (this.sequenceType == FastaFileSequenceType.PROTEIN_SEQUENCE) {
				iS = new ProteinSequence(header, sequenceData);
			} 
			this.sequenceList.add(iS);
		}
	}
	
}
