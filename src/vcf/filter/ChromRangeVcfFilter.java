package vcf.filter;

public class ChromRangeVcfFilter extends AVcfFilter{
	
	public ChromRangeVcfFilter(String vcfLine, String filterElem) {
		super(vcfLine, filterElem);
	}

	@Override
	public boolean filter() throws Exception {
		boolean match = false;
		String[] splitVcfEntry = this.vcfLine.split("\t");
		String chrom = splitVcfEntry[0];

		String chromRange = this.filterElem;
		String[] splitChromRange = chromRange.split(":");
		if (splitChromRange.length < 2) {
			match = false;
			return match;
		} else {
			String chromFilter = splitChromRange[0];
			RangeVcfFilter rVF = new RangeVcfFilter(this.vcfLine, this.filterElem);
			boolean matchRange = rVF.filter();
			if (chromFilter.equals(chrom) && matchRange) {
				match = true;
				return match;
			} else {
				return match;
			}	
		}	
	}

}
