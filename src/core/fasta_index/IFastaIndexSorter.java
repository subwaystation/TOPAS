package core.fasta_index;

public interface IFastaIndexSorter {
	
	/**
	 * @param faidxList, an unsorted list of FastaIndices
	 * @return an IntegerArray which contains the sorted Indices of the inserted faidxList
	 */
	public Integer[] sortAsc();
	
	public Integer[] sortDesc();

}
