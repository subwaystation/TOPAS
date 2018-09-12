package vcf.filter;

public class VtVcfFilter extends AVcfFilter{

	public VtVcfFilter(String vcfEntry, String filterElem) {
		super(vcfEntry, filterElem);
	}

	@Override
	public boolean filter() {
		boolean match = false;
		if (this.vcfLine.contains(this.filterElem)) {
			match = true;
			return match;
		} else {
			return match;
		}
	}

}
