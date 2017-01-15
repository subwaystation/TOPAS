package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class GenConSParameters extends Parameters {

	protected static GenConSParameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("snps",1, false, Parameter.EXISTING_FILE_PARSER,
				"-snps                   <snp.vcf>           [REQUIRED] input snps.vcf file",			
				"No input SNP vcf file specified. Use '-snps snps.vcf'."));		
		this.addParameter(new Parameter("indels",1, true, Parameter.EXISTING_FILE_PARSER,
				"-indels                 <indels.vcf>        [OPTIONAL] input indels.vcf file",			
				null));
		this.addParameter(new Parameter("o", 1, false, 
				"-o                      <output file>       [REQUIRED] the output path of the consensus fasta file",			
				"Please specify the output file using '-o output.fna'."));
		this.addParameter(new Parameter("ref", 1, false, Parameter.EXISTING_FILE_PARSER,
				"-ref                    <ref.fasta>         [REQUIRED] the refernce fasta file",			
				"Please specify the reference fasta file using '-ref ref.fasta.'"));
		this.addParameter(new Parameter("fai", 1, true, Parameter.EXISTING_FILE_PARSER,
				"-fai                    <reference.fna.fai> [OPTIONAL] "
						+ "The path to the fasta index of the reference genome in order to"
						+ "the right number of trailing 'N' to "
						+ "the consensus sequence.",
						null));
		this.addParameter(new Parameter("consensus_ratio", 1 ,true, Parameter.DOUBLE_PARSER,
				"-consensus_ratio        <double>            [OPTIONAL] "
						+ "The minimum percentage of reads of a base in relation to"
						+ " all other bases, so that this base is called for the consensus sequence."
						+ " DEFAULT: 0.75.",
						null));
//		this.addParameter(new Parameter("qual", 1 ,true, Parameter.DOUBLE_PARSER,
//				"-qual                   <double>            [OPTIONAL] "
//						+ "The minimum quality of the given call in a VCF line."
//						+ " DEFAULT: 0.",
//						null));
		this.addParameter(new Parameter("major_allele_coverage", 1 ,true, Parameter.INT_PARSER,
				"-major_allele_coverage  <integer>           [OPTIONAL] "
						+ "The minimum number of reads that are required"
						+ " declaring a base as a part of the consensus sequence."
						+ " DEFAULT: 5.",
						null));
		this.addParameter(new Parameter("total_coverage", 1 ,true, Parameter.INT_PARSER,
				"-total_coverage         <integer>           [OPTIONAL] "
						+ "The minimum total number of reads that are required"
						+ " making a consensus call of bases in the vcf file."
						+ " DEFAULT: Not Set.",
						null));
		this.addParameter(new Parameter("punishment_ratio", 1 ,true, Parameter.DOUBLE_PARSER,
				"-punishment_ratio       <double>            [OPTIONAL] "
						+ "The ratio which the coverage of a base exchange from C/G to T/A is multiplied with."
						+ " DEFAULT: 0.8.",
						null));
		this.addParameter(new Parameter("name", 1 ,true,
				"-name                   <string>            [OPTIONAL] "
						+ "A name (e.g. of sample) appearing in all FASTA headers.",
						null));

		this.addParameter(new Parameter("suppress_warn", 0 ,true,
				"-suppress_warn                              [OPTIONAL] "
						+ "If this option is set, all warnings are suppressed.",
						null));
		this.addParameter(new Parameter("vcf_out", 1 ,true,
				"-vcf_out                                    [OPTIONAL] "
						+ "Additional output in vcf format.",
						null));
		this.addParameter(new Parameter("minor", 0 ,true,
				"-minor                                      [OPTIONAL] "
						+ "If this option is set, only the minor ",
						null));
	}

	public static void createInstance(String[] args) {
		instance = new GenConSParameters();
		instance.parse(args);
	}

	public static GenConSParameters getInstance() {
		return instance;
	}

}
