package pretesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import comparison.ArrayIndexComparator;
import comparison.FastaIndexIdentifierComparatorAsc;
import comparison.FastaIndexIdentifierComparatorDesc;
import comparison.FastaIndexSequenceLengthComparatorAsc;
import core.fasta_index.FastaIndex;

public class TestSortingOnlyGetIndex {
	
	public static void main(String[] args) {
		
		String[] countries = { "France", "Spain", "Germany"};
		ArrayIndexComparator comparator = new ArrayIndexComparator(countries);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		System.out.println(Arrays.toString(indexes));
		
		// testing fasta index
		List<FastaIndex> faidxList = new ArrayList<FastaIndex>();
		faidxList.add(new FastaIndex("seq1", 34, 5, 34, 35));
		faidxList.add(new FastaIndex("seq1", 34, 5, 34, 35));
		faidxList.add(new FastaIndex("seq2", 345, 56, 34, 35));
		faidxList.add(new FastaIndex("seq5", 5, 99, 34, 35));
		System.out.println(faidxList);
		FastaIndexSequenceLengthComparatorAsc faSeqCom = new FastaIndexSequenceLengthComparatorAsc(faidxList);
		Integer[] indexes1 = faSeqCom.createIndexArray();
		Arrays.sort(indexes1, faSeqCom);
		System.out.println(Arrays.toString(indexes1));
		
		FastaIndexIdentifierComparatorDesc faIdenCom = new FastaIndexIdentifierComparatorDesc(faidxList);
		Integer[] indexes2 = faIdenCom.createIndexArray();
		Arrays.sort(indexes2, faIdenCom);
		System.out.println(Arrays.toString(indexes2));

	}

}
