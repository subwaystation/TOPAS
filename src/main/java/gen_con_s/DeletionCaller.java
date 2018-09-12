package gen_con_s;

import java.util.ArrayList;
import java.util.TreeMap;

import vcf.VcfEntry;


public class DeletionCaller {

	private static final String NO = "no";
	private static final String DEL = "del";
	private static final String SNC = "snc";

	// the deletion as a full VcfEntry itself
	private VcfEntry deletion;
//	private int deletionBaseIndex;
	// the ratio at which a consensus is reached
	private double consensusRatio;

	// the snps that are covered by the deletion will be stored here
	private ArrayList<VcfEntry> snps;
	// resulting consensus bases
	private TreeMap<Integer, VcfEntry> consensusEntries;
	// the deletion coverage
	private double deletionCoverage;
	// the length of the deletion base(s)
	private int deletionLen;
	// the deletion base(s)
	private String deletionBases;
	// the total snp coverage
	private double totalSnpCoverage;
	// did a call occur?
	private String callType;
	// the actual obtained ratio after all relevant SNPs were parsed
	private double ratio;

	public DeletionCaller(VcfEntry deletion, int deletionBaseIndex, double consensusRatio) {
		this.deletion = deletion;
//		this.deletionBaseIndex = deletionBaseIndex;
		this.consensusRatio = consensusRatio;
		this.snps = new ArrayList<VcfEntry>();
		this.consensusEntries = new TreeMap<Integer, VcfEntry>();
		this.totalSnpCoverage = 0.0;
		this.callType = NO;
		this.ratio = 0.0;
	}

	private void calcDelCovLenBases() {
		String[] sampleSplit = this.deletion.getSampleIds().split(",");
		this.deletionCoverage = Double.parseDouble(sampleSplit[1]);
		this.deletionBases = this.deletion.getRef();
		this.deletionLen = this.deletionBases.length();
	}

	public void addSnp(VcfEntry snp) {
		this.snps.add(snp);
		String[] sampleSplit = snp.getSampleIds().split(",");
		for (String cov : sampleSplit) {
			this.totalSnpCoverage += Double.parseDouble(cov);
		}
	}

	public void finishDel() {
		calcDelCovLenBases();
		performCall();
		calcOutput();
	}
	
//	public void setDelEntry(VcfEntry deletion, int deletionBaseIndex) {
//		this.deletion = deletion;
//		this.deletionBaseIndex = deletionBaseIndex;
//	}
	
	private void calcOutput() {
		if (this.callType.equals(NO)) {
			// no output required, all will be 'N'
		} else if (this.callType.equals(DEL)) {
			// only the consensus base as output is required, nothing to do here

			// we will need all snps as output
		} else {
			for (VcfEntry vE : this.snps) {
				if (!vE.isEmpty()) {
					Integer pos = Integer.parseInt(vE.getPos());
					this.consensusEntries.put(pos, vE);
				}
			}
		}

	}

	private void performCall() {
		double totalCov = this.totalSnpCoverage + this.deletionCoverage;
		ratio = this.deletionCoverage / totalCov;
		// call on deletion
		if (ratio >= this.consensusRatio) {
			this.callType = DEL;
			// call on snps
		} else {
			if ((1 - ratio) >= this.consensusRatio) {
				this.callType = SNC;
			} else {
				// no call on snps or on deletion
				this.callType = NO;
			}
		}
	}

	public void reset() {
		this.callType = NO;
		this.snps.clear();
		this.consensusEntries.clear();
		this.totalSnpCoverage = 0.0;
		this.deletion = new VcfEntry();
		this.ratio = 0.0;
	}

	public VcfEntry getDeletion() {
		return deletion;
	}

	public void setDeletion(VcfEntry deletion) {
		this.deletion = deletion;
	}

	public double getConsensusRatio() {
		return consensusRatio;
	}

	public void setConsensusRatio(double consensusRatio) {
		this.consensusRatio = consensusRatio;
	}

	public ArrayList<VcfEntry> getSnps() {
		return snps;
	}

	public void setSnps(ArrayList<VcfEntry> snps) {
		this.snps = snps;
	}

	public TreeMap<Integer, VcfEntry> getConsensusEntries() {
		return consensusEntries;
	}

	public void setConsensusEntries(TreeMap<Integer, VcfEntry> consensusEntries) {
		this.consensusEntries = consensusEntries;
	}

	public double getDeletionCoverage() {
		return deletionCoverage;
	}

	public void setDeletionCoverage(double deletionCoverage) {
		this.deletionCoverage = deletionCoverage;
	}

	public int getDeletionLen() {
		return deletionLen;
	}

	public void setDeletionLen(int deletionLen) {
		this.deletionLen = deletionLen;
	}

	public String getDeletionBases() {
		return deletionBases;
	}

	public void setDeletionBases(String deletionBases) {
		this.deletionBases = deletionBases;
	}

	public double getTotalSnpCoverage() {
		return totalSnpCoverage;
	}

	public void setTotalSnpCoverage(double totalSnpCoverage) {
		this.totalSnpCoverage = totalSnpCoverage;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public static String getNo() {
		return NO;
	}

	public static String getDel() {
		return DEL;
	}

	public static String getSnp() {
		return SNC;
	}
	
	public double getRatio() {
		return this.ratio;
	}

}
