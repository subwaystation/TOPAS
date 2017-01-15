package phy_cc;

/**
 * 
 * @author heumos
 * 
 * A class representing a SNP call.
 *
 */

public class SnpCall {
	
	// the sample name the SNP belongs to
	private String sampleName;
	
	// the position of the SNP
	private String pos;
	
	// the reference of the SNP
	private String ref;
	
	// the actual SNP
	private String snp;
	
	// was it a safe or an unsafe SNP call?
	private boolean safe;

	/**
	 * A SNP call.
	 * 
	 * @param sampleName the sample name the SNP belongs to.
	 * @param pos the position of the SNP.
	 * @param ref the reference of the SNP.
	 * @param snp the actual base of the SNP.
	 * @param safe was it a safe or an unsafe SNP call?
	 */
	public SnpCall(String sampleName, String pos, String ref, String snp, boolean safe) {
		this.sampleName = sampleName;
		this.pos = pos;
		this.ref = ref;
		this.snp = snp;
		this.safe = safe;
	}
	
	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getSnp() {
		return snp;
	}

	public void setSnp(String snp) {
		this.snp = snp;
	}

	public boolean isSafe() {
		return safe;
	}

	public void setSafe(boolean safe) {
		this.safe = safe;
	}
	
	

}
