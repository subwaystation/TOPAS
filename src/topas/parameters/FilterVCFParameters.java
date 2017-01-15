package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class FilterVCFParameters extends Parameters{
	
protected static FilterVCFParameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("vcf",1,Parameter.EXISTING_FILE_PARSER,
				"-vcf  <file>                             [REQUIRED] file.vcf",			
				"No vcf file specified. Use \'-vcf vcf file\'."));
		this.addParameter(new Parameter("vai",1,Parameter.EXISTING_FILE_PARSER,
				"-vai  <file>                             [REQUIRED] file.vai",			
				"No vcf index file specified. Use '-vai file.vai'."));
		this.addParameter(new Parameter("o",1, 
				"-o   <file>                              [REQUIRED] output file (the found VLC Entries)",			
				"Please specify the output file using '-o outputfile'."));
		this.addParameter(new Parameter("chromRanges",1,true,Parameter.EXISTING_FILE_PARSER,
				"-chromRanges <file>                      [OPTIONAL] input file with chromRanges; format of chromRanges: CHROM:START-END, each chromRange must be in a seperate line.",			
				null));	
		this.addParameter(new Parameter("chromRange",1,true,
				"-chromRange   <CHROM:START-END>          [OPTIONAL] chromRange, format of chromRange: CHROM:START-END; will be overwritten from Parameter 'chromRanges'",			
				null));
		this.addParameter(new Parameter("ids",1,true,Parameter.EXISTING_FILE_PARSER,
				"-ids <file>                              [OPTIONAL] input file with ids; each id must be in a seperate line",			
				null));
		this.addParameter(new Parameter("id",1,true,
				"-id   <String>                           [OPTIONAL] the id, after which should be filtered; will be overwritten from Parameter 'ids'",			
				null));
		this.addParameter(new Parameter("snp",0,true,
				"-snp                                     [OPTIONAL] if present, only VCF Entries containing SNPs are written out; DEFAULT: all Entries are written out",			
				null));
		this.addParameter(new Parameter("indel",0,true,
				"-indel                                   [OPTIONAL] if present, only VCF Entries containing INDELs are written out; DEFAULT: all Entries are written out",			
				null));
	}
	
	public static void createInstance(String[] args) {
		instance = new FilterVCFParameters();
		instance.parse(args);
	}
	
	public static FilterVCFParameters getInstance() {
		return instance;
	}

}
