package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class ValidateFastaParameters extends Parameters {
	
	protected static ValidateFastaParameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",-1,Parameter.EXISTING_FILE_PARSER,
				"-i  <file1 file2 ...>  [REQUIRED] input file(s) (fasta format)",			
				"No input file(s) specified. Use '-i inputfile1 inputfile2 ...'."));				
		this.addParameter(new Parameter("o",1,Parameter.DIRECTORY_PARSER,
				"-o  <directory>        [REQUIRED] output directory for the validation file(s),"
				+ " output file(s) will be named 'inputfile.valid'",			
				"Please specify the output directory using '-o directory'"));	
		this.addParameter(new Parameter("st", 1 ,
				"-st <String>           [REQUIRED] sequence type of the corresponding fasta file",
				"Please specify the sequence type of the fasta file by using '-st sequenceType'. " +
				"Possible sequence types are 'dna' or 'protein'."));
	}
	
	public static void createInstance(String[] args) {
		instance = new ValidateFastaParameters();
		instance.parse(args);
	}
	
	public static ValidateFastaParameters getInstance() {
		return instance;
	}

}
