package feature_format.gff.filter;

import java.util.List;

import feature_format.gff.GffThreeEntry;

public class TypeGffThreeFilter extends AGffThreeFilter{

	public TypeGffThreeFilter(GffThreeEntry gTE) {
		super(gTE);
	}

	@Override
	protected boolean filter(List<String> listFilter) {
		boolean match = false;
		String type = this.gTE.getType();
		// regions won't be taken into account
		if (type.equals("region")) {
			return false;
		}
		if (listFilter.isEmpty()) {
			match = true;
			return match;
		}
		
		for (int i = 0; i < listFilter.size(); i++) {
			if (type.contains(listFilter.get(i))) {
				match = true;
				return match;
			}
		}
		return match;
	}

}
