package feature_format.gff;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import feature_format.AFeatureFormatEntry;

/**
 * A class representing a GFF3-Entry.
 * @author heumos
 *  
 */
public class GffThreeEntry extends AFeatureFormatEntry{

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
	public GffThreeEntry(String seqId, String source, String type,
			int start, int end, String score, String strand,
			String phase, SortedMap<String, String> attributes,
			long totalLines) {
		super(seqId, source, type, start, end, score, strand, phase, attributes, totalLines);
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
	public GffThreeEntry(String seqId, String source, String type, int start,
			int end, String score, String strand, String phase,
			SortedMap<String, String> attributes) {
		this(seqId, source, type, start, end, score, strand, phase, attributes, -1);

	}

	/**
	 * @return all attributes as a String as specified in the gff3-version for 
	 * attributes
	 */
	public String attributesToString() {
		StringBuilder sb = new StringBuilder();
		String emptyString = "";
		String semicolon = ";";
		String equal = "=";
		if(this.getAttributes() == null) {
			return emptyString;
		}
		Set<Entry<String, String>> attributesSet = this.getAttributes().entrySet();
		Iterator<Entry<String, String>> iterator = attributesSet.iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, String> mapEntry = (Map.Entry<String, String>)iterator.next();
			sb.append(mapEntry.getKey());
			sb.append(equal);
			sb.append(mapEntry.getValue());
			sb.append(semicolon);
//			emptyString += mapEntry.getKey() + "=" + mapEntry.getValue() + ";";
		}
		String result = sb.toString();
		return result.substring(0, result.length()-1);
	}

	/**
	 * checks, if the given GFF3Entry has the tag "ID" in attributes
	 * @return
	 * 
	 */
	public boolean hasId() {
		boolean hasId = false;
		String[] attributes = this.attributesToString().split(";");
		for (int i = 0; i < attributes.length; i++) {
			String[] attribute = attributes[i].split("=");
			String tag = attribute[0];
			if (tag.equals("ID")) {
				return true;
			}
		}		
		return hasId;
	}

	/**
	 * checks, if the given GFF3Entry has the tag "Parent" in attributes
	 * @return
	 */
	public boolean hasParentValue() {
		boolean hasParent = false;
		String[] attributes = this.attributesToString().split(";");
		for (int i = 0; i < attributes.length; i++) {
			String[] attribute = attributes[i].split("=");
			String tag = attribute[0];
			if (tag.equals("Parent")) {
				return true;
			}
		}
		return hasParent;
	}

	/**
	 * @return
	 * the value of the attribute "Parent"
	 */
	public String getParentValue() {
		return this.getAttributes().get("Parent");
	}
	
	/**
	 * checks, if the given GFF3Entry has the tag "Name" in attributes
	 * @return
	 */
	public boolean hasName() {
		boolean hasName = false;
		if (this.getAttributes().containsKey("Name")) {
			hasName = true;
			return hasName;
		} else {
			return hasName;
		}
	}
	
	/**
	 * checks, if the given GFF3Entry has the tag "locus_tag" in attributes
	 * @return
	 */
	public boolean hasLocusTag() {
		if (this.getAttributes().containsKey("locus_tag")) {
			return true;
		} else {
			return false;
		}
	}

	/** check, if the given GFF3Entry and another one form a multi-feature
	 * @param entry
	 * @return
	 */
	public boolean isMultiFeature(GffThreeEntry entry) {
		boolean seqId = false;
		boolean source = false;
		boolean type = false;
		boolean strand = false;
		boolean attributes = false;
		boolean startEnd = true;
		if (this.getSeqId().equals(entry.getSeqId())) {
			seqId = true;
		}
		if (this.getSource().equals(entry.getSource())) {
			source = true;
		}
		if (this.getType().equals(entry.getType())) {
			type = true;
		}
		if (this.getAttributes().equals(entry.getAttributes())) {
			attributes = true;
		}
		if (this.getStrand().equals(entry.getStrand())) {
			strand = true;
		}
		if ((this.getStart() == entry.getStart()) && (this.getEnd() == entry.getEnd())) {//||
			//				//(this.start != entry.getStart() && this.end == entry.getEnd())) {
			startEnd = false;
		}
		return seqId && source && strand && type && attributes && startEnd;
	}
}
