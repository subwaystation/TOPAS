package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class CorrectFastaParameters extends Parameters {
	
	protected static CorrectFastaParameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {

		this.addParameter(new Parameter("i",-1,Parameter.EXISTING_FILE_PARSER,
				"-i  <file1 file 2 ...>          [REQUIRED] input file(s) (fasta format)",			
				"No input file(s) specified. Use '-i inputfile1 inputfile2 ...'."));				
		this.addParameter(new Parameter("o",1,Parameter.DIRECTORY_PARSER,
				"-o  <directory>                 [REQUIRED] output directory for the corrected fasta file(s),"
				+ " output file(s) will be named 'corrected.inputfile'",			
				"Please specify the output directory using '-o directory'"));	
		this.addParameter(new Parameter("st", 1 ,
				"-st <String>                    [REQUIRED] sequence type of the corresponding fasta file",
				"Please specify the sequence type of the fasta file. " +
				"Possible sequence types are 'dna' or 'protein'."));
		this.addParameter(new Parameter("width", 1 , Parameter.INT_PARSER,
				"-width <int>                    [REQUIRED] the width of each sequence line of the resulting fasta file",
				"Please specify the sequence width of the resulting fasta file."));
		this.addParameter(new Parameter("eol", 1 ,true,
				"-eol <String>                   [OPTIONAL] the newline type of the resulting fasta file given in decimal ASCII decoding: '10' for 'LF', '13' for 'CR' and '1310' for 'CRLF'; DEFAULT: the newline type of the input file",
				null));
	}
	
	public static void createInstance(String[] args) {
		instance = new CorrectFastaParameters();
		instance.parse(args);
	}
	
	public static CorrectFastaParameters getInstance() {
		return instance;
	}

}
