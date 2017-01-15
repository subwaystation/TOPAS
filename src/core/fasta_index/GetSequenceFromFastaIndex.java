package core.fasta_index;

import io.ParseBytes;

import java.io.IOException;

public class GetSequenceFromFastaIndex {
	
	String fastaFilePath;
	FastaIndex faidx;
	
	/**
	 * Constructor for retrieving the sequence of a single FastaIndex
	 * @param fileInputStream
	 * @param faidx
	 */
	public GetSequenceFromFastaIndex(String fastaFilePath, FastaIndex faidx) {
		this.fastaFilePath = fastaFilePath;
		this.faidx = faidx;
	}
	
	/**
	 * Constructor for retrieving the sequences of multiple FastaIndices
	 * @param fastaFilePath
	 */
	public GetSequenceFromFastaIndex(String fastaFilePath) {
		this.fastaFilePath = fastaFilePath;
	}

	public String getFileInputStream() {
		return fastaFilePath;
	}

	public void setFileInputStream(String fastaFilePath) {
		this.fastaFilePath = fastaFilePath;
	}

	public FastaIndex getFaidx() {
		return faidx;
	}

	public void setFaidx(FastaIndex faidx) {
		this.faidx = faidx;
	}
	
	/**
	 * for a given FastaIndex returns the corresponding sequence from a fasta-file
	 * the returned sequence includes all newLines as in the original fasta-file
	 * @return
	 * @throws IOException
	 */
	public String getSequence() throws IOException {
		int seqLength = this.faidx.getSequenceLength();
		long firstBaseOffset =  this.faidx.getFirstBaseOffset();
		int lineBytes = this.faidx.getNumberBytes();
		int lineBases = this.faidx.getNumberBases();
		int numberByteLines = seqLength/lineBases;
		int numberBytes = seqLength%lineBases;
		int offsetLength = numberByteLines*lineBytes+numberBytes;
		int lineType = lineBytes-lineBases;
		if(numberBytes == 0) {
			offsetLength = offsetLength-lineType;
		}
		
		ParseBytes bytesParser = new ParseBytes(this.fastaFilePath, firstBaseOffset, offsetLength);
		
		String seq = bytesParser.parseBytes();

		return seq;
	}
	
	public String getSequenceMulti(long firstBaseOffset, int offsetLength, int lineType) throws IOException {
		ParseBytes bytesParser = new ParseBytes(fastaFilePath, firstBaseOffset, offsetLength);
		String seq = bytesParser.parseBytes();
		return seq;
	}

}
