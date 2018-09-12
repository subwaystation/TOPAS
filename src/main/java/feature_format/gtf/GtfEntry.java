package feature_format.gtf;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.Map.Entry;

import feature_format.AFeatureFormatEntry;

/**
 * @author heumos
 * A class representing a GTF Entry
 */
public class GtfEntry extends AFeatureFormatEntry{

	/**
	 * @param seqName
	 * @param source
	 * @param feature
	 * @param start
	 * @param end
	 * @param score
	 * @param strand
	 * @param frame
	 * @param attributes
	 * @param totalLines
	 */
	public GtfEntry(String seqName, String source, String feature,
			int start, int end, String score, String strand,
			String frame, SortedMap<String, String> attributes,
			long totalLines) {
		super(seqName, source, feature, start, end, score, strand, frame, attributes, totalLines);
	}

	/**
	 * @param seqName
	 * @param source
	 * @param feature
	 * @param start
	 * @param end
	 * @param score
	 * @param strand
	 * @param frame
	 * @param attributes
	 */
	public GtfEntry(String seqName, String source, String feature,
			int start, int end, String score, String strand,
			String frame, SortedMap<String, String> attributes) {
		this(seqName, source, feature, start, end, score, strand, frame, attributes, -1);

	}
	
	public String getSeqName() {
		return this.getSeqId();
	}
	
	public void setSeqName(String seqName) {
		this.setSeqId(seqName);
	}
	
	public String getFeature() {
		return this.getType();
	}
	
	public void setFeature(String feature) {
		this.setType(feature);
	}
	
	public String getFrame() {
		return this.getPhase();
	}
	
	public void setFrame(String frame) {
		this.setPhase(frame);
	}

	/**
	 * @return all attributes as a String as specified in the gff3-version for 
	 * attributes
	 */
	public String attributesToString() {
		StringBuilder sb = new StringBuilder();
		String emptyString = "";
		String semicolon = ";";
		String space = " ";
		if(this.getAttributes() == null) {
			return emptyString;
		}
		Set<Entry<String, String>> attributesSet = this.getAttributes().entrySet();
		Iterator<Entry<String, String>> iterator = attributesSet.iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, String> mapEntry = (Map.Entry<String, String>)iterator.next();
			sb.append(mapEntry.getKey());
			sb.append(space);
			sb.append(mapEntry.getValue());
			sb.append(semicolon);
			sb.append(space);
		}
		String result = sb.toString();
		return result.substring(0, result.length()-1);
	}
	

}
