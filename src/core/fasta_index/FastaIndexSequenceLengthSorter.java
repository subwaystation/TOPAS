package core.fasta_index;

import java.util.Arrays;
import java.util.List;

import comparison.FastaIndexSequenceLengthComparatorAsc;
import comparison.FastaIndexSequenceLengthComparatorDesc;

public class FastaIndexSequenceLengthSorter implements IFastaIndexSorter {
	
	private List<FastaIndex> faidxList;
	
	public FastaIndexSequenceLengthSorter(List<FastaIndex> faidxList) {
		this.faidxList = faidxList;
	}

	@Override
	public Integer[] sortAsc() {
		FastaIndexSequenceLengthComparatorAsc faSeqCom = new FastaIndexSequenceLengthComparatorAsc(this.faidxList);
		Integer[] indices = faSeqCom.createIndexArray();
		Arrays.sort(indices, faSeqCom);
		return indices;		
	}

	@Override
	public Integer[] sortDesc() {
		FastaIndexSequenceLengthComparatorDesc faSeqCom = new FastaIndexSequenceLengthComparatorDesc(this.faidxList);
		Integer[] indices = faSeqCom.createIndexArray();
		Arrays.sort(indices, faSeqCom);
		return indices;	
	}

}
