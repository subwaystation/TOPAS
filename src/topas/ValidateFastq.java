package topas;

import java.io.IOException;

import io.fastq.FastqValidator;
import topas.Topas.TOPASModule;
import topas.parameters.ValidateFastqParameters;

@TOPASModule(
		purpose = "validate a fastq file"
		)


public class ValidateFastq {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(ValidateFastq.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		ValidateFastqParameters.createInstance(args);
		
		String inputFile = ValidateFastqParameters.getInstance().getParameter("i").toString();
		String outputFile = ValidateFastqParameters.getInstance().getParameter("o").toString();
		boolean validateUniqueness = ValidateFastqParameters.getInstance().getParameter("u").isPresent();

		System.out.println();
		
		System.out.println("Parameters chosen: ");
		System.out.println("Input file           : "+inputFile);
		System.out.println("Output file          : "+outputFile);
		System.out.println("Validate uniqueness  : "+validateUniqueness);
		System.out.println();
		
		FastqValidator fV = new FastqValidator(inputFile, outputFile, validateUniqueness);
		fV.validateFastq();
		
		System.out.println();
		
		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(ValidateFastq.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");

	}

}
