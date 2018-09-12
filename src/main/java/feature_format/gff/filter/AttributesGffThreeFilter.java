package feature_format.gff.filter;

import java.util.List;
import java.util.SortedMap;

import feature_format.gff.GffThreeEntry;

public class AttributesGffThreeFilter extends AGffThreeFilter{

	public AttributesGffThreeFilter(GffThreeEntry gTE) {
		super(gTE);
	}

	@Override
	protected boolean filter(List<String> listFilter) {
		boolean matchTag = false;
		boolean matchValue = false;
		if (listFilter.isEmpty()) {
			return true;
		}
		SortedMap<String, String> attributes = this.gTE.getAttributes();
		for (int i = 0; i < listFilter.size(); i++)	 {
			String aFilter = listFilter.get(i);
			String[] aFilterSplit = aFilter.split("=");
			// attribute type to filter is tag
			if (aFilterSplit[0].toLowerCase().equals("tag")) {
				String tag = aFilterSplit[1];
				if (attributes.containsKey(tag)) {
					matchTag = true;
				}
			} else {
				matchTag = true;
			}
			// attribute type to filter is value
			if (aFilterSplit[0].toLowerCase().equals("value")) {
				String value = aFilterSplit[1];
				if (attributes.containsValue(value)) {
					matchValue = true;
				}
			} else {
				matchValue = true;
			}
		}
		return matchTag && matchValue;
	}

}
