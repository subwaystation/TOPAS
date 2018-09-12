package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class SortGFF3Parameters extends Parameters{

	protected static SortGFF3Parameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1,Parameter.EXISTING_FILE_PARSER,
				"-i <file.gff3>          [REQUIRED] inputfile of GFF3 file to sort",			
				"No input file specified. Use '-i inputfile.gff3'."));				
		this.addParameter(new Parameter("o",1,false, 
				"-o <file.gff3>          [REQUIRED] ouputfile of the sorted GFF3 file",			
				"Please specify the output file using '-o outputfile.gff3'"));	
	}

	public static void createInstance(String[] args) {
		instance = new SortGFF3Parameters();
		instance.parse(args);
	}

	public static SortGFF3Parameters getInstance() {
		return instance;
	}
}
