package vcf.analyse;

import java.util.ArrayList;
import java.util.List;

import vcf.CoverageFraction;
import vcf.VcfEntry;
import vcf.analyse.window.CoverageWindow;
import vcf.analyse.window.NumSnpsFacWindow;

public class VcfEntryPreAnalyser {

	// the vcf entries to preanalyse
	private List<VcfEntry> vcfEntries;

	// the SNP factor window parameters
	private String[] snpFacWindowParameters;

	// the coverage window parameters
	private String[] coverageWindowParameters;
	
	// the heterozygous window parameters
	private String[] heterozygousWindowParameters;
	
	// the resulting coverage fractions
	private List<CoverageFraction> coverageFractions;

	private double meanCoveragePerWindow;

	private double meanNumSnpsPerWindow;
	
	private double numCoverageWindows;
	
	private double numSnpFacWindows;

	public VcfEntryPreAnalyser(List<VcfEntry> vcfEntries, String[] snpFacWindowParameters,
			String[] coverageWindowParameters, String[] heterozygousWindowParameters) {
		this.vcfEntries = vcfEntries;
		this.snpFacWindowParameters = snpFacWindowParameters;
		this.coverageWindowParameters = coverageWindowParameters;
		this.heterozygousWindowParameters = heterozygousWindowParameters;
		this.meanCoveragePerWindow = 0.0;
		this.meanNumSnpsPerWindow = 0.0;
		this.numCoverageWindows = 0;
		this.numSnpFacWindows = 0;
		this.coverageFractions = new ArrayList<>();
	}

	public void preAnalyseVcfEntries() {
		System.out.println("Preanalysing VCF entries...");
		// calculate the coverage fractions
		System.out.println("Calculating coverage fractions...");
		calculateCoverageFractions();
		if (this.snpFacWindowParameters != null) {
			System.out.println("Calculating mean number of SNPs in windows...");
			preAnalyseFacWindows();
		}
		if (this.coverageWindowParameters != null) {
			System.out.println("Calculating mean coverage in windows...");
			preAnalyseCoverageWindows();
		}
		this.meanCoveragePerWindow = this.meanCoveragePerWindow / 
				((double) this.numCoverageWindows);
		this.meanNumSnpsPerWindow = this.meanNumSnpsPerWindow / 
				((double) this.numSnpFacWindows);
	}

	private void calculateCoverageFractions() {
		for (VcfEntry vEntry : this.vcfEntries) {
			if (!vEntry.isEmpty()) {
				if (vEntry.hasSnp()) {
					this.coverageFractions.add(vEntry.toCoverageFraction());
				}
			}
		}
		
	}

	private void preAnalyseCoverageWindows() {
		int windowSize = Integer.parseInt(this.coverageWindowParameters[1]);
		int windowShift = Integer.parseInt(this.coverageWindowParameters[2]);
		int start = 1;
		int end = start + windowSize - 1;
		int contigLen = this.vcfEntries.size();
		while (true) {
			if (start < contigLen && end < contigLen) {
				CoverageWindow coverageWindow = new CoverageWindow(start, end, windowSize, vcfEntries);
				int coverageWindowTotalCoverage = coverageWindow.getTotalCoverage();
				this.meanCoveragePerWindow += coverageWindowTotalCoverage;
				start += windowShift;
				end = start + windowSize + 1;
				this.numCoverageWindows++;
			} else {
				break;
			}
		}
	}

	private void preAnalyseFacWindows() {
		int windowSize = Integer.parseInt(this.snpFacWindowParameters[1]);
		int windowShift = Integer.parseInt(this.snpFacWindowParameters[2]);
		double frequency = Double.parseDouble(this.heterozygousWindowParameters[3]);
		double punishmentRatio = Double.parseDouble(this.heterozygousWindowParameters[4]);
		int start = 1;
		int end = start + windowSize - 1;
		int contigLen = this.vcfEntries.size();
		while (true) {
			if (start < contigLen && end < contigLen) {
				NumSnpsFacWindow numSnpsFacWindow = new NumSnpsFacWindow(start, end, windowSize, vcfEntries);
				int numSnpsFacWindowTotalNuMSnps = numSnpsFacWindow.getTotalNumSnpsNotHeterozygous(frequency, punishmentRatio);
				this.meanNumSnpsPerWindow += numSnpsFacWindowTotalNuMSnps;
				start += windowShift;
				end = start + windowSize + 1;
				this.numSnpFacWindows++;
			} else {
				break;
			}
		}
	}

	public List<VcfEntry> getVcfEntries() {
		return vcfEntries;
	}

	public void setVcfEntries(List<VcfEntry> vcfEntries) {
		this.vcfEntries = vcfEntries;
	}

	public String[] getSnpFacWindowParameters() {
		return snpFacWindowParameters;
	}

	public void setSnpFacWindowParameters(String[] snpFacWindowParameters) {
		this.snpFacWindowParameters = snpFacWindowParameters;
	}

	public String[] getCoverageWindowParameters() {
		return coverageWindowParameters;
	}

	public void setCoverageWindowParameters(String[] coverageWindowParameters) {
		this.coverageWindowParameters = coverageWindowParameters;
	}

	public double getMeanCoveragePerWindow() {
		return meanCoveragePerWindow;
	}

	public void setMeanCoveragePerWindow(double meanCoveragePerWindow) {
		this.meanCoveragePerWindow = meanCoveragePerWindow;
	}

	public double getMeanNumSnpsPerWindow() {
		return meanNumSnpsPerWindow;
	}

	public void setMeanNumSnpsPerWindow(double meanNumSnpsPerWindow) {
		this.meanNumSnpsPerWindow = meanNumSnpsPerWindow;
	}

	public List<CoverageFraction> getCoverageFractions() {
		return coverageFractions;
	}

	public void setCoverageFractions(List<CoverageFraction> coverageFractions) {
		this.coverageFractions = coverageFractions;
	}

}
