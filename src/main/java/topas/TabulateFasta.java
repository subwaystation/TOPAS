package topas;

import java.io.IOException;

import io.fasta.FastaTabulator;
import topas.Topas.TOPASModule;
import topas.parameters.TabulateFastaParameters;

@TOPASModule(
		purpose = "tabulates a fasta file into: HEADER TAB SEQUENCE"
		)

public class TabulateFasta {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		System.out.println(TabulateFasta.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		TabulateFastaParameters.createInstance(args);
		
		String inputFile = TabulateFastaParameters.getInstance().getParameter("i").toString();
		String outputFile = TabulateFastaParameters.getInstance().getParameter("o").toString();
		System.out.println();
		
		System.out.println("Parameters chosen: ");
		System.out.println("Input file           : "+inputFile);
		System.out.println("Output file          : "+outputFile);
		
		System.out.println();
		
		if(outputFile.equals("--")) {
			outputFile = inputFile;
		}
		if(outputFile.endsWith(".tsv")) {
			outputFile = outputFile.substring(0, outputFile.length()-4);
		}
		
		FastaTabulator fT = new FastaTabulator(inputFile, outputFile);
		fT.tabulate();

		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(TabulateFasta.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
	}

}
