package feature_format.gff.filter;

import feature_format.gff.GffThreeEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GffThreeFinder {
	
	private List<GffThreeEntry> gTEList;
	private List<GffThreeEntry> foundGTEList;
	
	public GffThreeFinder(List<GffThreeEntry> gTEList) {
		this.gTEList = gTEList;
		this.foundGTEList = new ArrayList<GffThreeEntry>();
	}
	
	public List<GffThreeEntry> getgTEList() {
		return gTEList;
	}

	public void setgTEList(List<GffThreeEntry> gTEList) {
		this.gTEList = gTEList;
	}

	public List<GffThreeEntry> getFoundGTEList() {
		return foundGTEList;
	}

	public void setFoundGTEList(List<GffThreeEntry> foundGTEList) {
		this.foundGTEList = foundGTEList;
	}

	public List<GffThreeEntry> getFoundLines(List<String> seqidRangesToFilter, List<String> typesToFilter) throws IOException {
		for (GffThreeEntry gTE : this.gTEList) {
			boolean match = false;
			match = findLine(gTE, seqidRangesToFilter, typesToFilter);
			if (match) {
				this.foundGTEList.add(gTE);
			}
		}		
		return this.foundGTEList;
	}
	
	public List<GffThreeEntry> getFoundLines(int pos, List<String> typesToFilter) throws IOException {
		for (GffThreeEntry gTE : this.gTEList) {
			boolean match = false;
			match = findLine(gTE, pos, typesToFilter);
			if (match) {
				this.foundGTEList.add(gTE);
			}
		}		
		return this.foundGTEList;
	}
	
	public List<GffThreeEntry> getFoundLines(int pos) throws IOException {
		for (GffThreeEntry gTE : this.gTEList) {
			boolean match = false;
			match = findLine(gTE, pos);
			if (match) {
				this.foundGTEList.add(gTE);
			}
		}		
		return this.foundGTEList;
	}
	
	private boolean findLine(GffThreeEntry gTE, int pos) {
		boolean passPos = false;
//		boolean passSeqIdRange = false;
//		boolean passSource = false;
//		boolean passType = false;
//		boolean passScore = false;
//		boolean passStrand = false;
//		boolean passPhase = false;
//		boolean passAttributes = false;
		
		
//		SeqIdRangeGffThreeFinder seqIdRangeFinder = new SeqIdRangeGffThreeFinder(gTE);
//		passSeqIdRange = seqIdRangeFinder.filter(seqidRangesToFilter);
		
		RangeGffThreeFinder posFinder = new RangeGffThreeFinder(gTE);
		passPos = posFinder.filter(pos);
		
//		SourceGffThreeFilter sourceFilter = new SourceGffThreeFilter(gTE);
//		passSource = sourceFilter.filter(this.sourcesToFilter);
//		
//		TypeGffThreeFilter typeFilter = new TypeGffThreeFilter(gTE);
//		passType = typeFilter.filter(typesToFilter);
//		
//		ScoreGffThreeFilter scoreFilter = new ScoreGffThreeFilter(gTE);
//		passScore = scoreFilter.filter(this.scoresToFilter);
//		
//		StrandGffThreeFilter strandFilter = new StrandGffThreeFilter(gTE);
//		passStrand = strandFilter.filter(this.strandsToFilter);
//		
//		PhaseGffThreeFilter phaseFilter = new PhaseGffThreeFilter(gTE);
//		passPhase = phaseFilter.filter(this.phasesToFilter);
//		
//		AttributesGffThreeFilter attributeFilter = new AttributesGffThreeFilter(gTE);
//		passAttributes = attributeFilter.filter(this.attributesToFilter);
		
		return passPos; //&&passScore&&passSource&&passStrand&&passPhase&&passAttributes;
	}
	
	private boolean findLine(GffThreeEntry gTE, int pos, List<String> typesToFilter) {
		boolean passPos = false;
//		boolean passSeqIdRange = false;
//		boolean passSource = false;
		boolean passType = false;
//		boolean passScore = false;
//		boolean passStrand = false;
//		boolean passPhase = false;
//		boolean passAttributes = false;
		
		
//		SeqIdRangeGffThreeFinder seqIdRangeFinder = new SeqIdRangeGffThreeFinder(gTE);
//		passSeqIdRange = seqIdRangeFinder.filter(seqidRangesToFilter);
		
		RangeGffThreeFinder posFinder = new RangeGffThreeFinder(gTE);
		passPos = posFinder.filter(pos);
		
//		SourceGffThreeFilter sourceFilter = new SourceGffThreeFilter(gTE);
//		passSource = sourceFilter.filter(this.sourcesToFilter);
//		
		TypeGffThreeFilter typeFilter = new TypeGffThreeFilter(gTE);
		passType = typeFilter.filter(typesToFilter);
//		
//		ScoreGffThreeFilter scoreFilter = new ScoreGffThreeFilter(gTE);
//		passScore = scoreFilter.filter(this.scoresToFilter);
//		
//		StrandGffThreeFilter strandFilter = new StrandGffThreeFilter(gTE);
//		passStrand = strandFilter.filter(this.strandsToFilter);
//		
//		PhaseGffThreeFilter phaseFilter = new PhaseGffThreeFilter(gTE);
//		passPhase = phaseFilter.filter(this.phasesToFilter);
//		
//		AttributesGffThreeFilter attributeFilter = new AttributesGffThreeFilter(gTE);
//		passAttributes = attributeFilter.filter(this.attributesToFilter);
		
		return passPos&&passType; //&&passScore&&passSource&&passStrand&&passPhase&&passAttributes;
	}
	
	private boolean findLine(GffThreeEntry gTE, List<String> seqidRangesToFilter, List<String> typesToFilter) {
		boolean passSeqIdRange = false;
//		boolean passSource = false;
		boolean passType = false;
//		boolean passScore = false;
//		boolean passStrand = false;
//		boolean passPhase = false;
//		boolean passAttributes = false;
		
		
		SeqIdRangeGffThreeFinder seqIdRangeFinder = new SeqIdRangeGffThreeFinder(gTE);
		passSeqIdRange = seqIdRangeFinder.filter(seqidRangesToFilter);
		
		
//		SourceGffThreeFilter sourceFilter = new SourceGffThreeFilter(gTE);
//		passSource = sourceFilter.filter(this.sourcesToFilter);
//		
		TypeGffThreeFilter typeFilter = new TypeGffThreeFilter(gTE);
		passType = typeFilter.filter(typesToFilter);
//		
//		ScoreGffThreeFilter scoreFilter = new ScoreGffThreeFilter(gTE);
//		passScore = scoreFilter.filter(this.scoresToFilter);
//		
//		StrandGffThreeFilter strandFilter = new StrandGffThreeFilter(gTE);
//		passStrand = strandFilter.filter(this.strandsToFilter);
//		
//		PhaseGffThreeFilter phaseFilter = new PhaseGffThreeFilter(gTE);
//		passPhase = phaseFilter.filter(this.phasesToFilter);
//		
//		AttributesGffThreeFilter attributeFilter = new AttributesGffThreeFilter(gTE);
//		passAttributes = attributeFilter.filter(this.attributesToFilter);
		
		return passSeqIdRange&&passType; //&&passScore&&passSource&&passStrand&&passPhase&&passAttributes;
	}

}
