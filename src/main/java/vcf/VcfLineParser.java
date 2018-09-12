package vcf;

public class VcfLineParser {
	
	// TODO extend classes fields + methods for use with VCF lines who have an annotation column
	
	private boolean chromB;
	private boolean posB;
	private boolean iDB;
	private boolean refB;
	private boolean altB;
	private boolean qualB;
	private boolean filterB;
	private boolean infoB;
	private boolean formatB;
	private boolean sampleIdsB;
	
	

	/**
	 * @param chromB
	 * @param posB
	 * @param iDB
	 * @param refB
	 * @param altB
	 * @param qualB
	 * @param filterB
	 * @param formatB
	 * @param sampleIdsB
	 */
	public VcfLineParser(boolean chromB, boolean posB, boolean iDB,
			boolean refB, boolean altB, boolean qualB, boolean filterB,
			boolean infoB, boolean formatB, boolean sampleIdsB) {
		this.chromB = chromB;
		this.posB = posB;
		this.iDB = iDB;
		this.refB = refB;
		this.altB = altB;
		this.qualB = qualB;
		this.filterB = filterB;
		this.infoB = infoB;
		this.formatB = formatB;
		this.sampleIdsB = sampleIdsB;
	}
	
	public VcfLineParser() {
		this(true, true, true, true, true, true, true, true, true, true);
	}


	/**
	 * @param line
	 * @param chromB
	 * @param posB
	 * @param iDB
	 * @param refB
	 * @param altB
	 * @param qualB
	 * @param filterB
	 * @param infoB
	 * @param formatB
	 * @param sampleIdsB
	 * @return
	 */
	public VcfEntry createVcfEntryFromLine(String line) {
		String chrom = null;
		String pos = null;
		String iD = null;
		String ref = null;
		String alt = null;
		double qual = 0.0;
		String filter = null;
		String info = null;
		String format = null;
		String sampleIds = null;
		String[] lineSplit = line.split("\t");

		if (this.chromB) {
			chrom = lineSplit[0];
		}
		if (this.posB) {
			pos = lineSplit[1];
		}
		if (this.iDB) {
			iD = lineSplit[2];
		}
		if (this.refB) {
			ref = lineSplit[3];
		}
		if (this.altB) {
			alt = lineSplit[4];
		}
		if (this.qualB) {
			try {
				qual = Double.parseDouble(lineSplit[5]);
			} catch (Exception e) {
				qual = -1.0;
			}
		}
		if (this.filterB) {
			filter = lineSplit[6];
		}
		if (this.infoB) {
			info = lineSplit[7];
		}
		if (this.formatB) {
			format = lineSplit[8];
		}
		if (this.sampleIdsB) {
			sampleIds = lineSplit[9];
		}
		return new VcfEntry(chrom, pos, iD, ref, alt, qual, filter, info, format, sampleIds);
	}

}
