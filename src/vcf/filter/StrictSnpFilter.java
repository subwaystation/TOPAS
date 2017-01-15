package vcf.filter;

public class StrictSnpFilter extends AVcfFilter{
	
	public StrictSnpFilter(String vcfLine) {
		super(vcfLine);
	}
	
	/**
	 * Apply a strict SNP filter to a VCF line.
	 * If InDels are also in the VCF line, the filter will not pass!
	 * Multiple alternate alleles are allowed!
	 * 
	 * @return <tt>true</tt> only if the VCF line was a SNP and only a SNP.
	 */
	public boolean filter() {
		boolean isStrictSnp = false;
		String[] splitVE = this.vcfLine.split(tab);
		String ref = splitVE[3];
		String alt = splitVE[4];

		if (ref.length() == 1 && !alt.equals(point)) {
			String[] splitAlt = alt.split(comma);
			for (int i = 0; i < splitAlt.length; i++) {
				if (splitAlt[i].length() > 1) {
					return isStrictSnp;
				}
			}
			isStrictSnp = true;
			return isStrictSnp;
		} else {
			return isStrictSnp;
		}
	}

}
