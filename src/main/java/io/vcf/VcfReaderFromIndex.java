package io.vcf;

import io.ParseBytes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import utils.newline_type.NewLineType;
import vcf.index.VcfIndex;

public class VcfReaderFromIndex {

	public static String[] readVcfLinesFromIndex(VcfIndex vaix, String vcfFile) throws IOException {
		String newLineType = NewLineType.calculateNewlineTypeString(vcfFile);
		long firstVcfLineOffset = vaix.getFirstVcfLineOffset();
		int offsetLength = vaix.getOffsetLength();
		ParseBytes pB = new ParseBytes(vcfFile, firstVcfLineOffset, offsetLength);
		String rawVcfLines = pB.parseBytes();
		String[] vcfLines = rawVcfLines.split(newLineType);		
		return vcfLines;		
	}

	public static String readHeader(String vcfFile) throws IOException {
		BufferedReader bR = new BufferedReader(new FileReader(vcfFile));
		String line = null;
		String header = "";
		StringBuilder sB = new StringBuilder();
		String n = "\n";
		while ((line = bR.readLine()) != null) {
			if (line.startsWith("#")) {
				sB.append(line);
				sB.append(n);
			} else {
				bR.close();
				header = sB.toString();
				return header;
			}
		}
		bR.close();
		return header;
	}
}
