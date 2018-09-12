package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class IndexFastaParameters extends Parameters {

	protected static IndexFastaParameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1,Parameter.EXISTING_FILE_PARSER,
				"-i <file>          [REQUIRED] input file (fasta format)",			
				"No input file specified. Use \'-i inputfile\'."));				
		this.addParameter(new Parameter("o",1,true, 
				"-o <file>          [OPTIONAL] output file (fasta index format)",			
				null));
	}
	
	public static void createInstance(String[] args) {
		instance = new IndexFastaParameters();
		instance.parse(args);
	}
	
	public static IndexFastaParameters getInstance() {
		return instance;
	}

}
