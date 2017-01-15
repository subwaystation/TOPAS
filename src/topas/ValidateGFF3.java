package topas;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;

import feature_format.gff.validate.GffThreeValidator;
import topas.Topas.TOPASModule;
import topas.parameters.CorrectFastaParameters;
import topas.parameters.ValidateGFF3Parameters;

@TOPASModule(
		purpose = "validate a gff3 file"
		)
public class ValidateGFF3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(ValidateGFF3.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		ValidateGFF3Parameters.createInstance(args);

		String[] inputFiles = ValidateGFF3Parameters.getInstance().getParameter("i").getValues();
		String outputDirectory = ValidateGFF3Parameters.getInstance().getParameter("o").toString();
		boolean m = ValidateGFF3Parameters.getInstance().getParameter("multifeatures").isPresent();
		
		boolean iF = ValidateGFF3Parameters.getInstance().getParameter("i").isPresent();
		
		if (!iF) {
			System.err.println("No input file(s) specified. Use '-i inputfile1 inputfile2 ...'.");
			System.exit(1);
		}

		System.out.println();

		System.out.println("Parameters chosen: ");
		System.out.println("Input file(s)          : "+Arrays.toString(inputFiles));
		System.out.println("Output directory       : "+outputDirectory);
		System.out.println("Print MultiFeatures    : "+m);
		System.out.println();

		for (int j = 0; j < inputFiles.length; j++) {
			String outputFile = "";
			String inputs = inputFiles[j];
			String[] splitInputFile = inputs.split("/");
			String inputFile = splitInputFile[splitInputFile.length-1];
			if (outputDirectory.endsWith("/")) {
				outputFile = outputDirectory + inputFile + ".valid";
			} else {
				outputFile = outputDirectory + "/" + inputFile + ".valid";
			}
			System.out.println("Validating " + inputs);
			System.out.println();

			GffThreeValidator gV = new GffThreeValidator(inputs, m);
			gV.validateGffThree();

			FileWriter fstream = new FileWriter(outputFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("GFF3Validation of " + inputs);		
			out.write("\n\n");
			out.write("[WARNINGS]");
			out.write("\n");
			for (int i = 0; i < gV.getWarningList().size(); i++) {
				out.write(gV.getWarningList().get(i));
				out.write("\n");

			}
			out.write("\n");
			out.write("[ENTRY_ERRORS]");
			out.write("\n");
			for (int i = 0; i < gV.getEntryErrorList().size(); i++) {
				out.write(gV.getEntryErrorList().get(i));
				out.write("\n");
			}
			out.write("\n");
			out.write("[UNIQUE_ID_ERRORS]");
			out.write("\n");
			for (int i = 0; i < gV.getUniqueIdErrorList().size(); i++) {
				out.write(gV.getUniqueIdErrorList().get(i));
				out.write("\n");
			}
			out.write("\n");
			out.write("[RELATIONSHIP_ERRORS]");
			for (int i = 0; i < gV.getRelationshipErrorList().size(); i++) {
				out.write("\n");		
				out.write(gV.getRelationshipErrorList().get(i));				
			}

			out.close();
			System.out.println();
		}	

		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(ValidateGFF3.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");

	}
}
