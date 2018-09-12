package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class PrimaryBaseFastaParameters  extends Parameters {
	
	protected static PrimaryBaseFastaParameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1, false, Parameter.EXISTING_FILE_PARSER,
				"-i                   <seq.fa>         [REQUIRED] input seq.fasta file",			
				"No input fasta sequence file specified. Use '-i seq.fa'."));		
		this.addParameter(new Parameter("o", 1, false, 
				"-o                   <output.fa>      [REQUIRED] the output path of the rectified fasta file",			
				"Please specify the output file using '-o output.fa'."));
	}

	public static void createInstance(String[] args) {
		instance = new PrimaryBaseFastaParameters();
		instance.parse(args);
	}

	public static PrimaryBaseFastaParameters getInstance() {
		return instance;
	}

}
