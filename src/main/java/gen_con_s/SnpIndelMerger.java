package gen_con_s;

import java.util.HashMap;
import java.util.Map;

import vcf.VcfEntry;

/**
 * This class provides a method to merge
 * a SNP with an INDEL. Note that this class was
 * specially designed for the consensus sequence tool and
 * thus is not compatible for any 'abstract' merging purpose!
 * Moreover, this class does not provide any verification what
 * so ever.
 * @author heumos
 *
 */
public class SnpIndelMerger {
	
	// the SNP to merge
	private VcfEntry snp;
	// the INDEL to merge
	private VcfEntry inDel;
	
	// the merged VcfEntry
	private VcfEntry mergedVcfEntry;
	
	/**
	 * @param snp
	 * the SNP to merge
	 * @param inDel
	 * the INDEL to merge
	 */
	public SnpIndelMerger(VcfEntry snp, VcfEntry inDel) {
		this.snp = snp;
		this.inDel = inDel;
		this.mergedVcfEntry = new VcfEntry();
	}
	
	public VcfEntry merge() {
		HashMap<String, Integer> refMap = new HashMap<String, Integer>();
		HashMap<String, Integer> altMap = new HashMap<String, Integer>();
		
		StringBuilder mergedRef = new StringBuilder();
		StringBuilder mergedAlt = new StringBuilder();
		StringBuilder mergedSample = new StringBuilder();
		
		// set chrom of merged entry
		this.mergedVcfEntry.setChrom(this.snp.getChrom());
		// set pos of merged entry
		this.mergedVcfEntry.setPos(this.snp.getPos());
		
		double snpQual = this.snp.getQual();
		double inDelQual = this.inDel.getQual();
		// set quality of the merged entry
		this.mergedVcfEntry.setQual((snpQual + inDelQual) / 2);
		// set format of the merged entry
		// will AD only, as only this field is needed for the call by coverage procedure
		this.mergedVcfEntry.setFormat("AD");
		
		String snpFormat = this.snp.getFormat();
		String[] snpFormatSplit = snpFormat.split(":");
		String sequencingDepth = snpFormatSplit[1];
		// do we have a deletion?
		if (this.inDel.isDel()) {
			// collect ref coverage from SNP, but add it to the alt map to merge with possible
			// single base from the deletion
			addRefCoverage(this.inDel, refMap);
			addRefCoverageDel(this.snp, refMap);
		} else {
			addRefCoverage(this.snp, refMap);
			// collect ref coverage from INDEL
			addRefCoverage(this.inDel, refMap);
		}
		// collect alt coverage from INDEL
		addAltCoverage(this.inDel, altMap);
		// sequencing depth is given as 'DP' -> only reference was called
		if (sequencingDepth.equals("DP")) {
			
			// sequencing depth is given as 'AD' -> also alternatives were called
		} else {
			addAltCoverage(this.snp, altMap);
		}
		// add refs and corresponding coverages
		for (Map.Entry<String, Integer> entry: refMap.entrySet()) {
			String base = entry.getKey();
			Integer coverage = entry.getValue();
			mergedRef.append(base);
			mergedRef.append(",");
			mergedSample.append(coverage);
			mergedSample.append(",");
		}
		// add alts and corresponding coverages
		for (Map.Entry<String, Integer> entry: altMap.entrySet()) {
			String base = entry.getKey();
			Integer coverage = entry.getValue();
			mergedAlt.append(base);
			mergedAlt.append(",");
			mergedSample.append(coverage);
			mergedSample.append(",");
		}
		// set the reference of the merged entry
		this.mergedVcfEntry.setRef(mergedRef.substring(0, mergedRef.length() - 1));
		// set the alternatives of the merged entry
		this.mergedVcfEntry.setAlt(mergedAlt.substring(0, mergedAlt.length() - 1));
		// set the sample of the merged entry
		this.mergedVcfEntry.setSampleIds(mergedSample.substring(0, mergedSample.length() - 1));

		return this.mergedVcfEntry;
	}
	
	
	private void addRefCoverageDel(VcfEntry vE, HashMap<String, Integer> refMap) {
		String sample = vE.getSampleIds();
		String seqDepth = sample.split(":")[1].split(",")[0];
		for (Map.Entry<String, Integer> entry : refMap.entrySet()) {
		    String base = entry.getKey();
		    Integer seqDepth_ = entry.getValue();
		    seqDepth_ += Integer.valueOf(seqDepth);
		    refMap.put(base, seqDepth_);
		}
	}
	
	private void addRefCoverage(VcfEntry vE, HashMap<String, Integer> refMap) {
		String ref = vE.getRef();
		String sample = vE.getSampleIds();
		String[] refSplit = ref.split(",");
		String[] seqDepthSplit = sample.split(":")[1].split(",");
		for (int i = 0; i < refSplit.length; i++) {
			addBaseCoverage(refSplit[i], Integer.parseInt(seqDepthSplit[i]), refMap);
		}
	}
	
	private void addBaseCoverage(String base, Integer coverage, HashMap<String, Integer> map) {
		if (map.containsKey(base)) {
			Integer cov = map.get(base);
			cov += coverage;
			map.put(base, cov);
		} else {
			map.put(base, coverage);
		}
	}

	private void addAltCoverage(VcfEntry vE, HashMap<String, Integer> altMap) {
		String ref = vE.getRef();
		int refSplitLen = ref.split(",").length;
		String alt = vE.getAlt();
		String sample = vE.getSampleIds();
		String[] altSplit = alt.split(",");
		String[] seqDepthSplit = sample.split(":")[1].split(",");
		for (int i = 0; i < altSplit.length; i++) {
			addBaseCoverage(altSplit[i], Integer.parseInt(seqDepthSplit[i + refSplitLen]), altMap);
		}
	}

	public VcfEntry getSnp() {
		return snp;
	}

	public void setSnp(VcfEntry snp) {
		this.snp = snp;
	}

	public VcfEntry getInDel() {
		return inDel;
	}

	public void setInDel(VcfEntry inDel) {
		this.inDel = inDel;
	}

	public VcfEntry getMergedVcfEntry() {
		return mergedVcfEntry;
	}

	public void setMergedVcfEntry(VcfEntry mergedVcfEntry) {
		this.mergedVcfEntry = mergedVcfEntry;
	}

}
