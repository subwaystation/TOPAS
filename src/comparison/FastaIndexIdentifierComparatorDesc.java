package comparison;

import java.util.Comparator;
import java.util.List;

import core.fasta_index.FastaIndex;

public class FastaIndexIdentifierComparatorDesc implements Comparator<Integer> {
	
	private List<FastaIndex> faidxList;

    public FastaIndexIdentifierComparatorDesc(List<FastaIndex> faidxList) {
        this.faidxList = faidxList;
    }

    public Integer[] createIndexArray() {
        Integer[] indexes = new Integer[faidxList.size()];
        for (int i = 0; i < faidxList.size(); i++) {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2) {
         // Autounbox from Integer to int to use as array indexes
        return -1*(faidxList.get(index1).getSequenceName().compareTo(faidxList.get(index2).getSequenceName()));
    }

}
