package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class NormExprTableParameters extends Parameters{
	
	protected static NormExprTableParameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {
		
		this.addParameter(new Parameter("i",1,false,Parameter.EXISTING_FILE_PARSER,
				"-i      <exprTable>  [REQUIRED] input exprTable (tab-delimited format, with or without header)",			
				"No input exprTable specified. Use '-i exprTable'."));		
		this.addParameter(new Parameter("o",1, false,
				"-o      <file>       [REQUIRED] output file (contains normalized exprTable in tab-delimited format)",			
				"Please specify the output file using '-o outputfile'."));
		this.addParameter(new Parameter("header",1,false,
				"-header <boolean>    [REQUIRED] boolean specifying, if the input file has a header or not, 'true' for yes, 'false' for no",			
				"Please specify the header boolean using '-header boolean'."));
		this.addParameter(new Parameter("cpm",0,true,
				"-cpm                 [OPTIONAL] if present, expression values are normalized after 'counts per million'",			
				null));
		this.addParameter(new Parameter("rpkm",0,true,
				"-rpkm                [OPTIONAL] if present, expression values are normalized after 'reads per kilo base per counts per million'",
				null));
		this.addParameter(new Parameter("gtf",1,true,Parameter.EXISTING_FILE_PARSER,
				"-gtf    <file.gtf>   [OPTIONAL] input gtf file (needed for rpkm normalization)",			
				"No gtf specified. Use '-gtf file.gtf'."));
		this.addParameter(new Parameter("type",1,true,
				"-type   <String>     [OPTIONAL] specify the feature type in the gtf file of which the counts were made.",			
				"No type specified. Use '-type String'."));
		this.addParameter(new Parameter("idattr",1,true,
				"-idattr <String>     [OPTIONAL] specify the idattr of the attributes in the gtf file of which the counts were made.",			
				"No idattr specified. Use '-idattr String'."));
		this.addParameter(new Parameter("commaIn",0,true,
				"-commaIn             [OPTIONAL] if present, numbers in double format are delimited by a ','.",			
				null));
		this.addParameter(new Parameter("commaOut",0,true,
				"-commaOut            [OPTIONAL] if present, all dots in double values are replaced by a comma for"
				+ " working with a europe version of e.g. excel",			
				null));
		this.addParameter(new Parameter("htseq",0,true,
				"-htseq                           [OPTIONAL] if present, the program assumes"
				+ " that input exprTables are in HTSeq format and deletes the following 'genes':" + "\n"
				+ "					__alignment_not_unique\n"
				+ "					__ambiguous\n"
				+ "					__no_feature\n"
				+ "					__not_aligned\n"
				+ "					__too_low_aQual\n"
				+ "					alignment_not_unique\n"
				+ "					ambiguous\n"
				+ "					no_feature\n"
				+ "					not_aligned\n"
				+ "					too_low_aQual",
				null));
	}
	
	public static void createInstance(String[] args) {
		instance = new NormExprTableParameters();
		instance.parse(args);
	}
	
	public static NormExprTableParameters getInstance() {
		return instance;
	}

}
