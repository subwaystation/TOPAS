package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class FormatFastqParameters extends Parameters {

protected static FormatFastqParameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1,Parameter.EXISTING_FILE_PARSER,
				"-i <file>          [REQUIRED] input file (fastq format)",			
				"No input file specified. Use \'-i inputfile\'."));				
		this.addParameter(new Parameter("o",1,false, 
				"-o <file>          [REQUIRED] output file (fastq format)",			
				"No input file specified. Use \'-o outputfile\'."));	
		this.addParameter(new Parameter("f",1,true,Parameter.INT_PARSER, 
				"-f <int>          [OPTIONAL] the length of the sequence string and quality string, DEFAULT: infinity",			
				null,
				null));	
	}
	
	public static void createInstance(String[] args) {
		instance = new FormatFastqParameters();
		instance.parse(args);
	}
	
	public static FormatFastqParameters getInstance() {
		return instance;
	}

}
