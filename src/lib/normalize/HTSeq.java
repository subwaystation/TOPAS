package lib.normalize;

import java.util.Arrays;
import java.util.HashSet;

public class HTSeq {
	
	public static final String[] SET_VALUES = new String[] {"__alignment_not_unique", "__ambiguous", "__no_feature",
		"__not_aligned", "__too_low_aQual", "alignment_not_unique", "ambiguous", "no_feature", "not_aligned", "too_low_aQual",
		""};

	public final static HashSet<String> BANNED = new HashSet<String>(Arrays.asList(SET_VALUES));

}
