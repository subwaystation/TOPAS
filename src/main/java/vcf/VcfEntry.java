package vcf;

import java.util.Arrays;

import utils.ArrayUtils;

/**
 * A class representing a VCF Entry.
 * @author heumos
 *
 */
public class VcfEntry {

	// the CHROM of the entry
	private String chrom;
	// the POS of the entry
	private String pos;
	// the ID of the entry
	private String iD;
	// the REF of the entry
	private String ref;
	// the ALT of the entry
	private String alt;
	// the QUAL of the entry
	private double qual;
	// the FILTER of the entry
	private String filter;
	// the INFO of the entry
	private String info;
	// the optional ANNOTATION of the entry
	private String annotation;
	// the FORMAT of the entry
	private String format;
	// the Sample_IDs of the entry
	private String sampleIds;

	/**
	 * @param chrom
	 * @param pos
	 * @param iD
	 * @param ref
	 * @param alt
	 * @param qual
	 * @param filter
	 * @param info
	 * @param annotation
	 * @param format
	 * @param sampleIds
	 */
	public VcfEntry(String chrom, String pos, String iD, String ref,
			String alt, double qual, String filter, String info,
			String annotation, String format, String sampleIds) {
		this.chrom = chrom;
		this.pos = pos;
		this.iD = iD;
		this.ref = ref;
		this.alt = alt;
		this.qual = qual;
		this.filter = filter;
		this.info = info;
		this.annotation = annotation;
		this.format = format;
		this.sampleIds = sampleIds;
	}

	public VcfEntry(String chrom, String pos, String iD, String ref,
			String alt, double qual, String filter, String info,
			String format, String sampleIds) {
		this(chrom, pos, iD, ref, alt, qual, filter, info, null, format, sampleIds);
	}

	public VcfEntry() {
		this(null, null, null, null, null, -1.0, null, null, null, null);
	}

	/**
	 * checks, if all the fields of the vcf entry are null and if the quality
	 * field is -1.0 (meaning that the quality was '.')
	 * @return
	 * true, if all the fields of the vcf entry are null and the quality is -1.0
	 * else false
	 */
	public boolean isEmpty() {
		boolean isEmpty = false;
		if (this.getChrom() == null && this.getPos()== null && this.getiD() ==null
				&& this.getRef() == null && this.getAlt() == null && this.getAnnotation() == null
				&& this.getFilter() == null && this.getQual() == -1.0 && this.getFormat() == null
				&& this.getInfo() == null && this.getSampleIds() == null) {
			isEmpty = true;
		}
		return isEmpty;
	}

	public String getChrom() {
		return chrom;
	}

	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getiD() {
		return iD;
	}

