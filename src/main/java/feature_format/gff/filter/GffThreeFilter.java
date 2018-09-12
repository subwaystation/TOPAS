package feature_format.gff.filter;

import feature_format.gff.GffThreeEntry;
import feature_format.gff.GffThreeParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GffThreeFilter {
	
	private String inputFile;
	private List<GffThreeEntry> featureList;
	
	private List<Integer> linesToFilter;

	private List<String> seqidRangesToFilter;
	private List<String> sourcesToFilter;
	private List<String> typesToFilter;
	private List<String> scoresToFilter;
	private List<String> strandsToFilter;
	private List<String> phasesToFilter;
	private List<String> attributesToFilter;
	
	/**
	 * @param inputFile
	 * @param linesToFilter
	 * @param seqidRangesToFilter
	 * @param sourcesToFilter
	 * @param typesToFilter
	 * @param scoresToFilter
	 * @param strandsToFilter
	 * @param phasesToFilter
	 * @param attributesToFilter
	 */
	public GffThreeFilter(String inputFile,
			List<Integer> linesToFilter, List<String> seqidRangesToFilter, List<String> sourcesToFilter,
			List<String> typesToFilter, List<String> scoresToFilter,
			List<String> strandsToFilter, List<String> phasesToFilter,
			List<String> attributesToFilter) {
		this.inputFile = inputFile;
		this.featureList = new ArrayList<GffThreeEntry>();
		this.linesToFilter = linesToFilter;
		this.seqidRangesToFilter = seqidRangesToFilter;
		this.sourcesToFilter = sourcesToFilter;
		this.typesToFilter = typesToFilter;
		this.scoresToFilter = scoresToFilter;
		this.strandsToFilter = strandsToFilter;
		this.phasesToFilter = phasesToFilter;
		this.attributesToFilter = attributesToFilter;
	}
	
	public GffThreeFilter(String inputFile, List<Integer> linesToFilter) {
		this(inputFile, linesToFilter, null, null, null, null, null, null, null);
	}
	
	public GffThreeFilter(String inputFile,
			List<String> seqidRangesToFilter, List<String> sourcesToFilter,
			List<String> typesToFilter, List<String> scoresToFilter,
			List<String> strandsToFilter, List<String> phasesToFilter,
			List<String> attributesToFilter) {
		this(inputFile, null, seqidRangesToFilter, sourcesToFilter,
				typesToFilter, scoresToFilter, strandsToFilter, phasesToFilter, attributesToFilter);
	}
	
	public String getInputFile() {
		return inputFile;
	}
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	public List<GffThreeEntry> getFeatureList() {
		return featureList;
	}
	public void setFeatureList(List<GffThreeEntry> featureList) {
		this.featureList = featureList;
	}
	public List<Integer> getLinesToFilter() {
		return linesToFilter;
	}
	public void setLinesToFilter(List<Integer> linesToFilter) {
		this.linesToFilter = linesToFilter;
	}
	public List<String> getSourcesToFilter() {
		return sourcesToFilter;
	}
	public void setSourcesToFilter(List<String> sourcesToFilter) {
		this.sourcesToFilter = sourcesToFilter;
	}
	public List<String> getTypesToFilter() {
		return typesToFilter;
	}
	public void setTypesToFilter(List<String> typesToFilter) {
		this.typesToFilter = typesToFilter;
	}
	public List<String> getScoresToFilter() {
		return scoresToFilter;
	}
	public void setScoresToFilter(List<String> scoresToFilter) {
		this.scoresToFilter = scoresToFilter;
	}
	public List<String> getStrandsToFilter() {
		return strandsToFilter;
	}
	public void setStrandsToFilter(List<String> strandsToFilter) {
		this.strandsToFilter = strandsToFilter;
	}
	public List<String> getPhasesToFilter() {
		return phasesToFilter;
	}
	public void setPhasesToFilter(List<String> phasesToFilter) {
		this.phasesToFilter = phasesToFilter;
	}
	public List<String> getAttributesToFilter() {
		return attributesToFilter;
	}
	public void setAttributesToFilter(List<String> attributesToFilter) {
		this.attributesToFilter = attributesToFilter;
	}
	
	public List<GffThreeEntry> getFilteredLines() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(this.inputFile));
		GffThreeParser gTP = new GffThreeParser();
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("#")) {
				boolean match = false;
				match = filterLine(line);
				if (match) {
					gTP.setLine(line);
					this.featureList.add((GffThreeEntry) gTP.scanLine());
				}
			}
			
		}
		br.close();
		return this.featureList;
	}
	
	private boolean filterLine(String line) {
		boolean passSeqIdRange = false;
		boolean passSource = false;
		boolean passType = false;
		boolean passScore = false;
		boolean passStrand = false;
		boolean passPhase = false;
		boolean passAttributes = false;
		
		GffThreeParser gTP = new GffThreeParser(line);
		GffThreeEntry gTE = (GffThreeEntry) gTP.scanLine();
		
		SeqIdRangeGffThreeFilter seqIdRangeFilter = new SeqIdRangeGffThreeFilter(gTE);
		passSeqIdRange = seqIdRangeFilter.filter(this.seqidRangesToFilter);
		
		SourceGffThreeFilter sourceFilter = new SourceGffThreeFilter(gTE);
		passSource = sourceFilter.filter(this.sourcesToFilter);
		
		TypeGffThreeFilter typeFilter = new TypeGffThreeFilter(gTE);
		passType = typeFilter.filter(this.typesToFilter);
		
		ScoreGffThreeFilter scoreFilter = new ScoreGffThreeFilter(gTE);
		passScore = scoreFilter.filter(this.scoresToFilter);
		
		StrandGffThreeFilter strandFilter = new StrandGffThreeFilter(gTE);
		passStrand = strandFilter.filter(this.strandsToFilter);
		
		PhaseGffThreeFilter phaseFilter = new PhaseGffThreeFilter(gTE);
		passPhase = phaseFilter.filter(this.phasesToFilter);
		
		AttributesGffThreeFilter attributeFilter = new AttributesGffThreeFilter(gTE);
		passAttributes = attributeFilter.filter(this.attributesToFilter);
		
		return passSeqIdRange&&passScore&&passSource&&passStrand&&passPhase&&passType&&passAttributes;
	}
	
	public List<GffThreeEntry> findLines() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(this.inputFile));
		GffThreeParser gTP = new GffThreeParser();
		String line;
		int numberLines = 0;
		int processedLines = 0;
		while ((line = br.readLine()) != null) {
			numberLines++;
			if (line.startsWith("#")&&this.linesToFilter.contains(numberLines)) {
				processedLines++;
				System.out.println("[WARNING] Line " + numberLines + " to map is not a gff3 entry in the gff file. Line " + numberLines + " ignored.");
			}
			if (this.linesToFilter.contains(numberLines)&&!(line.startsWith("#"))) {
				processedLines++;
				gTP.setLine(line);
				this.featureList.add((GffThreeEntry) gTP.scanLine());
				if (processedLines == this.linesToFilter.size()) {
					break;
				}
			} else {
				continue;
			}			
		}
		br.close();
		return this.featureList;
	}

}
