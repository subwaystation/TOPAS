package feature_format.gff.validate;

import feature_format.gff.GffThreeEntry;

// an object containing a gff3entry and a line
// this is necessary to later obtain the line, where the error occured
public class GffThreeEntryLine {
	
	private GffThreeEntry gte;
	private long totalLines;
	
	/**
	 * @param gte
	 * @param totalLines
	 */
	public GffThreeEntryLine(GffThreeEntry gte, long totalLines) {
		this.gte = gte;
		this.totalLines = totalLines;
	}

	public GffThreeEntry getGte() {
		return gte;
	}

	public void setGte(GffThreeEntry gte) {
		this.gte = gte;
	}

	public long getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(long totalLines) {
		this.totalLines = totalLines;
	}

}
