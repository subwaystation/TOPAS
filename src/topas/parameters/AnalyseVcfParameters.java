package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class AnalyseVcfParameters extends Parameters {
	
	protected static AnalyseVcfParameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("vcf",1, false, Parameter.EXISTING_FILE_PARSER,
				"-vcf                  <file.vcf>           [REQUIRED] input vcf file, with all positions, preferably from GATK",			
				"No input vcf file specified. Use '-vcf file.vcf'."));		
		this.addParameter(new Parameter("o", 1, false, 
				"-o                    <output.gff3>        [REQUIRED] the output path of the resulting GFF3 file",			
				"Please specify the output file using '-o output.gff3'."));
		// coverage
		this.addParameter(new Parameter("coverage_window", 3, true,
				"-coverage_window      <double int int>     [OPTIONAL] this parameter specifies the behaviour of a coverage window"
				+ "and takes three arguments: "
				+ "1) The factor at which the coverage at a window is seen as problematic with respect"
				+ "to the mean coverage of the whole vcf file's windows 2) The window size 3) The window shift. DEFAULT: [3.0 50 5]",			
				"Please specify the coverage window using '-coverage_window <factor window_size window_shift>'."));
		// absolut snp
		this.addParameter(new Parameter("snp_abs_window", 3, true, 
				"-snp_abs_window       <int int int>        [OPTIONAL] this parameter specifies the behaviour of an absolute snp window "
				+ "and takes three arguments: "
				+ "1) The absolute value as the maximum number of snps seen as unproblematic in a window "
				+ "2) The window size 3) The window shift. DEFAULT: [2 5 1]",			
				"Please specify the snp absolute window using '-snp_abs_window <number window_size window_shift>'."));
		// snps mean 
		this.addParameter(new Parameter("snp_fac_window", 3, true, 
				"-snp_fac_window       <double int int>     [OPTIONAL] this parameter specifies the behaviour of a snp factor window "
				+ "and takes three arguments: "
				+ "1) The factor at which the number of snps at a window is seen as problematic with respect"
				+ "to the mean coverage of the whole vcf file's windows 2) The window size 3) The window shift. DEFAULT: [5.0 100 10]",			
				"Please specify the snp factor window using '-snp_fac_window <factor window_size window_shift>'."));
		// heterozygous calls
		this.addParameter(new Parameter("heterozygous_window", 5, true, 
				"-heterozygous_window  <int int int double double> [OPTIONAL] this parameter specifies the behaviour of a heterozygous window "
				+ "and takes five arguments: "
				+ "1) The absolute value as the maximum number of heterozygous calls seen as unproblematic in a window"
				+ " 2) The window size 3) The window shift 4) The major allele coverage frequency from which on a call"
				+ "is seen as homozygous and not heterozygous 5) The punishment ratio which with the number of reads of a base that is involved in damage"
				+ "are multiplied (the punishment ratio is only applied when: C -> T and G -> A). DEFAULT: [3 100 10 0.9 0.8]",			
				"Please specify the heterozygous window using '-heterozygous_window <number window_size window_shift frequency punishment_ratio>'."));
		// uncovered bases
		this.addParameter(new Parameter("uncovered_pos_window", 3, true, 
				"-uncovered_pos_window <double int int>     [OPTIONAL] this parameter specifies the behaviour of a procentual "
				+ "uncovered positions window "
				+ "and takes three arguments: "
				+ "1) The percent value to calculate the maximum number of positions that are allowed to be uncovered and therefore"
				+ " seen as (un)problematic in a window 2) The window size 3) The window shift. DEFAULT: [25.0 100 10]",			
				"Please specify the uncovered positions window using '-uncovered_pos_window <double window_size window_shift>'."));
		// do we want to merge the regions / windows?
		this.addParameter(new Parameter("merge_regions", 0, true,
				"-merge_regions                             [OPTIONAL] if set, the windows of the different categories are merged together "
				+ "to regions.", 
				"Please specify if the windows of different categories shall be merged using '-merge_regions'."));
		// do we only want to work on certain contigs?
		this.addParameter(new Parameter("contig_names", -1, true,
				"-contig_names         <Name1 Name2 ...>    [OPTIONAL] enter the contig names you want to analyse. Only the entered ones"
				+ " will be analysed, all others are ignored.", 
				"Please specify the contig names using '-contig_names'."));
	}

	public static void createInstance(String[] args) {
		instance = new AnalyseVcfParameters();
		instance.parse(args);
	}

	public static AnalyseVcfParameters getInstance() {
		return instance;
	}

}
