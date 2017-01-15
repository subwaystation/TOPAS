package vcf.analyse.window;

import java.util.List;

import vcf.VcfEntry;

public abstract class AFilterWindow {
	
	// the start base position of the window, inclusively
	private int start;
	
	// the end base position of the window, inclusively
	private int end;
	
	// the length of the window
	private int windowLen;
	
	// the list of vcf entries given as an reference to the array
	private List<VcfEntry> vcfEntries;
	
	public AFilterWindow(int start, int end, int windowLen, List<VcfEntry> vcfEntries) {
		this.start = start;
		this.end = end;
		this.windowLen = windowLen;
		this.vcfEntries = vcfEntries;
	}
	
	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		String tab = "\t";
		sB.append(this.start).append(tab);
		sB.append(this.end).append(tab);
		return sB.toString();
	}
	
	public abstract boolean isProblematic(double[] aimingValues);
	
	public abstract String getReason();

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getWindowLen() {
		return windowLen;
	}

	public void setWindowLen(int windowLen) {
		this.windowLen = windowLen;
	}

	public List<VcfEntry> getVcfEntryArray() {
		return this.vcfEntries;
	}

	public void setVcfEntryArray(List<VcfEntry> vcfEntryArray) {
		this.vcfEntries = vcfEntryArray;
	}
	
	

}
