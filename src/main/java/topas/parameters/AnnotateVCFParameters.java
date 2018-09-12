package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class AnnotateVCFParameters extends Parameters{

protected static AnnotateVCFParameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("vcf",1,Parameter.EXISTING_FILE_PARSER,
				"-vcf <vcf file>              [REQUIRED] the vcf file, which should be annotated",			
				"No vcf file specified. Use \'-i input.vcf\'."));				
		this.addParameter(new Parameter("o",1,false, 
				"-o <vcf file>                [REQUIRED] the output path of the annotated vcf file",			
				"Please specify the output file using '-o output.vcf'"));
		this.addParameter(new Parameter("gff", 1,Parameter.EXISTING_FILE_PARSER,
				"-gff <gff file>              [REQUIRED] the GFF3 file, from where the annotation(s) should be extracted",
				"Please specify the gff3 file using '-gff input.gff3'"));
		this.addParameter(new Parameter("map", 1,true,Parameter.EXISTING_FILE_PARSER,
				"-map <map file>              [OPTIONAL] the file, in which the VCF CHROMs are mapped to the GFF3 SEQIDs, first col CHROMs, second col SEQIDs",
				"Please specify the map file using '-map input.map'"));
		this.addParameter(new Parameter("type", -1, true,
				"-type   <type1 type2 ...>    [OPTIONAL] only the specified type(s) will be taken into account for filtering",
				"Please specify the type(s) for filtering using '-type type1 type2 ...'."));
	}
	
	public static void createInstance(String[] args) {
		instance = new AnnotateVCFParameters();
		instance.parse(args);
	}
	
	public static AnnotateVCFParameters getInstance() {
		return instance;
	}

}
