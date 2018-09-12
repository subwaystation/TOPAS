package core.fasta_index;

import java.io.IOException;
import java.util.List;

/**
 * @author heumos
 *
 */
public class GetSequencesFromFastaIndices {
	
	String[] sequences;
	String fastFilePath;
	List<FastaIndex> faidxList;
	
	/**
	 * @param fastFilePath
	 * @param faidxList
	 */
	public GetSequencesFromFastaIndices(String fastFilePath, List<FastaIndex> faidxList) {
		this.fastFilePath = fastFilePath;
		this.faidxList = faidxList;
		this.sequences = new String[this.faidxList.size()];
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	public String[] getSequences() throws IOException {
		GetSequenceFromFastaIndex getSequenceFromFastaIndex = new GetSequenceFromFastaIndex(fastFilePath);
		for(int i = 0; i < this.faidxList.size(); i++) {
			int seqLength = this.faidxList.get(i).getSequenceLength();
			long firstBaseOffset =  this.faidxList.get(i).getFirstBaseOffset();
			int lineBytes = this.faidxList.get(i).getNumberBytes();
			int lineBases = this.faidxList.get(i).getNumberBases();
			int numberByteLines = seqLength/lineBases;
			int numberBytes = seqLength%lineBases;
			int offsetLength = numberByteLines*lineBytes+numberBytes;
			int lineType = lineBytes-lineBases;
			if(numberBytes == 0) {
				offsetLength = offsetLength-lineType;
			}
			this.sequences[i] = getSequenceFromFastaIndex.getSequenceMulti(firstBaseOffset, offsetLength, lineType);
		}
		return sequences;
	}

}
