package gen_con_s;

public class StatsCounter {

	// the reference length
	private int refLen;
	//the length of the consensus sequence
	private int consSeqLen;
	// the number of Ns in the consensus sequence
	private int numN;
	// the tag counter, in which all the numbers of tags are stored
	private TagCounter tagCounter;
	// the total coverage count
	private double totalCoverageCount;
	
	public StatsCounter(int refLen, int consSeqLen) {
		this.refLen = refLen;
		this.consSeqLen = consSeqLen;
		this.numN = 0;
		// initialise TagCounter
		this.tagCounter = new TagCounter(0, 0, 0, 0, 0, 0, 0, 0);
		this.totalCoverageCount = 0.0;
	}
	
	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		String n = "\n";
		String tab = "\t";
		sB.append(this.tagCounter.toString()); sB.append(n);
		sB.append("##STATS_COUNTER:"); sB.append(n);
		sB.append("#REF_LEN"); sB.append(tab);
		sB.append("CONSENSUS_LEN"); sB.append(tab);
		sB.append("NUM_N"); sB.append(tab);
		sB.append("PERCENT_N"); sB.append(tab);
		sB.append("TOTAL_COV_COUNT"); sB.append(tab);
		sB.append("COV_PER_POS"); sB.append(tab);
		sB.append(n);
		sB.append(this.refLen); sB.append(tab);
		sB.append(this.consSeqLen); sB.append(tab);
		calcNumN();
		sB.append(this.numN); sB.append(tab);
		double numNPerc = (double) ((double) this.numN / (double) this.consSeqLen);
		sB.append(String.format("%.2f", numNPerc)); sB.append(tab);
		sB.append(String.format("%.2f", this.totalCoverageCount)); sB.append(tab);
		double covPerPos = (double) ((double) this.totalCoverageCount / (double) this.consSeqLen);
		sB.append(String.format("%.2f", covPerPos));
		return sB.toString();
	}

	public void reset() {
		this.refLen = 0;
		this.consSeqLen = 0;
		this.numN = 0;
		// reset TagCounter
		this.tagCounter.reset();
		this.totalCoverageCount = 0;
	}
	
	public int getRefLen() {
		return refLen;
	}

	public void setRefLen(int refLen) {
		this.refLen = refLen;
	}

	public int getConsSeqLen() {
		return consSeqLen;
	}

	public void setConsSeqLen(int consSeqLen) {
		this.consSeqLen = consSeqLen;
	}


	/**
	 * @return int The number of Ns that are in the created consensus Sequence.
	 */
	public int getNumN() {
		return this.numN;
	}
	
	public void calcNumN() {
		int numConsensusRatio = this.tagCounter.getNumConsensusRatio();
		int numMajorAlleleCoverage = this.tagCounter.getNumMajorAlleleCoverage();
		int numTotalCoverage = this.tagCounter.getNumTotalCoverage();
		int numNoPos = this.tagCounter.getNumNoPos();
		this.numN = numConsensusRatio + numMajorAlleleCoverage + numTotalCoverage + numNoPos;
	}

	public void setNumN(int numN) {
		this.numN = numN;
	}
	
	public void addNumN() {
		this.numN += 1;
	}

	public TagCounter getTagCounter() {
		return tagCounter;
	}

	public void setTagCounter(TagCounter tagCounter) {
		this.tagCounter = tagCounter;
	}

	public double getTotalCoverageCount() {
		return totalCoverageCount;
	}

	public void setTotalCoverageCount(double totalCoverageCount) {
		this.totalCoverageCount = totalCoverageCount;
	}
	
	public void addTotalCoverageCount(double totalCoverageCount) {
		this.totalCoverageCount += totalCoverageCount;
	}

}
