package vcf.analyse.window;

import java.util.List;

import vcf.VcfEntry;
import vcf.analyse.Region;

public class NumSnpsAbsWindow extends AFilterWindow {

	public NumSnpsAbsWindow(int start, int end, int windowLen, List<VcfEntry> vcfEntryArray) {
		super(start, end, windowLen, vcfEntryArray);
	}

	@Override
	public boolean isProblematic(double[] aimingValues) {
		/**
		 * deprecated...
		// the heterozygous frequency
		double frequency = aimingValues[1];
		// the heterozygous punishment factor
		double punishmentRatio = aimingValues[2]; **/
		if (this.getTotalNumAbsSnps() > aimingValues[0]) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getTotalNumAbsSnps() {
		int totalNumAbsSnps = 0;
		for (int i = this.getStart() - 1; i < this.getEnd(); i++) {
			VcfEntry vcfEntry = this.getVcfEntryArray().get(i);
			if (!vcfEntry.isEmpty()) {
				if (vcfEntry.hasSnp()) {
					totalNumAbsSnps++;
				}
			}
		}
		return totalNumAbsSnps;
	}
	
	public int getTotalNumAbsSnpsNotHeterozygous(double frequency, double punishmentRatio) {
		int totalNumAbsSnps = 0;
		for (int i = this.getStart() - 1; i < this.getEnd(); i++) {
			VcfEntry vcfEntry = this.getVcfEntryArray().get(i);
			if (!vcfEntry.isEmpty()) {
				if (vcfEntry.hasSnpNotHeterozygous(frequency, punishmentRatio)) {
					totalNumAbsSnps++;
				}
			}
		}
		return totalNumAbsSnps;
	}
	
	@Override
	public String getReason() {
		return Region.ABSOLUT;
	}

}
