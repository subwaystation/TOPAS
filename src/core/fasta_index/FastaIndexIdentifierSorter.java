package core.fasta_index;

import java.util.Arrays;
import java.util.List;

import comparison.FastaIndexIdentifierComparatorAsc;
import comparison.FastaIndexIdentifierComparatorDesc;

public class FastaIndexIdentifierSorter implements IFastaIndexSorter{
	
private List<FastaIndex> faidxList;
	
	public FastaIndexIdentifierSorter(List<FastaIndex> faidxList) {
		this.faidxList = faidxList;
	}

	@Override
	public Integer[] sortAsc() {
		FastaIndexIdentifierComparatorAsc faIdenCom = new FastaIndexIdentifierComparatorAsc(this.faidxList);
		Integer[] indices = faIdenCom.createIndexArray();
		Arrays.sort(indices, faIdenCom);
		return indices;		
	}

	@Override
	public Integer[] sortDesc() {
		FastaIndexIdentifierComparatorDesc faIdenCom = new FastaIndexIdentifierComparatorDesc(this.faidxList);
		Integer[] indices = faIdenCom.createIndexArray();
		Arrays.sort(indices, faIdenCom);
		return indices;
	}

}
