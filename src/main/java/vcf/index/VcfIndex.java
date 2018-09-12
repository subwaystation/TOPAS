package vcf.index;

/**
 * A class representing a VCF_Index
 * @author heumos
 * 
 */
public class VcfIndex {
	
	// the CHROM of the VCF_Index
	private String chrom;
	// the POS of the VCF_Index
	private String pos;
	// the offset of the first VcfLine which belongs to this index
	private long firstVcfLineOffset;
	// the length of the offet in bytes
	private int offsetLength;
	
	/**
	 * @param chrom the chromosome of the VcfLine marking the beginning of this index
	 * @param pos the position of the VcfLine marking the beginning of this index
	 * @param firstVcfLineOffset the offset in bytes where the VcfLine, marking the beginning of this index, begins
	 * @param offsetLength the offset length in bytes
	 */
	public VcfIndex(String chrom, String pos, long firstVcfLineOffset, int offsetLength) {
		this.chrom = chrom;
		this.pos = pos;
		this.firstVcfLineOffset = firstVcfLineOffset;
		this.offsetLength = offsetLength;
	}
	
	/**
	 * @return the offset length of a VCF_Index
	 */
	public int getOffsetLength() {
		return this.offsetLength;
	}
	
	public void setOffsetLength(int offsetLength) {
		this.offsetLength = offsetLength;
	}

	/**
	 * @return the chromosome of a VCF_Index
	 */
	public String getChrom() {
		return chrom;
	}

	/** 
	 * Set the chromosome of a VCF_Index
	 * @param chrom
	 */
	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	/**
	 * @return the position of a VCF_Index
	 */
	public String getPos() {
		return pos;
	}

	/**
	 * Set the position of a VCF_Index
	 * @param pos
	 */
	public void setPos(String pos) {
		this.pos = pos;
	}

	/**
	 * @return the firstVcfLineOffset of a VCF_Index
	 */
	public long getFirstVcfLineOffset() {
		return firstVcfLineOffset;
	}

	/**
	 * Set the firstVcfLineOffset of a VCF_Index
	 * @param firstVcfLineOffset
	 */
	public void setFirstVcfLineOffset(long firstVcfLineOffset) {
		this.firstVcfLineOffset = firstVcfLineOffset;
	}
	
	@Override
	public String toString() {
		String vaidxIndex = null;
		String t = "\t";
		StringBuilder sB = new StringBuilder();
		sB.append(this.chrom);
		sB.append(t);
		sB.append(this.pos);
		sB.append(t);
		sB.append(String.valueOf(this.firstVcfLineOffset));
		sB.append(t);
		sB.append(String.valueOf(this.offsetLength));
		vaidxIndex = sB.toString();
		return vaidxIndex;
	}
	

}
