package io.vcf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vcf.index.VcfIndex;

public class VcfIndexReader {
	
	private String inputFile;
	private List<VcfIndex> vaidxList;
	/**
	 * @param inputFile
	 * @param vaidxList
	 */
	public VcfIndexReader(String inputFile) {
		this.inputFile = inputFile;
		this.vaidxList = new ArrayList<VcfIndex>();
	}
	
	public List<VcfIndex> readVcfIndices() throws IOException {
		BufferedReader bR = new BufferedReader(new FileReader(this.inputFile));
		
		String line = null;
		
		while ((line = bR.readLine()) != null) {
			String[] lineSplit = line.split("\t");
			String chrom = lineSplit[0];
			String pos = lineSplit[1];
			long firstVcfLineOffset = Long.parseLong(lineSplit[2]);
			int offsetLength = Integer.parseInt(lineSplit[3]);
			VcfIndex vaidx = new VcfIndex(chrom, pos, firstVcfLineOffset, offsetLength);
			this.vaidxList.add(vaidx);
		}		
		bR.close();
		return this.vaidxList;
	}

}
