package feature_format;

import java.util.SortedMap;

/**
 * An abstract feature format parser gets a line and parses this line
 * returning it in an abstract feature format, being either GFF3 or GTF format
 * @author heumos
 *
 */
public abstract class AFeatureFormatParser {
	
	// the line to be parsed
	private String line;
	
	public AFeatureFormatParser(String line) {
		this.line = line;
	}
	
	public AFeatureFormatParser() {
		this(null);
	}
	
	public String getLine() {
		return this.line;
	}
	
	public void setLine(String line) {
		this.line = line;
	}
	
	public AFeatureFormatEntry scanLine() {
		// split featureLine by tab to get all the different columns
		String[] split = this.line.split("\t");

		// fetch the seqId
		String seqId = split[0];

		// fetch the source
		String source = split[1];

		// fetch the type
		String type = split[2];

		// fetch the start
		int start = Integer.parseInt(split[3]);

		// fetch the end
		int end = Integer.parseInt(split[4]);

		// fetch the score
		String score = split[5];

		// fetch the strand
		String strand = split[6];

		// fetch the phase
		String phase = split[7];

		// fetch the attributes
		SortedMap<String, String> attributes = createAttributes(split[8]);
		
		return createEntry(seqId, source, type, start, end, score, strand, phase, attributes);

	}
	
	/**
	 * @param attributesString
	 * @return
	 */
	protected abstract SortedMap<String, String> createAttributes(String attributesString);
	
	/**
	 * @param seqId
	 * @param source
	 * @param type
	 * @param start
	 * @param end
	 * @param strand
	 * @param phase
	 * @param attributes
	 * @return
	 */
	protected abstract AFeatureFormatEntry createEntry(String seqId, String source, String type,
			int start, int end, String score, String strand, String phase, SortedMap<String, String> attributes);

}
