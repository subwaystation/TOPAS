package feature_format.gff.filter;

import java.util.ArrayList;
import java.util.List;

import feature_format.gff.GffThreeEntry;

public class SeqIdRangeGffThreeFilter extends AGffThreeFilter{

	public SeqIdRangeGffThreeFilter(GffThreeEntry gTE) {
		super(gTE);
	}

	@Override
	protected boolean filter(List<String> listFilter) {
		boolean matchSeqId = false;
		boolean matchRange = false;
		List<String> sFList = new ArrayList<String>();
		List<String> rFList = new ArrayList<String>();
		for (int i = 0; i < listFilter.size(); i++) {
			String s = listFilter.get(i);
			String[] split = s.split(":");
			sFList.add(split[0]);
			rFList.add(split[1]);
		}
		SeqIdGffThreeFilter sF = new SeqIdGffThreeFilter(this.gTE);
		matchSeqId = sF.filter(sFList);
		RangeGffThreeFilter rF = new RangeGffThreeFilter(this.gTE);
		matchRange = rF.filter(rFList);
		return matchSeqId && matchRange;
	}

}
