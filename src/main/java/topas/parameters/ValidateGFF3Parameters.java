package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class ValidateGFF3Parameters extends Parameters {
	protected static ValidateGFF3Parameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",-1,Parameter.EXISTING_FILE_PARSER,
				"-i  <file1 file2 ...>     [REQUIRED] input file(s) (gff3 format)",			
				"No input file(s) specified. Use '-i inputfile1 inputfile2 ...'."));				
		this.addParameter(new Parameter("o",1,Parameter.DIRECTORY_PARSER,
				"-o  <directory>           [REQUIRED] output directory for the validated gff file(s),"
				+ " output file(s) will be named 'inputfile.valid'",			
				"Please specify the output directory using '-o directory'"));
		this.addParameter(new Parameter("multifeatures", 0 ,true,
				"-multifeatures            [OPTIONAL] if present, all possible multi features will be printed out",
				null));
	}
	
	public static void createInstance(String[] args) {
		instance = new ValidateGFF3Parameters();
		instance.parse(args);
	}
	
	public static ValidateGFF3Parameters getInstance() {
		return instance;
	}
}
