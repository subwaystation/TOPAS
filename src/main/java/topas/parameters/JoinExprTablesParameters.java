package topas.parameters;

import lib.parameters.Parameter;
import lib.parameters.Parameters;

public class JoinExprTablesParameters extends Parameters{
	
	protected static JoinExprTablesParameters instance;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void init() {
		
		this.addParameter(new Parameter("i",-1,false,Parameter.EXISTING_FILE_PARSER,
				"-i  <exprTable1 exprTable2 ...>  [REQUIRED] input exprTables (tab-delimited format)",			
				"No input exprTables specified. Use '-i exprTable1 exprTable2 ...'."));		
//		this.addParameter(new Parameter("dir",1,true,Parameter.DIRECTORY_PARSER,
//				"-dir <directory>                 [SEMI-OPTIONAL] directory from where to read the exprTables,"
//				+ " tables must be in tab-delimited format",			
//				"No directory specified. Use \'-dir directory\'."));
		this.addParameter(new Parameter("o",1, 
				"-o  <file>                       [REQUIRED] output file (contains all joined exprTables in tab-delimited format)",			
				"Please specify the output file using '-o outputfile'."));
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
		this.addParameter(new Parameter("nan",0,true,
				"-nan                             [OPTIONAL] if present, the following is done:"
				+ " all zeros in the exprTables are replaced by NaNs and if there is no value for a gene in an "
				+ "exprTable, then the value is also set to NaN",			
				null));
		this.addParameter(new Parameter("commaIn",0,true,
				"-commaIn                         [OPTIONAL] if present, numbers in double format are delimited by a ','.",			
				null));
		this.addParameter(new Parameter("commaOut",0,true,
				"-commaOut                        [OPTIONAL] if present, all dots in double values are replaced by a comma for"
				+ " working with a european version of e.g. excel",			
				null));
//		this.addParameter(new Parameter("names",-1,true,
//				"-names  <name1 name2 ...>        [OPTIONAL] the names of the input exprTables, must be in the same ordere as specified in '-i',"
//				+ "if this parameter is set when also '-dir' is given, than this parameter is ignored",			
//				"No names specified. Use '-names name1 name2 ...'."));
		
	}
	
	public static void createInstance(String[] args) {
		instance = new JoinExprTablesParameters();
		instance.parse(args);
	}
	
	public static JoinExprTablesParameters getInstance() {
		return instance;
	}

}
