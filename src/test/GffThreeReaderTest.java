package test;

import java.io.IOException;
import java.util.List;

import feature_format.gff.GffThreeEntry;
import io.feature_format.GffThreeReader;

public class GffThreeReaderTest {

	private static final String CP = "test.gff";

	public static void main(String[] args) throws IOException {

		GffThreeReader gTR = new GffThreeReader(CP);
		List<GffThreeEntry> lGTF = (List<GffThreeEntry>)(List<?>) gTR.scanFile();

		for (GffThreeEntry s : lGTF) {
			System.out.println(s);
		}
	}

}
