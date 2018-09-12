package vcf.filter;

public class IdVcfFilter extends AVcfFilter{

	public IdVcfFilter(String vcfEntry, String filterElem) {
		super(vcfEntry, filterElem);
	}

	@Override
	public boolean filter() {
		boolean match = false;
		if (this.vcfLine.contains("\t" + this.filterElem + "\t")) {
			match = true;
			return match;
		} else {
			return match;
		}
	}
}
