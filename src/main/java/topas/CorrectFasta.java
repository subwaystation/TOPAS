package topas;

import java.util.Arrays;

import io.fasta.fasta_corrector.FastaDnaCorrector;
import io.fasta.fasta_corrector.FastaProteinCorrector;
import topas.Topas.TOPASModule;
import topas.parameters.CorrectFastaParameters;

@TOPASModule(
		purpose = "correct a fasta file"
		)

public class CorrectFasta {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(CorrectFasta.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		CorrectFastaParameters.createInstance(args);

		String[] inputFiles = CorrectFastaParameters.getInstance().getParameter("i").getValues();
		String outputDirectory = CorrectFastaParameters.getInstance().getParameter("o").toString();
		String sequenceType = CorrectFastaParameters.getInstance().getParameter("st").toString();
		int width = Integer.parseInt(CorrectFastaParameters.getInstance().getParameter("width").toString());
		String eol = CorrectFastaParameters.getInstance().getParameter("eol").toString();
		sequenceType = sequenceType.toLowerCase();
		
		boolean iF = CorrectFastaParameters.getInstance().getParameter("i").isPresent();
		
		if (!iF) {
			System.err.println("No input file(s) specified. Use '-i inputfile1 inputfile2 ...'.");
			System.exit(1);
		}

		boolean e = CorrectFastaParameters.getInstance().getParameter("eol").isPresent();
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
		

		// check if the right argument was passed to 'eol'
		String lineEnding = null;
		eol = eol.trim();
		if (e) {
//			this code was rewritten, because TOPAS should also be executable in a Java 1.6 environment
//			switch(eol) {
//			case "10": lineEnding = "10"; break;
//			case "13": lineEnding = "13"; break;
//			case "1310": lineEnding = "1310"; break;
//			default: System.out.println("Please specify the newline type of the resulting fasta file in the correct format: '10' for 'LF', '13' for 'CR' and '1310' for 'CRLF'"); 
//			System.exit(1); break;
//			}
			String ten = "10";
			String thirteen = "13";
			String thirten = "1310";
			if (eol.equals(ten)) {
				lineEnding = ten;
			} else {
				if (eol.equals(thirteen)) {
					lineEnding = thirteen;
				} else {
					if (eol.equals(thirten)) {
						lineEnding = thirten;
					} else {
						System.out.println("Please specify the newline type of the resulting fasta file"
								+ " in the correct format: '10' for 'LF', '13' for 'CR' and '1310' for 'CRLF'"); 
						System.exit(1);
					}
				}
			}
		}		

		System.out.println();

		System.out.println("Parameters chosen: ");
		System.out.println("Input file(s)        : "+Arrays.toString(inputFiles));
		System.out.println("Output directory     : "+outputDirectory);
		System.out.println("Sequence type        : "+sequenceType);
		System.out.println("Sequence width       : "+width);
		if (e) {
			System.out.println("EOL                  : "+ lineEnding);
		}

		System.out.println();
		
		for (int i = 0; i < inputFiles.length; i++) {
			String outputFile = "";
			String inputs = inputFiles[i];
			String[] splitInputFile = inputs.split("/");
			String inputFile = splitInputFile[splitInputFile.length-1];
			if (outputDirectory.endsWith("/")) {
				outputFile = outputDirectory + "corrected." + inputFile;
			} else {
				outputFile = outputDirectory + "/" + "corrected." + inputFile;
			}
			if(sequenceType.equals("dna")) {
				FastaDnaCorrector fDC = new FastaDnaCorrector(inputs, outputFile, width, lineEnding);
				fDC.correct();
			} else {
				FastaProteinCorrector fPC = new FastaProteinCorrector(inputs, outputFile, width, lineEnding);
				fPC.correct();
			}
			System.out.println();
			System.out.println();
		}



		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(CorrectFasta.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
	}

}
