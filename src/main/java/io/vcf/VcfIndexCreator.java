package io.vcf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.newline_type.NewLineType;
import vcf.index.VcfIndex;

/**
 * A class given the user the possibility to index a VCF_File
 * @author heumos
 *
 */
public class VcfIndexCreator {

	// the input File from which the indices should be created
	private String inputFile;
	// the resulting list of VCF_Indices
	private List<VcfIndex> vaidxList;
	// the number of lines between each VCF Index
	private int gap;

	/**
	 * @param inputFile the VCF_File to index
	 */
	public VcfIndexCreator(String inputFile, int gap) {
		this.inputFile = inputFile;
		this.vaidxList = new ArrayList<VcfIndex>();
		this.gap = gap;
	}

	public List<VcfIndex> createVcfIndices() throws Exception {

		BufferedReader bR = new BufferedReader(new FileReader(inputFile));

		String hash = "#";
		String line = null;

		long firstVcfLineOffset = 0;
		long finalFirstVcfLineOffset = 0;
		// the number of vcfEntries already passed
		int vcfEntries = 0;
		// the offset length of the current index
		int offsetLength = 0;

		// the last read line
		String lastLine = null;
		
		// the last occured CHROM
		String lastChrom = "";
		// the current CHROM
		String curChrom = "";
		String tab = "\t";

		// the LineType
		int lineType = NewLineType.calculateNewlineTypeInt(inputFile);
		// check, if the parsed Fasta-File was created in Unix/Windows
		if(lineType == -1) {
			System.out.println("Please insert a Unix/Windows formatted FASTA-File!");
			System.exit(1);
		}
		
		System.out.println("Creating VCF_Index of " + this.inputFile);

		do {
			line = bR.readLine();
			if (line != null) {
				// reading header lines
				if (line.startsWith(hash)) {
					firstVcfLineOffset += line.getBytes().length + lineType;
					// reading VCF_Entries
				} else {				
					if (vcfEntries == 0) {
						lastLine = line;
						lastChrom = lastLine.split(tab)[0];
						finalFirstVcfLineOffset = firstVcfLineOffset;
					}
					curChrom = line.split(tab)[0];
					if ((vcfEntries%this.gap == 0 || !curChrom.equals(lastChrom)) && vcfEntries != 0) {
						addVcfIndex(lastLine, finalFirstVcfLineOffset, offsetLength);
						offsetLength = 0;
						lastLine = line;
						finalFirstVcfLineOffset = firstVcfLineOffset;
						vcfEntries = 0;
						lastChrom = curChrom;
					}
					firstVcfLineOffset += line.getBytes().length + lineType;
					offsetLength += line.getBytes().length + lineType;
					vcfEntries++;
				}
			} else {
				addLastVcfIndex(lastLine, finalFirstVcfLineOffset, offsetLength, lineType);
			}
		} while (line != null);
		bR.close();
		System.out.println("Finished VCF_Indexing of "+ this.inputFile);
		return this.vaidxList;
	}

	private void addLastVcfIndex(String line, long finalFirstVcfLineOffset,
			int offsetLength, int lineType) {
		String[] lineSplit = line.split("\t");
		String chrom = lineSplit[0];
		String pos = lineSplit[1];
		VcfIndex vaidx = new VcfIndex(chrom, pos, finalFirstVcfLineOffset, offsetLength-lineType);
		this.vaidxList.add(vaidx);	
	}

	/**
	 * adds a new VCF_Index to vaidxList in VcfIndexCreator
	 * @param line
	 */
	private void addVcfIndex(String line, long firstVcfLineOffset, int offsetLength) {
			String[] lineSplit = line.split("\t");
			String chrom = lineSplit[0];
			String pos = lineSplit[1];
			VcfIndex vaidx = new VcfIndex(chrom, pos, firstVcfLineOffset, offsetLength);
			this.vaidxList.add(vaidx);	
	}

}
