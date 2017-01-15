package feature_format.gff.filter;

import java.util.List;

import feature_format.gff.GffThreeEntry;

public class ScoreGffThreeFilter extends AGffThreeFilter{

	public ScoreGffThreeFilter(GffThreeEntry gTE) {
		super(gTE);
	}

	@Override
	protected boolean filter(List<String> listFilter) {
		boolean match = false;
		if (listFilter.isEmpty()) {
			match = true;
			return match;
		}
		String score = this.gTE.getScore();
		for (int i = 0; i < listFilter.size(); i++) {
			if (score.contains(listFilter.get(i))) {
				match = true;
				return match;
			}
		}
		return match;
	}

}
