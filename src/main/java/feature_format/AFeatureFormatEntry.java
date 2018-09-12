package feature_format;

import java.util.SortedMap;

/**
 * @author heumos
 * An abstract class representing an entry of any unspecific feature format. The names of the fields
 * are taken from a GFF3 Entry, don't get confused, when working with GTF Entries!
 * 
 */
public abstract class AFeatureFormatEntry {

	// the ID of the feature, called 'seqName' in GTF
	private String seqId;
	// which software/database generated this feature
	private String source;
	// type of the feature, simply called 'feature' in GTF
	private String type;
	// start of the feature
	private int start;
	// end of the feature
	private int end;
	// the score of the feature, normally a floating point number
	// if there is no score available, the score simply is a dot '.'
	private String score;
	// the strand of the feature
	// the feature can either be plus, minus, or not stranded
	private String strand;
	// the phase of the feature
	// only for features of type "CDS"
	// indicates the number of bases that should be removed from the beginning of this feature (0, 1, 2,)
	// to reach the first base of the next codon
	// if there is no phase available, the phase simply is a dot '.'
	// called 'frame' in GTF 
	private String phase;
	// a HashMap of feature attributes in the form: 'tag=value' in GFF3 and 'tag "value";' in GTF
	private SortedMap<String, String> attributes;
	// the line of the entry
	private long line;

	/**
	 * @param seqId
	 * @param source
	 * @param type
	 * @param start
	 * @param end
	 * @param score
	 * @param strand
	 * @param phase
	 * @param attributes
	 * @param totalLines
	 */
	public AFeatureFormatEntry(String seqId, String source, String type,
			int start, int end, String score, String strand,
			String phase, SortedMap<String, String> attributes,
			long totalLines) {
		this.seqId = seqId;
		this.source = source;
		this.type = type;
		this.start = start;
		this.end = end;
		this.score = score;
		this.strand = strand;
		this.phase = phase;
		this.attributes = attributes;
		this.line = totalLines;		
	}

	/**
	 * @param seqId
	 * @param source
	 * @param type
	 * @param start
	 * @param end
	 * @param score
	 * @param strand
	 * @param phase
	 * @param attributes
	 */
	public AFeatureFormatEntry(String seqId, String source, String type, int start,
			int end, String score, String strand, String phase,
			SortedMap<String, String> attributes) {
		this(seqId, source, type, start, end, score, strand, phase, attributes, -1);

	}

	public long getLine() {
		return this.line;
	}

	public void setLine(long line) {
		this.line = line;
	}	

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getStrand() {
		return strand;
	}

	public void setStrand(String strand) {
		this.strand = strand;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public SortedMap<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(SortedMap<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public int getFeatureLength() {
		return this.end - this.start+1;
	}

	private String startToString() {
		return Integer.toString(start);
	}

	private String endToString() {
		return Integer.toString(end);
	}

	/**
	 * @return all attributes as a String
	 */
	protected abstract String attributesToString();

	@Override
	public String toString() {
		String tab = "\t";
		StringBuilder sb = new StringBuilder();
		sb.append(this.seqId);
		sb.append(tab);
		sb.append(this.source);
		sb.append(tab);
		sb.append(this.type);
		sb.append(tab);
		sb.append(startToString());
		sb.append(tab);
		sb.append(endToString());
		sb.append(tab);
		sb.append(this.score);
		sb.append(tab);
		sb.append(this.strand);
		sb.append(tab);
		sb.append(this.phase);
		sb.append(tab);
		sb.append(attributesToString());
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		boolean equal = false;
		if (o instanceof AFeatureFormatEntry) {
			AFeatureFormatEntry that = (AFeatureFormatEntry) o;
			equal = (this.getSeqId().equals(that.getSeqId())&&
					this.getAttributes().equals(that.getAttributes())&&
					this.getEnd() == that.getEnd()&&
					this.getPhase().equals(that.getPhase())&&
					this.getScore().equals(that.getScore())&&
					this.getStart() == that.getStart()&&
					this.getSource().equals(that.getSource())&&
					this.getStrand().equals(that.getStrand())&&
					this.getType().equals(that.getType()));
		}
		return equal;

	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

}
