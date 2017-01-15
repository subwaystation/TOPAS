package feature_format.gff.gene_loci_mapper;

import io.ParseBytes;

import java.io.IOException;

import core.fasta_index.FastaIndex;

public class GetSequenceFromGeneLocus {
	
	String fastaFilePath;
	FastaIndex faidx;
	int start;
	int end;
	
	/**
	 * @param fastaFilePath
	 * @param faidx
	 * @param start
	 * @param end
	 */
	public GetSequenceFromGeneLocus(String fastaFilePath, FastaIndex faidx,
			int start, int end) {
		this.fastaFilePath = fastaFilePath;
		this.faidx = faidx;
		this.start = start;
		this.end = end;
	}
	
	/**
	 * for a given FastaIndex returns the corresponding sequence from a fasta-file
	 * the returned sequence includes all newLines as in the original fasta-file
	 * @return
	 * @throws IOException
	 */
	public String getSequenceFromGeneLocus() throws IOException {
		// calculate the firstBaseOffset of the gene locus in the sequence
		if(this.end>this.faidx.getSequenceLength()) {
			System.out.println("The end of the given locus is higher than the sequence length, please correct your input.");
		}
		if(this.start>this.faidx.getSequenceLength()) {
			System.out.println("The start of the given locus is higher than the sequence length, please correct your input.");
		}
		int geneLocusLength = this.end-this.start+1;
		int seqLengthBefore = this.start;
		long firstBaseOffset =  this.faidx.getFirstBaseOffset();
		int lineBytes = this.faidx.getNumberBytes();
		int lineBases = this.faidx.getNumberBases();
		int numberByteLines = seqLengthBefore/lineBases;
		int lineType = lineBytes-lineBases;
		int numberBytes;
		long firstBaseOffsetBegin = 0;
		if(seqLengthBefore < lineBases) {
			numberBytes = lineBytes;
			firstBaseOffsetBegin = firstBaseOffset + seqLengthBefore-lineType;
		} else {
			numberBytes = seqLengthBefore%lineBases;
			firstBaseOffsetBegin = numberByteLines*lineBytes+firstBaseOffset+numberBytes-lineType;
		}
		
		//System.out.println("numberBytes " + numberBytes);
		
		int offsetLength = 0;
		
		//System.out.println("firstBaseOffsetBegin " + firstBaseOffsetBegin);
		
		
		//System.out.println("GeneLocusLengthOld " + geneLocusLength);
		if(geneLocusLength < numberBytes) {
			offsetLength = geneLocusLength;
			//System.out.println(geneLocusLength);
		} else {
			int geneLocusLengthNew = geneLocusLength - numberBytes;
			//System.out.println("GeneLocusLengthNew "+geneLocusLengthNew);
			int numberByteLinesGLL = geneLocusLengthNew/lineBases;
			int numberBytesGLL = geneLocusLengthNew%lineBases;
			offsetLength = numberBytes + numberByteLinesGLL*lineBytes + numberBytesGLL+lineType;
			//System.out.println("OffsetLength "+ offsetLength);
		}
		
		
		ParseBytes bytesParser = new ParseBytes(this.fastaFilePath, firstBaseOffsetBegin, offsetLength);
		
		String seq = bytesParser.parseBytes();

		return seq;
	}

	public String getFastaFilePath() {
		return fastaFilePath;
	}

	public void setFastaFilePath(String fastaFilePath) {
		this.fastaFilePath = fastaFilePath;
	}

	public FastaIndex getFaidx() {
		return faidx;
	}

	public void setFaidx(FastaIndex faidx) {
		this.faidx = faidx;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	

}
