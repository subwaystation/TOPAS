package vcf.analyse.window;

import java.util.List;

import vcf.VcfEntry;
import vcf.analyse.Region;

public class CoverageWindow extends AFilterWindow {
	
	// the total coverage of the window
	private int totalCov;
	
	public CoverageWindow(int start, int end, int windowLen, List<VcfEntry> vcfEntryArray) {
		super(start, end, windowLen, vcfEntryArray);
		this.totalCov = 0;
	}

	@Override
	public boolean isProblematic(double[] aimingValues) {
		double factor = aimingValues[0];
		double meanTotalCov = aimingValues[1];
		double upperBound = factor * meanTotalCov;
		if (this.getTotalCoverage() > upperBound) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getTotalCoverage() {
		for (int i = this.getStart() - 1; i < this.getEnd(); i++) {
			VcfEntry vcfEntry = this.getVcfEntryArray().get(i);
			if (!vcfEntry.isEmpty()) {
				this.totalCov += vcfEntry.getCoverage();
			}
		}
		return this.totalCov;
	}
	
	@Override
	public String getReason() {
		return Region.COVERAGE;
	}

}
