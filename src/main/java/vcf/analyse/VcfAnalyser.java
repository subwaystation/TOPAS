package vcf.analyse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vcf.VcfEntry;
import vcf.analyse.window.CoverageWindow;
import vcf.analyse.window.NumHetCallsWindow;
import vcf.analyse.window.NumSnpsAbsWindow;
import vcf.analyse.window.NumSnpsFacWindow;
import vcf.analyse.window.NumUncoveredPosWindow;

public class VcfAnalyser {
	
	// the given list of vcf entries
	private List<VcfEntry> vcfEntries;
	
	// the coverage window parameter
	private String[] coverageWindowParameters;
	
	// the SNP absolute window parameter
	private String[] snpAbsWindowParameters;
	
	// the SNP factor window parameter
	private String[] snpFacWindowParameters;
	
	// the heterozygous window parameter
	private String[] heterozygousWindowParameters;
	
	// uncovered positions parameters
	private String[] uncoveredPosParameters;
	
	// the resulting coverage window list
	private List<CoverageWindow> coverageWindows;
	
	// the resulting absolute window list
	private List<NumSnpsAbsWindow> absWindows;
	
	// the resulting factor window list
	private List<NumSnpsFacWindow> facWindows;
	
	// the resulting heterozygous window list
	private List<NumHetCallsWindow> heterozygousWindows;
	
	// the resulting uncovered window list
	private List<NumUncoveredPosWindow> uncoveredWindows;
	
	// the precalculated mean coverage per window
	private double meanCoveragePerWindow;

	// the precalculated mean number of SNPs per window
	private double meanNumSnpsPerWindow;
	
	// the total number of windows, given by
	// 1) coverage
	// 2) abs
	// 3) fac
	// 4) heterozygous
	// 5) uncovered
	private int[] totalNumWindows; 
	
	public VcfAnalyser(List<VcfEntry> vcfEntries) {
		this.coverageWindows = new ArrayList<>();
		this.absWindows = new ArrayList<>();
		this.facWindows = new ArrayList<>();
		this.heterozygousWindows = new ArrayList<>();
		this.uncoveredWindows = new ArrayList<>();
		this.totalNumWindows = new int[5];
		this.vcfEntries = vcfEntries;
	}
	
	public void analyseVcf() throws IOException {
		analyseCoverageWindows();
		analyseAbsWindows();
		analyseFacWindows();
		analyseHeterozygousWindows();
		analyseUncoveredWindows();
	}

	private void analyseUncoveredWindows() {
		if (this.uncoveredPosParameters != null) {
			System.out.println("Analysing uncovered windows...");
			double percent = Double.parseDouble(this.uncoveredPosParameters[0]);
			int windowSize = Integer.parseInt(this.uncoveredPosParameters[1]);
			int windowShift = Integer.parseInt(this.uncoveredPosParameters[2]);
			int start = 1;
			int end = start + windowSize - 1;
			int contigLen = this.vcfEntries.size();
			double[] aimingValues = new double[] {percent};
			while (true) {
				if (start < contigLen && end < contigLen) {
					NumUncoveredPosWindow uncoveredWindow = new NumUncoveredPosWindow(start, end, windowSize, vcfEntries);
					if (uncoveredWindow.isProblematic(aimingValues)) {
						this.uncoveredWindows.add(uncoveredWindow);
					}
					start += windowShift;
					end += windowShift;
					this.totalNumWindows[4]++;
				} else {
					break;
				}
			}				
		}
	}
	
	private void analyseHeterozygousWindows() {
		if (this.heterozygousWindowParameters != null) {
			System.out.println("Analysing heterozygous windows...");
			int absVal = Integer.parseInt(this.heterozygousWindowParameters[0]);
			int windowSize = Integer.parseInt(this.heterozygousWindowParameters[1]);
			int windowShift = Integer.parseInt(this.heterozygousWindowParameters[2]);
			double frequency = Double.parseDouble(this.heterozygousWindowParameters[3]);
			double punishmentRatio = Double.parseDouble(this.heterozygousWindowParameters[4]);
			int start = 1;
			int end = start + windowSize - 1;
			int contigLen = this.vcfEntries.size();
			double[] aimingValues = new double[] {absVal, frequency, punishmentRatio};
			while (true) {
				if (start < contigLen && end < contigLen) {
					NumHetCallsWindow hetWindow = new NumHetCallsWindow(start, end, windowSize, vcfEntries);
					if (hetWindow.isProblematic(aimingValues)) {
						this.heterozygousWindows.add(hetWindow);
					}
					start += windowShift;
					end += windowShift;
					this.totalNumWindows[3]++;
				} else {
					break;
				}
			}		
		}
	}

	private void analyseFacWindows() {
		if (this.snpFacWindowParameters != null) {
			System.out.println("Analysing factor windows...");
			double factor = Double.parseDouble(this.snpFacWindowParameters[0]);
			int windowSize = Integer.parseInt(this.snpFacWindowParameters[1]);
			int windowShift = Integer.parseInt(this.snpFacWindowParameters[2]);
			double frequency = Double.parseDouble(this.heterozygousWindowParameters[3]);
			double punishmentRatio = Double.parseDouble(this.heterozygousWindowParameters[4]);
			int start = 1;
			int end = start + windowSize - 1;
			int contigLen = this.vcfEntries.size();
			double meanNumSnps = this.meanNumSnpsPerWindow;
			double upperBound = factor * meanNumSnps;
			double[] aimingValues = new double[] {upperBound, frequency, punishmentRatio};
			while (true) {
				if (start < contigLen && end < contigLen) {
					NumSnpsFacWindow facWindow = new NumSnpsFacWindow(start, end, windowSize, vcfEntries);
					if (facWindow.isProblematic(aimingValues)) {
						this.facWindows.add(facWindow);
					}
					start += windowShift;
					end += windowShift;
					this.totalNumWindows[2]++;
				} else {
					break;
				}
			}		
		}
	}

