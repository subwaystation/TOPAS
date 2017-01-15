package vcf;

import java.text.DecimalFormat;

/**
 * @author heumos
 * Class representing the coverage of a position in a VCF as fractions <DOUBLE>
 * It is similar to the MulitVCFAnalyser output.
 */
public class CoverageFraction {
	
	// the chromosome
	private String chrom;
	
	// the position
	private int pos;
	
	// the reference base
	private String ref;
	
	// the alternative base
	private String alt;
	
	// the coverage fraction
	private double fraction;
	
	// the double formatter
	private DecimalFormat doubleFormatter;

	public CoverageFraction(String chrom, int pos, String ref, String alt, double fraction) {
		super();
		this.chrom = chrom;
		this.pos = pos;
		this.ref = ref;
		this.alt = alt;
		this.fraction = fraction;
		this.doubleFormatter = new DecimalFormat("#0.0");
	}
	
	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		String tab = "\t";
		sB.append(this.chrom).append(tab);
		sB.append(this.pos).append(tab);
		sB.append(this.ref).append(tab);
		sB.append(this.alt).append(" (");
		sB.append(this.doubleFormatter.format(this.fraction));
		sB.append(")");
		return sB.toString();
	}

	public String getChrom() {
		return chrom;
	}

	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public double getFraction() {
		return fraction;
	}

	public void setFraction(double fraction) {
		this.fraction = fraction;
	}

}
