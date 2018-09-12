package feature_format.gtf;

import java.util.SortedMap;
import java.util.TreeMap;


import feature_format.AFeatureFormatEntry;
import feature_format.AFeatureFormatParser;

public class GtfParser extends AFeatureFormatParser{

	public GtfParser(String line) {
		super(line);
	}
	
	public GtfParser() {
		this(null);
	}

	@Override
	protected SortedMap<String, String> createAttributes(String attributesString) {
		SortedMap<String, String> sortedAttributes = new TreeMap<String, String>();
		String[] attributes = attributesString.split(";");
		for(int i = 0; i < attributes.length; i++) {
			String[] splitSpace = attributes[i].trim().split(" ");
			sortedAttributes.put(splitSpace[0], splitSpace[1]);
		}
		return sortedAttributes;
	}

	@Override
	protected AFeatureFormatEntry createEntry(String seqId, String source,
			String type, int start, int end, String score, String strand,
			String phase, SortedMap<String, String> attributes) {
		return new GtfEntry(seqId, source, type, start, end, score, strand, phase, attributes);
	}
}
