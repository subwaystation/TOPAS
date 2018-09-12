package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class IndexVCFParameters extends Parameters{

protected static IndexVCFParameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1,Parameter.EXISTING_FILE_PARSER,
				"-i <file>          [REQUIRED] input file (vcf format)",			
				"No input file specified. Use \'-i inputfile\'."));				
		this.addParameter(new Parameter("o",1,true, 
				"-o <file>          [OPTIONAL] output file (vcf index format)",			
				"Please specify the output file using -o"));
		this.addParameter(new Parameter("gap",1,true, 
				"-gap <int>         [OPTIONAL] the gap in lines between each VCF index",			
				"Please specify the gap in lines between each VCF index. Use -gap <int>"));
	}
	
	public static void createInstance(String[] args) {
		instance = new IndexVCFParameters();
		instance.parse(args);
	}
	
	public static IndexVCFParameters getInstance() {
		return instance;
	}
}
