package vcf.analyse.window;

import java.util.List;

import vcf.VcfEntry;
import vcf.analyse.Region;

public class NumHetCallsWindow extends AFilterWindow {

	public NumHetCallsWindow(int start, int end, int windowLen, List<VcfEntry> vcfEntryArray) {
		super(start, end, windowLen, vcfEntryArray);
	}

	@Override
	public boolean isProblematic(double[] aimingValues) {
		// first value is the absolute number of allowed heterozygous calls in the window
		// the second value is the major allele frequency from which a call is seen as homozygous
		// the third value is the punishment ratio
		double punishmentRatio = aimingValues[2];
		if (this.getTotalNumHetCalls(aimingValues[1], punishmentRatio) > aimingValues[0]) {
			return true;
		} else {
			return false;
		}
	}

	public int getTotalNumHetCalls(double frequency, double punishmentRatio) {
		int totalNumHetCalls = 0;
		for (int i = this.getStart() - 1; i < this.getEnd(); i++) {
			VcfEntry vcfEntry = this.getVcfEntryArray().get(i);
			if (!vcfEntry.isEmpty()) {
				if (vcfEntry.isHeterozygous(frequency, punishmentRatio)) {
					totalNumHetCalls++;
				}
			}
		}
		return totalNumHetCalls;
	}
	
	@Override
	public String getReason() {
		return Region.HETEROZYGOUS;
	}
}
