package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class ValidateFastqParameters  extends Parameters {
	
protected static ValidateFastqParameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1,Parameter.EXISTING_FILE_PARSER,
				"-i <file>          [REQUIRED] input file (fastq format)",			
				"No input file specified. Use \'-i inputfile\'."));				
		this.addParameter(new Parameter("o",1,false, 
				"-o <file>          [REQUIRED] output file (fastq validation file)",			
				"No input file specified. Use \'-o outputfile\'."));	
		this.addParameter(new Parameter("u",0,true,
				"-u                 [OPTIONAL] if this parameter is present, ValidateFastq will" +
				" additionaly look for unique sequenceIds and unique qualityIds, DEFAULT: no lookup",			
				null,
				null));	
	}
	
	public static void createInstance(String[] args) {
		instance = new ValidateFastqParameters();
		instance.parse(args);
	}
	
	public static ValidateFastqParameters getInstance() {
		return instance;
	}


}
