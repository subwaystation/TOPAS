package comparison.gff_three_entry_comparators;

import feature_format.gff.GffThreeEntry;

import java.util.Comparator;

public class SeqIdComparatorAsc implements Comparator<GffThreeEntry> {

    @Override
    public int compare(GffThreeEntry gTE1, GffThreeEntry gTE2) {
        return gTE1.getSeqId().compareTo(gTE2.getSeqId());
    }
}
