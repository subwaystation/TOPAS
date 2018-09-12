package comparison.gff_three_entry_comparators;

import feature_format.gff.GffThreeEntry;

import java.util.Comparator;

public class SeqIdStartEndComparatorAsc implements Comparator<GffThreeEntry>{

	@Override
	public int compare(GffThreeEntry gTE1, GffThreeEntry gTE2) {
		int compareSeqIdResult = gTE1.getSeqId().compareTo(gTE2.getSeqId());
		if (compareSeqIdResult == 0) {
			int compareStartResult = gTE1.getStart() - gTE2.getStart();
			if (compareStartResult == 0) {
				return gTE1.getEnd() - gTE2.getEnd();
			} else {
				return compareStartResult;
			}
		} else {
			return compareSeqIdResult;
		}
	}

}
