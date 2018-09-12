package feature_format.gff.filter;

import java.util.List;

import feature_format.gff.GffThreeEntry;

public class RangeGffThreeFilter extends AGffThreeFilter{

	public RangeGffThreeFilter(GffThreeEntry gTE) {
		super(gTE);
	}

	@Override
	protected boolean filter(List<String> listFilter) {
		boolean match = false;
		if (listFilter.isEmpty()) {
			match = true;
			return match;
		}
		int start = this.gTE.getStart();
		int end = this.gTE.getEnd();
		for (int i = 0; i < listFilter.size(); i++) {
			if (listFilter.get(i).toLowerCase().equals("all")) {
				match = true;
				return match;
			}
			String[] splitRange = listFilter.get(i).split("-");
			int splitStart;
			int splitEnd;
			// single nucleotide
			if (splitRange.length == 1) {
				splitStart = Integer.parseInt(splitRange[0]);
				splitEnd =Integer.parseInt(splitRange[0]);
			} else {
				splitStart = Integer.parseInt(splitRange[0]);
				splitEnd = Integer.parseInt(splitRange[1]);
			}
			if (start>=splitStart&&end<=splitEnd) {
				match = true;
				return match;
			}
		}
		return match;
	}

}
