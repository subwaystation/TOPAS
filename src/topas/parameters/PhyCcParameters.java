package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class PhyCcParameters extends Parameters {
	
	protected static PhyCcParameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1, false, Parameter.EXISTING_FILE_PARSER,
				"-i                   <name.tsv>           [REQUIRED] input tsv file",			
				"No input tsv SNP file specified. Use '-i name.tsv'."));		
		this.addParameter(new Parameter("o", 1, false, 
				"-o                   <file.phycc>         [REQUIRED] the output file where the results will be stored",			
				"Please specify the output file using '-o file.phycc'."));
		this.addParameter(new Parameter("cols", 1, false, 
				"-cols                <col1,col3:col7>     [REQUIRED] the column(s) in which the low coverage samples are located",			
				"Please specify the low coverage sample column(s) using '-cols col1,col3:col7'."));
		this.addParameter(new Parameter("num_agreements", 1, false, 
				"-num_agreements      <int>                [REQUIRED] the number of agreements of one sample"
				+ " with the others that should be reported",			
				"Please specify the number of agreements using '-num_agreements int'."));
		this.addParameter(new Parameter("bootstrapping", 1, true, 
				"-bootstrapping       <int>                [OPTIONAL] perform bootstrapping on the positions in the SNP file with given iterations",		
				"Please specify the bootstrapping iterations using '-bootstrapping int'."));
		this.addParameter(new Parameter("selected_samples", 1, true, 
				"-selected_samples    <file>               [OPTIONAL] file which is specifying if the sample shall be used for analysis or not.",		
				"Please specify the selected samples file using '-selected_samples file'."));
		
	}

	public static void createInstance(String[] args) {
		instance = new PhyCcParameters();
		instance.parse(args);
	}

	public static PhyCcParameters getInstance() {
		return instance;
	}

}
