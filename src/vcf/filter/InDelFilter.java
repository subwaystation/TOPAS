package vcf.filter;

public class InDelFilter extends AVcfFilter {
	
//	public InDelFilter(String vcfLine, String filterElem) {
//		super(vcfLine, filterElem);
//	}

	public InDelFilter(String vcfEntry) {
		super(vcfEntry);
	}

	@Override
	public boolean filter() throws Exception {
		boolean isInDel = false;
		String[] splitVE = this.vcfLine.split(tab);
		String ref = splitVE[3];
		String alt = splitVE[4];
		if (ref.length() > 1) {
			isInDel = true;
			return isInDel;
		} else {
			String[] splitAlt = alt.split(comma);
			for (int i = 0; i < splitAlt.length; i++) {
				if (splitAlt[i].length() == 1) {
					return isInDel;
				}
			}
			isInDel = true;
			return isInDel;
		}
	}		
}
