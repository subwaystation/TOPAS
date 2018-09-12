package io.vcf;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import vcf.index.VcfIndex;

public class VcfIndexWriter {
	
	private String outputFile;
	private List<VcfIndex> vaidxList;
	
	/**
	 * @param outputFile
	 * @param vaidxList
	 */
	public VcfIndexWriter(String outputFile, List<VcfIndex> vaidxList) {
		this.outputFile = outputFile;
		this.vaidxList = vaidxList;
	}
	
	public void writeVcfIndex() throws IOException {
		if (!this.outputFile.endsWith(".vai")) {
			outputFile = outputFile + ".vai";
		}
		System.out.println("Writing VCF_Indices to: " + this.outputFile);
		BufferedWriter bW = new BufferedWriter(new FileWriter(this.outputFile));
		
		boolean firstIndex = true;
		
		for (VcfIndex vI : this.vaidxList) {
			if (firstIndex) {
				bW.write(vI.toString());
				firstIndex = false;
			} else {
				bW.write('\n');
				bW.write(vI.toString());
			}
		}
		bW.close();
	}

}
