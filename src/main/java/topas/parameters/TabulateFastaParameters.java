package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class TabulateFastaParameters extends Parameters{

protected static TabulateFastaParameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1,Parameter.EXISTING_FILE_PARSER,
				"-i <file>          input file (fasta format)",			
				"No input file specified. Use \'-i inputfile\'."));				
		this.addParameter(new Parameter("o",1,true, 
				"-o <file>          output file (tsv format)",			
				"Please specify the output file using -o"));	
	}
	
	public static void createInstance(String[] args) {
		instance = new TabulateFastaParameters();
		instance.parse(args);
	}
	
	public static TabulateFastaParameters getInstance() {
		return instance;
	}

}
