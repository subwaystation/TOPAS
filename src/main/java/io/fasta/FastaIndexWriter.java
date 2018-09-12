package io.fasta;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

import core.fasta_index.FastaIndex;

public class FastaIndexWriter {
	
	String outputFile;
	List<FastaIndex> faidxList;
	
	public FastaIndexWriter(String outFile, List<FastaIndex> faidxList) {
		this.outputFile = outFile;
		this.faidxList = faidxList;
	}
	
	public void writeFastaIndex() {
		try{
			// Create file 
			FileWriter fstream = new FileWriter(outputFile + ".fai");
			System.out.println("Writing fasta indices to: " + outputFile + ".fai");
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i = 0; i < faidxList.size(); i++) {
				if(!(i == (faidxList.size()-1))) {
					out.write(faidxList.get(i).toString() + "\n");
				} else {
					out.write(faidxList.get(i).toString());
				}

			}
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

}
