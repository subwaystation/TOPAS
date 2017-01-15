package io.fasta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import core.fasta_index.FastaIndex;

public class FastaIndexReader {
	
	String inputFile;
	List<FastaIndex> faidxList;
	
	public FastaIndexReader(String inputFilePath) {
		this.inputFile = inputFilePath;
		this.faidxList = new ArrayList<FastaIndex>();
	}
	
	public List<FastaIndex> readFastaIndex() throws IOException {
		if(!this.inputFile.endsWith(".fai")) {
			System.err.println("FastaIndexReader: The input file is not a FASTA index. (input file must end with .fai)");
			System.exit(1);
		}
		BufferedReader br = new BufferedReader(new FileReader(this.inputFile));
        String line;
        while((line = br.readLine()) != null) {
             String[] split = line.split("\t");
             FastaIndex faidx = new FastaIndex(split[0], Integer.parseInt(split[1]), Long.parseLong(split[2]),
            		 Integer.parseInt(split[3])
            		 , Integer.parseInt(split[4]));
             this.faidxList.add(faidx);
        }
        br.close();
		return this.faidxList;
	}

}
