package test;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class MapLociParameters extends Parameters {

protected static MapLociParameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("fa",1,Parameter.EXISTING_FILE_PARSER,
				"-fa  <fasta file>                        [REQUIRED] input file (fasta format)",			
				"No fasta file specified. Use \'-fa inputfile\'."));
		this.addParameter(new Parameter("fai",1,Parameter.EXISTING_FILE_PARSER,
				"-fai <fasta index file>                  [REQUIRED] input file (fasta index format)",			
				"No fasta index file specified. Use \'-fai inputfile\'."));	
		this.addParameter(new Parameter("gff",1,Parameter.EXISTING_FILE_PARSER,
				"-gff <gff3 file>                         [REQUIRED] input file (gff3 format)",			
				"No gff3 file specified. Use \'-gff inputfile\'."));	
		this.addParameter(new Parameter("o",1, 
				"-o   <file>                              [REQUIRED] output file (fasta format)",			
				"Please specify the output file using '-o outputfile'."));
		this.addParameter(new Parameter("l", -1, true,
				"-l   <int int ...>                       [SEMI-OPTIONAL] (either parameter '-l' or parameters '-seqId' and '-range' must be set) line(s) to map, lines that are not a gff3 entry are ignored!",
				"Please specify the line(s) to map using '-l int int ...'."));
		this.addParameter(new Parameter("seqId", -1, true,
				"-seqId   <seqId1 seqId1 ...>             [SEMI-OPTIONAL] (either parameter '-l' or parameters '-seqId' and '-range' must be set) seqId(s) to map",
				"Please specify the seqId(s) to map using '-seqId seqId1 seqId2 ...'."));
		this.addParameter(new Parameter("range", -1, true,
				"-range   <start1_end1 start2_end2 ...>   [OPTIONAL] range(s) to map, if no range is specified, the range will be the whole genome",
				"Please specify the line(s) to map using '-l int int ...'."));
		this.addParameter(new Parameter("source", -1, true,
				"-source   <source1 source2 ...>          [OPTIONAL] only the specified source(s) will be taken into account for mapping",
				"Please specify the sources(s) for mapping using '-source source1 source2 ...'."));
		this.addParameter(new Parameter("type", -1, true,
				"-type   <type1 type2 ...>                [OPTIONAL] only the specified type(s) will be taken into account for mapping",
				"Please specify the type(s) for mapping using '-type type1 type2 ...'."));
		this.addParameter(new Parameter("score", -1, true,
				"-score   <score1 score2 ...>             [OPTIONAL] only the specified score(s) will be taken into account for mapping",
				"Please specify the score(s) for mapping using '-score score1 score2 ...'."));
		this.addParameter(new Parameter("strand", -1, true,
				"-strand   <strand strand ...>            [OPTIONAL] only the specified strand(s) will be taken into account for mapping",
				"Please specify the strand(s) for mapping using '-strand strand1 strand2 ...'."));
		this.addParameter(new Parameter("phase", -1, true,
				"-phase   <phase phase ...>               [OPTIONAL] only the specified phase(s) will be taken into account for mapping",
				"Please specify the phase(s) for mapping using '-phase phase1 phase1 ...'."));
		this.addParameter(new Parameter("attribute", -1, true,
				"-phase   <attribute attribute ...>       [OPTIONAL] only the specified attribute(s) will be taken into account for filtering; an attribute can either be entered as 'tag=tag1' or as 'value=value1'",
				"Please specify the attribute(s) for filtering using '-attribute attribute1 attribute2 ...'."));
	}
	
	public static void createInstance(String[] args) {
		instance = new MapLociParameters();
		instance.parse(args);
	}
	
	public static MapLociParameters getInstance() {
		return instance;
	}

}
