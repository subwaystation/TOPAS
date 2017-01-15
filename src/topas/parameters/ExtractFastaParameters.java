package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class ExtractFastaParameters extends Parameters{

protected static ExtractFastaParameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1,Parameter.EXISTING_FILE_PARSER,
				"-i <file>          [REQUIRED] input file (fasta format)",			
				"No input file specified. Use \'-i inputfile\'."));
		this.addParameter(new Parameter("faidx", 1, false, Parameter.EXISTING_FILE_PARSER,
				"-faidx <file>      [REQUIRED] fasta index file",
				"No fasta index file specified. Use \'-faidx fastaindexfile\'."));
		this.addParameter(new Parameter("pattern", 1, true,
				"-pattern <String>  [OPTIONAL] pattern to look for, ATTENTION: all special characters must be wrtitten in unicode format!",
				"Please enter a pattern using -pattern. ATTENTION: All special characters must be written in Unicode-Format!"));
		this.addParameter(new Parameter("sort", 2, true,
				"-sort <String+>    [OPTIONAL] fasta part ('identifier' or 'sequence') followed by sorting order ('asc' or 'desc')",
				"Please specify if you want to sort the fasta file by \'identifier\' or by \'sequence\'.\n"
				+ "Also specify if you want to sort \'asc\' or \'desc\'"));
		this.addParameter(new Parameter("o",1,true, 
				"-o <file>          [OPTIONAL] output file (fasta format)",			
				"Please specify the output file using -o"));	
	}
	
	public static void createInstance(String[] args) {
		instance = new ExtractFastaParameters();
		instance.parse(args);
	}
	
	public static ExtractFastaParameters getInstance() {
		return instance;
	}
	
}
