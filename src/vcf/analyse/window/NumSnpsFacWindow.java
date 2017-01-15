package vcf.analyse.window;

import java.util.List;

import vcf.VcfEntry;
import vcf.analyse.Region;

public class NumSnpsFacWindow extends AFilterWindow {
	
	// the total number of SNPs of this window
	private int totalNumSnps;

	public NumSnpsFacWindow(int start, int end, int windowLen, List<VcfEntry> vcfEntryArray) {
		super(start, end, windowLen, vcfEntryArray);
		this.totalNumSnps = 0;
	}

	@Override
	public boolean isProblematic(double[] aimingValues) {
		// the upper bound
		double upperBound = aimingValues[0];
		// the heterozygous factor
		double heterozygousFactor = aimingValues[1];
		// the punishment ratio
		double punishmentRatio = aimingValues[2];
		if (this.getTotalNumSnpsNotHeterozygous(heterozygousFactor, punishmentRatio) > upperBound) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getTotalNumSnps() {
		for (int i = this.getStart() - 1; i < this.getEnd(); i++) {
			VcfEntry vcfEntry = this.getVcfEntryArray().get(i);
			if (!vcfEntry.isEmpty()) {
				if (vcfEntry.hasSnp()) {
					this.totalNumSnps++;
				}
			}
		}
		return this.totalNumSnps;
	}
	
	public int getTotalNumSnpsNotHeterozygous(double frequency, double punishmentRatio) {
		for (int i = this.getStart() - 1; i < this.getEnd(); i++) {
			VcfEntry vcfEntry = this.getVcfEntryArray().get(i);
			if (!vcfEntry.isEmpty()) {
				if (vcfEntry.hasSnpNotHeterozygous(frequency, punishmentRatio)) {
					this.totalNumSnps++;
				}
			}
		}
		return this.totalNumSnps;
	}
	
	@Override
	public String getReason() {
		return Region.FACTOR;
	}

}
