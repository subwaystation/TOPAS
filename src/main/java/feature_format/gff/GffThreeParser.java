package feature_format.gff;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import comparison.gff_three_entry_comparators.GffThreeAttributesComparison;
import feature_format.AFeatureFormatEntry;
import feature_format.AFeatureFormatParser;

public class GffThreeParser extends AFeatureFormatParser{
	
	public GffThreeParser(String line) {
		super(line);
	}
	
	public GffThreeParser() {
		this(null);
	}
 
	@Override
	protected SortedMap<String, String> createAttributes(String attributesString) {
		SortedMap<String, String> sortedAttributes = new TreeMap<String, String>(
				new Comparator<String>() {
					@Override
					public int compare(String e1Key, String e2Key) {
						return GffThreeAttributesComparison.compareAttributes(e1Key, e2Key);
					}
				});
		String[] attributes = attributesString.split(";");
		for(int i = 0; i < attributes.length; i++) {
			String[] splitEqual = attributes[i].split("=");
			sortedAttributes.put(splitEqual[0], splitEqual[1]);
		}
		return sortedAttributes;
	}

	@Override
	protected AFeatureFormatEntry createEntry(String seqId, String source,
			String type, int start, int end, String score, String strand,
			String phase, SortedMap<String, String> attributes) {
		return new GffThreeEntry(seqId, source, type, start, end, score, strand, phase, attributes);
	}
}
