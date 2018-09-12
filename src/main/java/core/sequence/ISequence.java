package core.sequence;

/**
 * @author heumos
 * An interface representing biological sequences. A biological sequence typically is either a DNA sequence
 * or a Protein sequence.
 */
public interface ISequence {
	
	/**
	 * @return
	 * Get the header/description of a sequence.
	 */
	public String getHeader();
	
	/**
	 * @param header
	 * Set the header/description of a sequence.
	 */
	public void setHeader(String header);
	
	/**
	 * @return
	 * Get the sequence content.
	 */
	public String getSequenceData();
	
	/**
	 * @param sequenceData
	 * Set the sequence content.
	 */
	public void setSequenceData(String sequenceData);

	/**
	 * @return
	 * Represent a sequence in a tab-delimited format.
	 */
	public String toTab();

}
