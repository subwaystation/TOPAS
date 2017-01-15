package test;

import feature_format.gtf.GtfEntry;
import io.feature_format.GtfReader;

import java.io.IOException;
import java.util.List;

public class GtfReaderTest {
	
	private static final String CP = "Saccharomyces_cerevisiae.gtf";

	public static void main(String[] args) throws IOException {

		GtfReader gTR = new GtfReader(CP);
		List<GtfEntry> lGTF = (List<GtfEntry>)(List<?>) gTR.scanFile();

		for (GtfEntry s : lGTF) {
			System.out.println(s);
		}
	}
}
