package feature_format.gff.filter;

import java.util.List;

import feature_format.gff.GffThreeEntry;

public class RangeGffThreeFinder extends AGffThreeFilter{

	public RangeGffThreeFinder(GffThreeEntry gTE) {
		super(gTE);
	}

	@Override
	protected boolean filter(List<String> listFilter) {
		boolean find = false;
		if (listFilter.isEmpty()) {
			find = true;
			return find;
		}
		int start = this.gTE.getStart();
		int end = this.gTE.getEnd();
		for (int i = 0; i < listFilter.size(); i++) {
			if (listFilter.get(i).toLowerCase().equals("all")) {
				find = true;
				return find;
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
			if (splitStart>=start&&splitEnd<=end) {
				find = true;
				return find;
			}
		}
		return find;
	}
	
	public boolean filter(int pos) {
		boolean find = false;
		int start = this.gTE.getStart();
		int end = this.gTE.getEnd();
		int splitStart = pos;
		int splitEnd = pos;
		if (splitStart>=start&&splitEnd<=end) {
			find = true;
			return find;
		}
		return find;
	}
	
	

}
