package vcf.filter;

/**
 * An abstract class representing a VCF filter.
 * @author heumos
 * 
 */
abstract public class AVcfFilter {

	protected static final String tab = "\t";
	protected static final String point = ".";
	protected static final String comma = ",";

	protected String vcfLine;
	protected String filterElem;
	
	public AVcfFilter(String vcfLine, String filterElem) {
		this.vcfLine = vcfLine;
		this.filterElem = filterElem;
	}

	public AVcfFilter(String vcfLine) {
		this(vcfLine, null);
	}

	public String getVcfLine() {
		return vcfLine;
	}

	public void setVcfLine(String vcfLine) {
		this.vcfLine = vcfLine;
	}
	
	public String getFilterElem() {
		return this.filterElem;
	}

	public void setFilterElem(String filterElem) {
		this.filterElem = filterElem;
	}

	protected abstract boolean filter() throws Exception;

}
