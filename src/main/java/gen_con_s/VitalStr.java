package gen_con_s;

/**
 * @author heumos
 * This class contains constant Strings beeing important for the consensus tool.
 */
public class VitalStr {
	
	public static final String N = "N";
	public static final String TAB = "\t";
	public static final String EMPTY = "";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String COLON = ":";
	
	public static final String G = "G";
	public static final String C = "C";
	
	public static final String CONSENSUS_RATIO = "CONSENSUS_RATIO";
	public static final String MAJOR_ALLELE_COVERAGE = "MAJOR_ALLELE_COVERAGE";
	public static final String TOTAL_COVERAGE = "TOTAL_COVERAGE";
	
	public static final String DEL_CALL = "DEL_CALL";
	public static final String SN_CALL = "SN_CALL";
	public static final String INSERT_CALL = "INSERT_CALL";
	public static final String NO_POS = "NO_POS";
	
	/**
	 * New type of call according to James' request.
	 * A call is NOT problematic, if only reads for the reference are present, or
	 * if we have 1 reference base and 1 alternative base. 
	 * Else, the call is seen as problematic.
	 */
	public static final String PROBLEMATIC = "PROBLEMATIC";

}
