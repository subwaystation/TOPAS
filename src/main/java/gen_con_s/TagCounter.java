package gen_con_s;

public class TagCounter {
	
	// number of consensus ratio fails
	private int numConsensusRatio;
	// number of major allele coverage fails
	private int numMajorAlleleCoverage;
	// number of total coverage fails
	private int numTotalCoverage;
	// number of deletion calls
	private int numDeletionCall;
	// number of snp calls
	private int numSnCall;
	// number of insertion calls
	private int numInsertCall;
	// number of no calls because there was no position in either
	// snps file or indels file
	private int numNoPos;
	
	// the problematic counter for James
	private int numProblematicPos;
	
	public TagCounter(int numConsensusRatio, int numMajorAlleleCoverage,
			int numTotalCoverage, int numDeletionCall,
			int numSnpCall, int numInsertCall,
			int numNoPos, int numProblematicPos) {
		this.numConsensusRatio = numConsensusRatio;
		this.numMajorAlleleCoverage = numMajorAlleleCoverage;
		this.numTotalCoverage = numTotalCoverage;
		this.numDeletionCall = numDeletionCall;
		this.numSnCall = numSnpCall;
		this.numInsertCall = numInsertCall;
		this.numNoPos = numNoPos;
		this.numProblematicPos = numProblematicPos;
	}
	
	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		String tab = "\t";
		String n = "\n";
		sB.append("##TAG_COUNTER:"); sB.append(n);
		sB.append("#CONSENSUS_RATIO"); sB.append(tab);
		sB.append("MAJOR_ALLELE_COVERAGE"); sB.append(tab);
		sB.append("TOTAL_COVERAGE"); sB.append(tab);
		sB.append("DELETION_CALL"); sB.append(tab);
		sB.append("SN_CALL"); sB.append(tab);
		sB.append("INSERT_CALL"); sB.append(tab);
		sB.append("NO_POS"); sB.append(tab);
		
		// james
		sB.append("PROBLEMATIC_POS"); sB.append(n);
		
		sB.append(this.numConsensusRatio); sB.append(tab);
		sB.append(this.numMajorAlleleCoverage); sB.append(tab);
		sB.append(this.numTotalCoverage); sB.append(tab);
		sB.append(this.numDeletionCall); sB.append(tab);
		sB.append(this.numSnCall); sB.append(tab);
		sB.append(this.numInsertCall); sB.append(tab);
		sB.append(this.numNoPos); sB.append(tab);
		sB.append(this.numProblematicPos); sB.append(tab);
		return sB.toString();
	}
	
	public void reset() {
		this.numConsensusRatio = 0;
		this.numMajorAlleleCoverage = 0;
		this.numTotalCoverage = 0;
		this.numDeletionCall = 0;
		this.numSnCall = 0;
		this.numInsertCall = 0;
		this.numNoPos = 0;
		this.numProblematicPos = 0;
	}

	public int getNumConsensusRatio() {
		return numConsensusRatio;
	}

	public void setNumConsensusRatio(int numConsensusRatio) {
		this.numConsensusRatio = numConsensusRatio;
	}
	
	public void addNumConsensusRatio() {
		this.numConsensusRatio += 1;
	}

	public int getNumMajorAlleleCoverage() {
		return numMajorAlleleCoverage;
	}

	public void setNumMajorAlleleCoverage(int numMajorAlleleCoverage) {
		this.numMajorAlleleCoverage = numMajorAlleleCoverage;
	}
	
	public void addNumMajorAlleleCoverage() {
		this.numMajorAlleleCoverage += 1;
	}

	public int getNumTotalCoverage() {
		return numTotalCoverage;
	}

	public void setNumTotalCoverage(int numTotalCoverage) {
		this.numTotalCoverage = numTotalCoverage;
	}
	
	public void addNumTotalCoverage() {
		this.numTotalCoverage += 1;
	}

	public int getNumDeletionCall() {
		return numDeletionCall;
	}

	public void setNumDeletionCall(int numDeletionCall) {
		this.numDeletionCall = numDeletionCall;
	}
	
	public void addNumDeletionCall() {
		this.numDeletionCall += 1;
	}

	public int getNumSnpCall() {
		return numSnCall;
	}

	public void setNumSnpCall(int numSnpCall) {
		this.numSnCall = numSnpCall;
	}
	
	public void addNumSnpCall() {
		this.numSnCall += 1;
	}

	public int getNumInsertCall() {
		return numInsertCall;
	}

	public void setNumInsertCall(int numInsertCall) {
		this.numInsertCall = numInsertCall;
	}
	
	public void addNumInsertCall() {
		this.numInsertCall += 1;
	}

	public int getNumNoPos() {
		return numNoPos;
	}

	public void setNumNoPos(int numNoPos) {
		this.numNoPos = numNoPos;
	}
	
	public void addNumNoPos() {
		this.numNoPos += 1;
	}
	
	public void addNumProblematicPos() {
		this.numProblematicPos += 1;
	}
}