	private void analyseAbsWindows() {
		if (this.snpAbsWindowParameters != null) {
			System.out.println("Analysing absolute windows...");
			int absVal = Integer.parseInt(this.snpAbsWindowParameters[0]);
			int windowSize = Integer.parseInt(this.snpAbsWindowParameters[1]);
			int windowShift = Integer.parseInt(this.snpAbsWindowParameters[2]);
			double frequency = Double.parseDouble(this.heterozygousWindowParameters[3]);
			double punishmentRatio = Double.parseDouble(this.heterozygousWindowParameters[4]);
			int start = 1;
			int end = start + windowSize - 1;
			int contigLen = this.vcfEntries.size();
			double[] aimingValues = new double[] {absVal, frequency, punishmentRatio};
			while (true) {
				if (start < contigLen && end < contigLen) {
					NumSnpsAbsWindow absWindow = new NumSnpsAbsWindow(start, end, windowSize, vcfEntries);
					if (absWindow.isProblematic(aimingValues)) {
						this.absWindows.add(absWindow);
					}
					start += windowShift;
					end += windowShift;
					this.totalNumWindows[1]++;
				} else {
					break;
				}
			}
		}
	}

	private void analyseCoverageWindows() {
		if (this.coverageWindowParameters != null) {
			System.out.println("Analysing coverage windows...");
			double factor = Double.parseDouble(this.coverageWindowParameters[0]);
			int windowSize = Integer.parseInt(this.coverageWindowParameters[1]);
			int windowShift = Integer.parseInt(this.coverageWindowParameters[2]);
			int start = 1;
			int end = start + windowSize - 1;
			int contigLen = this.vcfEntries.size();
			double[] aimingValues = new double[] {factor, this.meanCoveragePerWindow};
			while (true) {
				if (start < contigLen && end < contigLen) {
					CoverageWindow coverageWindow = new CoverageWindow(start, end, windowSize, vcfEntries);
					if (coverageWindow.isProblematic(aimingValues)) {
						this.coverageWindows.add(coverageWindow);
					}
					start += windowShift;
					end += windowShift;
					this.totalNumWindows[0]++;
				} else {
					break;
				}
			}
		}
	}

	public List<VcfEntry> getVcfEntries() {
		return vcfEntries;
	}

	public void setVcfEntries(List<VcfEntry> vcfEntries) {
		this.vcfEntries = vcfEntries;
	}

	public String[] getCoverageWindowParameters() {
		return coverageWindowParameters;
	}

	public void setCoverageWindowParameters(String[] coverageWindowParameters) {
		this.coverageWindowParameters = coverageWindowParameters;
	}

	public String[] getSnpAbsWindowParameters() {
		return snpAbsWindowParameters;
	}

	public void setSnpAbsWindowParameters(String[] snpAbsWindowParameters) {
		this.snpAbsWindowParameters = snpAbsWindowParameters;
	}

	public String[] getSnpFacWindowParameters() {
		return snpFacWindowParameters;
	}

	public void setSnpFacWindowParameters(String[] snpFacWindowParameters) {
		this.snpFacWindowParameters = snpFacWindowParameters;
	}

	public String[] getHeterozygousWindowParameters() {
		return heterozygousWindowParameters;
	}

	public void setHeterozygousWindowParameters(String[] heterozygousWindowParameters) {
		this.heterozygousWindowParameters = heterozygousWindowParameters;
	}

	public String[] getUncoveredPosParameters() {
		return uncoveredPosParameters;
	}

	public void setUncoveredPosParameters(String[] uncoveredPosParameters) {
		this.uncoveredPosParameters = uncoveredPosParameters;
	}

	public List<CoverageWindow> getCoverageWindows() {
		return coverageWindows;
	}

	public void setCoverageWindows(List<CoverageWindow> coverageWindows) {
		this.coverageWindows = coverageWindows;
	}

	public List<NumSnpsAbsWindow> getAbsWindows() {
		return absWindows;
	}

	public void setAbsWindows(List<NumSnpsAbsWindow> absWindows) {
		this.absWindows = absWindows;
	}

	public List<NumSnpsFacWindow> getFacWindows() {
		return facWindows;
	}

	public void setFacWindows(List<NumSnpsFacWindow> facWindows) {
		this.facWindows = facWindows;
	}

	public List<NumHetCallsWindow> getHeterozygousWindows() {
		return heterozygousWindows;
	}

	public void setHeterozygousWindows(List<NumHetCallsWindow> heterozygousWindows) {
		this.heterozygousWindows = heterozygousWindows;
	}

	public List<NumUncoveredPosWindow> getUncoveredWindows() {
		return uncoveredWindows;
	}

	public void setUncoveredWindows(List<NumUncoveredPosWindow> uncoveredWindows) {
		this.uncoveredWindows = uncoveredWindows;
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

	public int[] getTotalNumWindows() {
		return totalNumWindows;
	}

	public void setTotalNumWindows(int[] totalNumWindows) {
		this.totalNumWindows = totalNumWindows;
	}
	
}
