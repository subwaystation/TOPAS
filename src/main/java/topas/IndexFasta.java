package topas;
import java.util.List;

import core.fasta_index.FastaIndex;
import io.fasta.FastaIndexCreator;
import io.fasta.FastaIndexWriter;
import topas.Topas.TOPASModule;
import topas.parameters.IndexFastaParameters;

@TOPASModule(
		purpose = "generate fasta index from a fasta file"
		)

public class IndexFasta {

	public static void main(String[] args) throws Exception {

		System.out.println(IndexFasta.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		IndexFastaParameters.createInstance(args);
		
		String inputFile = IndexFastaParameters.getInstance().getParameter("i").toString();
		String outputFile = IndexFastaParameters.getInstance().getParameter("o").toString();
		System.out.println();
		
		if(outputFile.equals("--")) {
			outputFile = inputFile;
		}
		if(outputFile.endsWith(".fai")) {
			outputFile = outputFile.substring(0, outputFile.length()-4);
		}
		
		System.out.println("Parameters chosen: ");
		System.out.println("Input file           : "+inputFile);
		System.out.println("Output file          : "+outputFile + ".fai");
		
		System.out.println();
		
		FastaIndexCreator fIC = new FastaIndexCreator(inputFile);
		
		List<FastaIndex> faidxList = fIC.createFastaIndex();
		
		System.out.println("Generated " + faidxList.size() + " indices.");
		System.out.println();
		
		FastaIndexWriter fIW = new FastaIndexWriter(outputFile, faidxList);
		fIW.writeFastaIndex();
		
		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(IndexFasta.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
	}

}