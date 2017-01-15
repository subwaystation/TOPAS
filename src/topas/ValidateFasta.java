package topas;

import java.util.Arrays;

import io.fasta.fasta_validator.FastaDnaValidator;
import io.fasta.fasta_validator.FastaProteinValidator;
import topas.Topas.TOPASModule;
import topas.parameters.ValidateFastaParameters;

@TOPASModule(
		purpose = "validate a fasta file"
		)

public class ValidateFasta {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(ValidateFasta.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		ValidateFastaParameters.createInstance(args);

		String[] inputFiles = ValidateFastaParameters.getInstance().getParameter("i").getValues();
		String outputDirectory = ValidateFastaParameters.getInstance().getParameter("o").toString();
		String sequenceType = ValidateFastaParameters.getInstance().getParameter("st").toString();
		sequenceType = sequenceType.toLowerCase();

		boolean iF = ValidateFastaParameters.getInstance().getParameter("i").isPresent();

		if (!iF) {
			System.err.println("No input file(s) specified. Use '-i inputfile1 inputfile2 ...'.");
			System.exit(1);
		}

		// check if the right argument was passed to 'st'
		boolean st;
//		this code was rewritten, because TOPAS should also be executable in a Java 1.6 environment
//		switch(sequenceType) {
//		case "dna": st = true; break;
//		case "protein": st = true; break;
//		default: st = false; break;
//		}
		if (sequenceType.equals("dna") || sequenceType.equals("protein")) {
			st = true;
		} else {
			st = false;
		}

		if(!st) {
			System.err.println("Please specify the sequence type of the fasta file. " +
					"Possible sequence types are 'dna' or 'protein'.");
			System.exit(1);
		}

		System.out.println();

		System.out.println("Parameters chosen: ");
		System.out.println("Input file(s)        : "+Arrays.toString(inputFiles));
		System.out.println("Output directory     : "+outputDirectory);
		System.out.println("Sequence type        : "+sequenceType);

		System.out.println();

		for (int i = 0; i < inputFiles.length; i++) {
			String outputFile = "";
			String inputs = inputFiles[i];
			String[] splitInputFile = inputs.split("/");
			String inputFile = splitInputFile[splitInputFile.length-1];
			if (outputDirectory.endsWith("/")) {
				outputFile = outputDirectory + inputFile;
			} else {
				outputFile = outputDirectory + "/" + inputFile;
			}
			if(sequenceType.equals("dna")) {
				FastaDnaValidator fDV = new FastaDnaValidator(inputs, outputFile);
				fDV.validate();
			} else {
				FastaProteinValidator fPV = new FastaProteinValidator(inputs, outputFile);
				fPV.validate();
			}
			System.out.println();
		}


		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(ValidateFasta.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");

	}

}
