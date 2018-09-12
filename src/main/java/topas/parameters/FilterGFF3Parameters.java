package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class FilterGFF3Parameters extends Parameters {

	protected static FilterGFF3Parameters instance;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",1,true,Parameter.EXISTING_FILE_PARSER,
				"-i  <input file>                         [OPTIONAL] input file, must have colum with gene loci positions, a gene loci position must be formatted like: SEQID:START-END",			
				"No input file with loci positions specified. Use \'-i inputfile\'."));
		this.addParameter(new Parameter("gff",1,Parameter.EXISTING_FILE_PARSER,
				"-gff <gff3 file>                         [REQUIRED] input file (gff3 format)",			
				"No gff3 file specified. Use \'-gff inputfile\'."));	
		this.addParameter(new Parameter("o",1, 
				"-o   <file>                              [REQUIRED] output file (the found gff3 entries will be written there)",			
				"Please specify the output file using '-o outputfile'."));
		this.addParameter(new Parameter("col", -1, true,
				"-col <int>                               [OPTIONAL] specify, in which colum of the input file the gene loci positions are; DEFAULT: 2",
				null));
		this.addParameter(new Parameter("seq", 0, true,
				"-seq                                     [OPTIONAL] if present, all the found gff3 entries' sequences will be written out as a fasta file.",
				null));
		this.addParameter(new Parameter("fa",1,true,Parameter.EXISTING_FILE_PARSER,
				"-fa  <fasta file>                        [OPTIONAL] input.fasta, the fasta file from which the sequences, specified in the gff3 file, should be extracted",			
				"No fasta file specified. Use \'-fa inputfile\'."));
		this.addParameter(new Parameter("fai",1,true,Parameter.EXISTING_FILE_PARSER,
				"-fai <fasta index file>                  [OPTIONAL] input.fai, the fasta index of the enterd fasta file",			
				"No fasta index file specified. Use \'-fai inputfile\'."));	
//		this.addParameter(new Parameter("l", -1, true,
//				"-l   <int int ...>                       [SEMI-OPTIONAL] (either parameter '-l' or parameters '-seqId' and '-range' must be set) line(s) to map, lines that are not a gff3 entry are ignored!",
//				"Please specify the line(s) to map using '-l int int ...'."));
		this.addParameter(new Parameter("seqidRange", -1, true,
				"-seqidRange   <seqidRange1>              [OPTIONAL] only the specified seqidRange (SEQID:START-END) will be taken into account for filtering, if a file with seqidRanges was specified, this seqidRange will be ignored",
				"Please specify the range for filtering using '-seqidRange SEQID:START-END'."));
		this.addParameter(new Parameter("source", -1, true,
				"-source   <source1 source2 ...>          [OPTIONAL] only the specified source(s) will be taken into account for filtering",
				"Please specify the sources(s) for filtering using '-source source1 source2 ...'."));
		this.addParameter(new Parameter("type", -1, true,
				"-type   <type1 type2 ...>                [OPTIONAL] only the specified type(s) will be taken into account for filtering",
				"Please specify the type(s) for filtering using '-type type1 type2 ...'."));
		this.addParameter(new Parameter("score", -1, true,
				"-score   <score1 score2 ...>             [OPTIONAL] only the specified score(s) will be taken into account for filtering",
				"Please specify the score(s) for filtering using '-score score1 score2 ...'."));
		this.addParameter(new Parameter("strand", -1, true,
				"-strand   <strand strand ...>            [OPTIONAL] only the specified strand(s) will be taken into account for filtering",
				"Please specify the strand(s) for filtering using '-strand strand1 strand2 ...'."));
		this.addParameter(new Parameter("phase", -1, true,
				"-phase   <phase phase ...>               [OPTIONAL] only the specified phase(s) will be taken into account for filtering",
				"Please specify the phase(s) for filtering using '-phase phase1 phase1 ...'."));
		this.addParameter(new Parameter("attribute", -1, true,
				"-phase   <attribute attribute ...>       [OPTIONAL] only the specified attribute(s) will be taken into account for filtering; an attribute can either be entered as 'tag=tag1' or as 'value=value1'",
				"Please specify the attribute(s) for filtering using '-attribute attribute1 attribute2 ...'."));
	}
	
	public static void createInstance(String[] args) {
		instance = new FilterGFF3Parameters();
		instance.parse(args);
	}
	
	public static FilterGFF3Parameters getInstance() {
		return instance;
	}
}
