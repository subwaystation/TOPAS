package feature_format.gff.filter;

import java.util.List;

import feature_format.gff.GffThreeEntry;

public class SeqIdGffThreeFilter extends AGffThreeFilter{

	public SeqIdGffThreeFilter(GffThreeEntry gTE) {
		super(gTE);
	}

	@Override
	protected boolean filter(List<String> listFilter) {
		boolean match = false;
		String seqId = this.gTE.getSeqId();
		if (listFilter.isEmpty()) {
			match = true;
			return match;
		}
		for (int i = 0; i < listFilter.size(); i++) {
			if (seqId.contains(listFilter.get(i))) {
				match = true;
				return match;
			}
		}
		return match;
	}

}
