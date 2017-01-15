package core.fasta_index;

/**
 * @author heumos
 *
 * A class representing a FastaIndex.
 */
public class FastaIndex {
	
	// header / id of the sequence
	private String sequenceName;
	// length of the sequence
	private int sequenceLength;
	// the offset of the first base in the file
	
	private long firstBaseOffset;
	// the number of bases in each fasta line
	private int numberBases;
	// the number of bytes in each fasta line
	private int numberBytes;
	
	/**
	 * @param sequenceName
	 * @param sequenceLength
	 * @param firstBaseOffset
	 * @param numberBases
	 * @param numberBytes
	 */
	public FastaIndex(String sequenceName, int sequenceLength,
			long firstBaseOffset, int numberBases, int numberBytes) {
		this.sequenceName = extractSequenceIdentifier(sequenceName);
		this.sequenceLength = sequenceLength;
		this.firstBaseOffset = firstBaseOffset;
		this.numberBases = numberBases;
		this.numberBytes = numberBytes;
	}

	/**
	 * @return
	 */
	public String getSequenceName() {
		return sequenceName;
	}

	/**
	 * @param sequenceName
	 */
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	/**
	 * @return
	 */
	public int getSequenceLength() {
		return sequenceLength;
	}

	/**
	 * @param sequenceLength
	 */
	public void setSequenceLength(int sequenceLength) {
		this.sequenceLength = sequenceLength;
	}

	/**
	 * @return
	 */
	public long getFirstBaseOffset() {
		return firstBaseOffset;
	}

	/**
	 * @param firstBaseOffset
	 */
	public void setFirstBaseOffset(int firstBaseOffset) {
		this.firstBaseOffset = firstBaseOffset;
	}

	/**
	 * @return
	 */
	public int getNumberBases() {
		return numberBases;
	}

	/**
	 * @param numberBases
	 */
	public void setNumberBases(int numberBases) {
		this.numberBases = numberBases;
	}

	/**
	 * @return
	 */
	public int getNumberBytes() {
		return numberBytes;
	}

	/**
	 * @param numberBytes
	 */
	public void setNumberBytes(int numberBytes) {
		this.numberBytes = numberBytes;
	}
	
	/**
	 * @param sequenceName
	 * @return
	 */
	private String extractSequenceIdentifier(String sequenceName) {
		if(sequenceName.startsWith(">")) {
			String[] s = sequenceName.trim().substring(1).split(" ");
			return s[0].trim();
		}
		return sequenceName;
	}
	
	
	@Override
	public String toString() {
		return sequenceName + "\t" + Integer.toString(sequenceLength) + "\t"
				+ Long.toString(firstBaseOffset) + "\t"
				+ Integer.toString(numberBases) + "\t"
				+ Integer.toString(numberBytes);
	}
	

}