	public void setiD(String iD) {
		this.iD = iD;
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

	public double getQual() {
		return qual;
	}

	public void setQual(double qual) {
		this.qual = qual;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getSampleIds() {
		return sampleIds;
	}

	public void setSampleIds(String sampleIds) {
		this.sampleIds = sampleIds;
	}

	/**
	 * checks, if this VcfEntry has exactly the same format as that VcfEntry
	 * @param vE
	 * @return
	 */
	public boolean equalsFormat(VcfEntry vE) {
		String thisF = this.getFormat();
		String thatF = vE.getFormat();
		return thisF.equals(thatF);
	}

	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		String tab = "\t";
		sB.append(this.chrom);
		sB.append(tab);
		sB.append(this.pos);
		sB.append(tab);
		sB.append(this.iD);
		sB.append(tab);
		sB.append(this.ref);
		sB.append(tab);
		sB.append(this.alt);
		sB.append(tab);
		sB.append(this.qual);
		sB.append(tab);
		sB.append(this.filter);
		sB.append(tab);
		sB.append(this.info);
		sB.append(tab);
		sB.append(this.format);
		sB.append(tab);
		sB.append(this.sampleIds);
		return sB.toString();
	}

	public boolean isDel() {
		boolean isDel = false;
		for (String base : this.getRef().split(",")) {
			if (base.length() > 1) {
				isDel = true;
				return isDel;
			}
		}
		return isDel;
	}

	public boolean isMultiDel() {
		boolean isMultiDel = false;
		boolean seen = false;
		for (String base: this.getRef().split(",")) {
			if (base.length() > 1 && seen) {
				isMultiDel = true;
				return isMultiDel;
			} else if (base.length() > 1) {
				seen = true;
			}
		}
		return isMultiDel;
	}

	/**
	 * make the current Vcf Entry have 
	 * the fields of the given one
	 * @param vE
	 */
	public void setEntry(VcfEntry vE) {
		this.chrom = vE.getChrom();
		this.pos = vE.getPos();
		this.iD = vE.getiD();
		this.ref = vE.getRef();
		this.alt = vE.getAlt();
		this.filter = vE.getFilter();
		this.info = vE.getInfo();
		this.format = vE.getFormat();
		this.sampleIds = vE.getSampleIds();
		this.qual = vE.getQual();
	}

	/**
	 * This function can only be applied to VCF entries
	 * which originate from the GATK Unified Genotyper!
	 * Alters the format and sample field of the given VCF entry:
	 * Only the AD field is left!
	 */
	public boolean setFormatAd() {
		this.setFormat("AD");
		try {
			this.setSampleIds(this.getSampleIds().split(":")[1]);
			return true;
		} catch (Exception e) {
			return false;
			// TODO: handle exception
		}
	}


	/**
	 * Does this VcfEntry have a SNP?
	 * @return true, if it has a SNP, else false
	 */
	public boolean hasSnp() {
		boolean altSingle = false;
		boolean refSingle = false;
		if (this.sampleIds.trim().equals("./.") || this.sampleIds.trim().equals(".")) {
			return false;
		} else {
			if (this.alt.equals(".")) {
				return false;
			} else {
				String[] refSplit = this.ref.split(",");
				String[] altSplit = this.alt.split(",");
				for (String base : refSplit) {
					if (base.length() == 1) {
						refSingle = true;
						break;
					}
				} 
				for (String base : altSplit) {
					if (base.length() == 1) {
						altSingle = true;
						break;
					}
				}
				return altSingle && refSingle;
			}
		}
	}

	/**
	 * @param frequency
	 * @return true, if the entry has a SNP but is not heterozygous (ergo homozygous), else false
	 */
	public boolean hasSnpNotHeterozygous(double frequency, double punishmentRatio) {
		return this.hasSnp() && !this.isHeterozygous(frequency, punishmentRatio);
	}

	/**
	 * Get the total coverage of this VcfEntry.
	 * @return
	 */
	public int getCoverage() {
		int cov = 0;
		if (this.sampleIds.trim().equals("./.") || this.sampleIds.trim().equals(".")) {
			return cov;
		} else {
			if (this.alt.equals(".")) {
				return Integer.parseInt(this.sampleIds.split(":")[1]);
			} else {
				String[] samplesSplit = this.sampleIds.split(":");
				String aD = samplesSplit[1];
				String[] aDSplit = aD.split(",");
				for (String baseCov : aDSplit) {
					cov += Integer.parseInt(baseCov);
				}
				return cov;
			}
		}
	}

	public boolean isHeterozygous(double frequency, double punishmentRatio) {
		if (this.hasSnp()) {
			String[] altSplit = this.alt.split(",");
			String[] refSplit = this.ref.split(",");
			String[] samplesSplit = this.sampleIds.split(":");
			String[] aDSplit = samplesSplit[1].split(",");
			double totalCov = 0.0;
			double highestCov = 0.0;
			// check if we have genuine SNP
			if (altSplit.length == 1 && refSplit.length == 1) {
				String refBase = String.valueOf(refSplit[0].charAt(0));
				String altBase = String.valueOf(altSplit[0].charAt(0));
				// check if base lengths are 1
				if (refBase.length() == 1 && altBase.length() == 1) {
					// collect coverages
					double[] coverages = {Double.valueOf(aDSplit[0]), Double.valueOf(aDSplit[1])};
					// check for punishment
					if ((refBase.equals("C") && altBase.equals("T")) ||
							refBase.equals("G") && altBase.equals("A")) {
						coverages[1] = coverages[1] * punishmentRatio;
					}
					totalCov = coverages[0] + coverages[1];
					highestCov = Math.max(coverages[0], coverages[1]);
					// if position of the highest coverage is the first one, then we have the reference as the winner,
					// giving us a homozygous position
					// if the frequency is reached, then we have a homozygous position, so we should not reach it
					if (!frequencyReached(frequency, totalCov, highestCov)) {
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			} else {
				return true;
			}

		} else {
			return false;
		}
		//} else {
		//	return false;
		//}
	}

	private boolean frequencyReached(double frequency, double totalCov, double highestCov) {
		double ratio = highestCov / totalCov;
		return frequency < ratio;
	}

	public CoverageFraction toCoverageFraction() {
		String localRef = this.ref.substring(0, 1);
		String localAlt = this.alt.substring(0, 1);
		double fraction = 0.0;
		String[] samplesSplit = this.sampleIds.split(":");
		String[] aDSplit = samplesSplit[1].split(",");
		double total = 0.0;
		double aim = 0.0;
		for (int i = 0; i < aDSplit.length; i++) {
			total += Double.parseDouble(aDSplit[i]);
			if (i == 1) {
				aim = Double.parseDouble(aDSplit[i]);
			}
		}
		fraction = aim / total;
		return new CoverageFraction(this.chrom, Integer.parseInt(this.pos), localRef, localAlt, fraction*100);
	}

	// TODO hashFunction

}
