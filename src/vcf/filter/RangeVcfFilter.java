package vcf.filter;

public class RangeVcfFilter extends AVcfFilter{

	public RangeVcfFilter(String vcfEntry, String filterElem) {
		super(vcfEntry, filterElem);
	}

	@Override
	public boolean filter() throws Exception {
		boolean match = false;
		String[] splitVcfEntry = this.vcfLine.split("\t");
		int pos = Integer.parseInt(splitVcfEntry[1]);

		String chromRange = this.filterElem;
		String[] splitChromRange = chromRange.split(":");

		String range = splitChromRange[1];
		if (range.toLowerCase().equals("all")) {
			match = true;
			return match;
		}
		String[] splitRange = range.split("-");
		int startRange = Integer.parseInt(splitRange[0]);
		int endRange = Integer.parseInt(splitRange[1]);
		if (pos >= startRange && pos <= endRange) {
			match = true;
			return match;
		} else {
			return match;
		}	
	}	
}
