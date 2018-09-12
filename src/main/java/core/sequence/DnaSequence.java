package core.sequence;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author heumos
 *
 */
public class DnaSequence implements ISequence {
	
	public static final List<Character> DNA_IUPAC_CODES = 
			Collections.unmodifiableList(Arrays.asList('A', 'C', 'G', 'T', 'U', 'R', 'Y', 'K', 'M', 'S',
					'W', 'B', 'D', 'H', 'V', 'N', 'X', '-'));
	
	public static final List<Character> DNA_BASES =
			Collections.unmodifiableList(Arrays.asList('A', 'C', 'T', 'G', 'N'));
	
    public static final Set<String> DNA_BASES_SET;
    static {
        Set<String> seqSet = new HashSet<String>();
        seqSet.add("A");
        seqSet.add("C");
        seqSet.add("T");
        seqSet.add("G");
        DNA_BASES_SET = Collections.unmodifiableSet(seqSet);
    }

	private String header;
	private String sequenceData;
	private int sequenceLength;

	/**
	 * @param header
	 * @param sequenceData
	 */
	public DnaSequence(String header, String sequenceData) {
		this(sequenceData);
		this.header = header;
	}
	
	/**
	 * @param header
	 * @param sequenceLength
	 * @description constructs a DNA-Sequence with the identifier! (not the full header) and the
	 * length of the sequence
	 */
	public DnaSequence(String header, int sequenceLength) {
		this.header = header.trim().substring(1).split(" ")[0];
		this.sequenceLength = sequenceLength;
	}

	/**
	 * @param sequenceData
	 */
	public DnaSequence(String sequenceData) {
		this.sequenceData = sequenceData.trim().toUpperCase();
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
		return "DNA" + "\t" + this.header + "\t"
				+ this.sequenceLength;
	}
	
	public String toTab() {
		return this.header + "\t" + this.sequenceData;
	}
	
	public int getSequenceLength() {
		return this.sequenceLength;
	}

}
