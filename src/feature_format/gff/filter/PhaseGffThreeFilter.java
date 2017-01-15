package feature_format.gff.filter;

import java.util.List;

import feature_format.gff.GffThreeEntry;

public class PhaseGffThreeFilter extends AGffThreeFilter{

	public PhaseGffThreeFilter(GffThreeEntry gTE) {
		super(gTE);
	}

	@Override
	protected boolean filter(List<String> listFilter) {
		boolean match = false;
		if (listFilter.isEmpty()) {
			match = true;
			return match;
		}
		String phase = this.gTE.getPhase();
		for (int i = 0; i < listFilter.size(); i++) {
			if (phase.contains(listFilter.get(i))) {
				match = true;
				return match;
			}
		}
		return match;
	}

}
