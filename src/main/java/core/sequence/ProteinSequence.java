package core.sequence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author heumos
 *
 */
public class ProteinSequence implements ISequence {
	
	public static final List<Character> PROTEIN_IUPAC_CODES =
			Collections.unmodifiableList(Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'Y', 'Z', 'X', '*', '-'));

	private String header;
	private String sequenceData;
	private int sequenceLength;

	/**
	 * @param header
	 * @param sequenceData
	 */
	public ProteinSequence(String header, String sequenceData) {
		this(sequenceData);
		this.header = header;
	}

	/**
	 * @param sequenceData
	 */
	public ProteinSequence(String sequenceData) {
		this.sequenceData = sequenceData.trim().toUpperCase();
	}

	/**
	 * @param header
	 * @param sequenceLength
	 * @description constructs a Protein-Sequence with the identifier! (not the full header) and the
	 * length of the sequence
	 */
	public ProteinSequence(String header, int sequenceLength) {
		this.header = header.trim().substring(1).split(" ")[0];
		this.sequenceLength = sequenceLength;
	}

	@Override
	public String getHeader() {
		return this.header;
	}

	@Override
	public void setHeader(String header) {
		this.header = header;
	}

	@Override
	public String getSequenceData() {
		return this.sequenceData;
	}

	@Override
	public void setSequenceData(String sequenceData) {
		this.sequenceData = sequenceData;
	}

	@Override
	public String toString() {
		return "Protein" + "\t" + this.header + "\t" +
				+ this.sequenceLength;
	}

	@Override
	public String toTab() {
		return this.header + "\t" + this.sequenceData;
	}

}
