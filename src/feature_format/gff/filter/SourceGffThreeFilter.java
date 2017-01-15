package feature_format.gff.filter;

import java.util.List;

import feature_format.gff.GffThreeEntry;

public class SourceGffThreeFilter extends AGffThreeFilter{

	public SourceGffThreeFilter(GffThreeEntry gTE) {
		super(gTE);
	}

	@Override
	protected boolean filter(List<String> listFilter) {
		boolean match = false;
		if (listFilter.isEmpty()) {
			match = true;
			return match;
		}
		String source = this.gTE.getSource();
		for (int i = 0; i < listFilter.size(); i++) {
			if (source.contains(listFilter.get(i))) {
				match = true;
				return match;
			}
		}
		return match;
	}

}
