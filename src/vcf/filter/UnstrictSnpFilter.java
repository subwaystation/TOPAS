package vcf.filter;

public class UnstrictSnpFilter extends AVcfFilter{

	public UnstrictSnpFilter(String vcfLine) {
		super(vcfLine);
	}

	/**
	 * Apply an unstrict SNP filter to a VCF line.
	 * If InDels are also in the VCF line, the filter will still pass.
	 * Multiple alternate alleles are allowed.
	 * 
	 * @return <tt>true</tt> only if the VCF line has a SNP contained.
	 */
	public boolean filter() {
		boolean isUnstrictSnp = false;
		String[] splitVE = this.vcfLine.split(tab);
		String ref = splitVE[3];
		String alt = splitVE[4];

		if (ref.length() == 1 && !alt.equals(point)) {
			String[] splitAlt = alt.split(comma);
			for (int i = 0; i < splitAlt.length; i++) {
				if (splitAlt[i].length() == 1) {
					isUnstrictSnp = true;
					return isUnstrictSnp;
				}
			}
			return isUnstrictSnp;
		} else {
			return isUnstrictSnp;
		}
	}

}
