package vcf.analyse.window;

import java.util.List;

import vcf.VcfEntry;
import vcf.analyse.Region;

public class NumUncoveredPosWindow extends AFilterWindow {
	
	public NumUncoveredPosWindow(int start, int end, int windowLen, List<VcfEntry> vcfEntryArray) {
		super(start, end, windowLen, vcfEntryArray);
	}
	
	@Override
	public boolean isProblematic(double[] aimingValues) {
		double percent = aimingValues[0];
		double localPercent = 100 * ((double) this.getTotalNumUncovordedPos() / (double) this.getWindowLen());
		if (localPercent > percent) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getTotalNumUncovordedPos() {
		int totalUncoveredPos = 0;
		for (int i = this.getStart() - 1; i < this.getEnd(); i++) {
			VcfEntry vcfEntry = this.getVcfEntryArray().get(i);
			if (!vcfEntry.isEmpty()) {
				if (vcfEntry.getCoverage() == 0) {
					totalUncoveredPos++;
				}
			}
		}
		return totalUncoveredPos;
	}
	
	@Override
	public String getReason() {
		return Region.UNCOVERED;
	}

}
