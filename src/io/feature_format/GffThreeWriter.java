package io.feature_format;

import feature_format.gff.GffThreeEntry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GffThreeWriter {
	
	private String outputFile;
	private String header;
	private List<GffThreeEntry> gTEList;
	
	public GffThreeWriter(String outputFile, List<GffThreeEntry> gTEList, String header) {
		this.outputFile = outputFile;
		this.header = header;
		this.gTEList = gTEList;
	}
	
	public void writeGffThree() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		bw.write(header);
		for (GffThreeEntry gTE : this.gTEList) {
			bw.write("\n");
			bw.write(gTE.toString());
		}
		bw.close();
	}

}
