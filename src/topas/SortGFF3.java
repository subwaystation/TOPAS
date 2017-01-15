package topas;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import comparison.gff_three_entry_comparators.SeqIdStartEndComparatorAsc;
import feature_format.gff.GffThreeEntry;
import io.feature_format.GffThreeReader;
import io.feature_format.GffThreeWriter;
import topas.Topas.TOPASModule;
import topas.parameters.SortGFF3Parameters;

@TOPASModule(
		purpose = "sorts a GFF3 File first by SeqId, then by Start/End"
		)

public class SortGFF3 {

	public static void main(String[] args) throws IOException {
		System.out.println(SortGFF3.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		SortGFF3Parameters.createInstance(args);
		
		String inputFile = SortGFF3Parameters.getInstance().getParameter("i").toString();
		String outputFile = SortGFF3Parameters.getInstance().getParameter("o").toString();
		System.out.println();
		
		System.out.println("Parameters chosen: ");
		System.out.println("Input file           : "+inputFile);
		System.out.println("Output file          : "+outputFile);
		
		System.out.println();
		
		System.out.println("Reading GFF3 File from " + inputFile);
		GffThreeReader gTR = new GffThreeReader(inputFile);
		List<GffThreeEntry> gTEList = (List<GffThreeEntry>)(List<?>)gTR.scanFile();
		String header = gTR.getHeader();
		
		
		System.out.println();
		System.out.println("Sorting GFF3 File...");
		Collections.sort(gTEList, new SeqIdStartEndComparatorAsc());
		
		System.out.println();
		System.out.println("Writing sorted GFF3 File to " + outputFile);
		GffThreeWriter gW = new GffThreeWriter(outputFile, gTEList, header);
		gW.writeGffThree();

		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(SortGFF3.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
	}

}
