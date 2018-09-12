package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class ConsensusSeqFromVCFsParameters extends Parameters {

	protected static ConsensusSeqFromVCFsParameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("fasta", 1, Parameter.EXISTING_FILE_PARSER,
				"-fasta <reference.fna>          [REQUIRED] the input path of the reference fasta file",
				"Please specify the input path of the reference fasta file."));
		this.addParameter(new Parameter("fai", 1, true, Parameter.EXISTING_FILE_PARSER,
				"-fai   <reference.fna.fai>      [OPTIONAL] the path to the reference fasta index file, DEFAULT: If no fasta index file is given, then a new one is generated.",
				null));
		this.addParameter(new Parameter("vcfs",-1,Parameter.EXISTING_FILE_PARSER,
				"-vcfs  <vcf 1 vcf 2 ...>        [REQUIRED] input vcf file(s)",			
				"No input  vcf file(s) specified. Use '-vcfs vcf 1 vcf 2 ...'."));				
		this.addParameter(new Parameter("o", 1, false, 
				"-o     <output file>            [REQUIRED] the output path of the consensus fasta file",			
				"Please specify the output file using '-o output.fna'"));
		this.addParameter(new Parameter("ratio", 1 ,true, Parameter.DOUBLE_PARSER,
				"-ratio <Double>                 [OPTIONAL] the ratio, from which the alternative base of a SNP is chosen as reference"
				+ "DEFAULT: 0.8",
				null));
	}
	
	public static void createInstance(String[] args) {
		instance = new ConsensusSeqFromVCFsParameters();
		instance.parse(args);
	}
	
	public static ConsensusSeqFromVCFsParameters getInstance() {
		return instance;
	}
	
}