package vcf.analyse;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import feature_format.gff.GffThreeEntry;

public class Region {

	public static final String UNCOVERED = "UNCOVERED_POS";
	public static final String ABSOLUT = "SNP_ABSOLUTE";
	public static final String FACTOR = "SNP_FACTOR";
	public static final String HETEROZYGOUS = "HETEROZYGOUS";
	public static final String COVERAGE = "COVERAGE";
	
	// the start base position of the region
	private int start;
	
	// the end base position of the region
	private int end;
	
	// the list of reasons, why this region is seen as problematic
	private List<String> reasons;
	
	// the sequence identifier of the region
	private String seqId;
	
	public Region(int start, int end, String seqId) {
		this.start = start;
		this.end = end;
		this.seqId = seqId;
		this.reasons = new ArrayList<>();
	}

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

	public List<String> getReasons() {
		return reasons;
	}

	public void setReasons(List<String> reasons) {
		this.reasons = reasons;
	}
	
	public void addReason(String reason) {
		this.reasons.add(reason);
	}
	
	public GffThreeEntry toGff3Entry() {
		SortedMap<String, String> attributes = new TreeMap<>();
		StringBuilder valueBuilder = new StringBuilder();
		String dash = "_";
		valueBuilder.append(this.start);
		valueBuilder.append(dash);
		valueBuilder.append(this.end);
		for (String reason: this.reasons) {
			valueBuilder.append(dash);
			valueBuilder.append(reason);
		}
		String value = valueBuilder.toString();
		attributes.put("ID", value);
		attributes.put("Name", value);
		return new GffThreeEntry(this.seqId, "TOPAS.AnalyseVcf", "region", this.start, this.end, ".", "+", ".", attributes);
	}

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}

	public int getSize() {
		return this.end - this.start + 1;
	}

}
