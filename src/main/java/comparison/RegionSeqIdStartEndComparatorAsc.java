package comparison;

import java.util.Comparator;

import vcf.analyse.Region;

public class RegionSeqIdStartEndComparatorAsc implements Comparator<Region> {

	@Override
	public int compare(Region region1, Region region2) {
		int compareSeqIdResult = region1.getSeqId().compareTo(region2.getSeqId());
		if (compareSeqIdResult == 0) {
			int compareStartResult = region1.getStart() - region2.getStart();
			if (compareStartResult == 0) {
				return region1.getEnd() - region1.getEnd();
			} else {
				return compareStartResult;
			}
		} else {
			return compareSeqIdResult;
		}
	}
	
	

}
