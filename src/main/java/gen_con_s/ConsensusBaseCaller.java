package gen_con_s;

import java.util.ArrayList;

import vcf.VcfEntry;
import core.sequence.DnaSequence;

public class ConsensusBaseCaller {

	private VcfEntry entryToCall;
	private int majorAlleleCoverage;
	private double consensusRatio;
	private double punishmentRatio;
	private double totalCoverageThreshold;

	private double totalCoverageCount;
	private double totalCoverageDamageCount;
	private ArrayList<String> bases;
	private String base;

	private String callType;
	private boolean call;
	
	private StatsCounter statsCounter;
	private double[] baseCounter;
	private int baseIndex;
	private double ratio;
	
	private boolean minorB;
	
	private ArrayList<Double> totalCoverageDamageList;

	public ConsensusBaseCaller(int majorAlleleCoverage,
			double consensusRatio, 
			double punishmentRatio, double totalCoverageThreshold,
			VcfEntry curSnpEntry, StatsCounter statsCounter, boolean minorB) {
		this.majorAlleleCoverage = majorAlleleCoverage;
		this.consensusRatio = consensusRatio;
		this.punishmentRatio = punishmentRatio;
		this.totalCoverageThreshold = totalCoverageThreshold;
		this.entryToCall = curSnpEntry;
		this.bases = new ArrayList<String>();
		
		this.call = false;
		this.callType = "none";
		this.base = "N";
		this.statsCounter = statsCounter;
		this.baseCounter = new double[5];
		this.baseIndex = -1;
		this.ratio = 0.0;
		this.minorB = minorB;
		this.totalCoverageDamageList = new ArrayList<Double>();
	}	

	public void reset() {
		this.totalCoverageCount = 0.0;
		this.totalCoverageDamageCount = 0.0;
		this.base = VitalStr.N;
		this.bases.clear();
		this.callType = "none";
		this.call = false;
		this.baseCounter = new double[5];
		this.baseIndex = -1;
		this.ratio = 0.0;
		this.totalCoverageDamageList.clear();
	}

	public void pickUpBases() {
		String ref = this.entryToCall.getRef();
		String alt = this.entryToCall.getAlt();
		String[] refSplit = ref.split(VitalStr.COMMA);
		for (String base : refSplit) {
			this.bases.add(base);
		}
		// check if alt equals "."
		if (!alt.equals(VitalStr.DOT)) {
			String[] altSplit = alt.split(VitalStr.COMMA);
			for (String base : altSplit) {
				this.bases.add(base);
			}
		}
	}

	public void findConsensusBase() {
		pickUpBases();
		this.totalCoverageCount = 0.0;
		this.totalCoverageDamageCount = 0.0;
		double sum = 0.0;
		double maximumAlelleCoverage = Double.MIN_VALUE;
		boolean[] basesB = new boolean[5];
		String sample = this.entryToCall.getSampleIds();
		String[] sampleSplit = sample.split(VitalStr.COLON);
		String[] aDSplit = sampleSplit[0].split(VitalStr.COMMA);
		for (int i = 0; i < aDSplit.length; i++) {
			String base = this.bases.get(i);
			// is base length one?
			double alleleDepth = Double.parseDouble(aDSplit[i]);
			if (base.length() == 1) {
				this.baseIndex = DnaSequence.DNA_BASES.indexOf(base.charAt(0));
				basesB[this.baseIndex] = true;
			} 
			this.totalCoverageCount += alleleDepth;
			if (base.length() == 1) {
				// find out, if punishment is necessary
				alleleDepth = alleleDepth * discoverPunishment(basesB, this.baseIndex, base);
				// set one of ACTG
				this.baseCounter[this.baseIndex] = alleleDepth;
			} else {
				// set other
				this.baseCounter[4] = alleleDepth;
			}
			this.totalCoverageDamageCount += alleleDepth;
			sum += alleleDepth;
			this.totalCoverageDamageList.add(alleleDepth);
			if (maximumAlelleCoverage < alleleDepth) {
				maximumAlelleCoverage = alleleDepth;
				this.base = base;
			}
		}
		this.totalCoverageDamageCount = sum;
		this.statsCounter.addTotalCoverageCount(sum);

		// reached total coverage threshold
		if (sum >= this.totalCoverageThreshold) {
			// reached major allele coverage threshold
			if (maximumAlelleCoverage >= (double) this.majorAlleleCoverage) {
				this.ratio = maximumAlelleCoverage / sum;
				// reached required ratio to make a consensus call
				if (this.ratio >= this.consensusRatio) {
					this.call = true;
					// we have an insertion
					if (this.base.length() > 1) {
						if (baseInRef()) {
							// what if base length > 1 and the base is in the reference?!
							// only take "reference" here, if SN_CALL did NOT happen!
							
							this.callType = VitalStr.SN_CALL;
						} else {
							this.callType = VitalStr.INSERT_CALL;
						}
					} else if (isDel()){
						this.callType = VitalStr.DEL_CALL;
					} else {
						
						/**
						 * James Case
						 * 
						 * We now have a base of length 1
						 */
						if (this.minorB) {
							jamesCase();
						} else {
							this.callType = VitalStr.SN_CALL;
						}
					}
				} else {
					this.callType = VitalStr.CONSENSUS_RATIO;
				}
			} else {
				this.callType = VitalStr.MAJOR_ALLELE_COVERAGE;
			}
		} else {
			this.callType = VitalStr.TOTAL_COVERAGE;
		}
	}

	private void jamesCase() {
		// we know that the current majority base has a length of 1
		
		// check if only reference
		if (this.bases.size() == 1) {
			this.callType = VitalStr.SN_CALL;
		} else {
			// check if we only see 2 bases and both of them have length 1
			if (this.bases.size() == 2) {
				if (this.bases.get(0).length() == 1 && this.bases.get(1).length() == 1) {
					int basePos = this.bases.indexOf(this.base);
					int minorBasePos = -1;
					if (basePos == 0) {
						minorBasePos = 1;
					} else {
						minorBasePos = 0;
					}
					// check if the major allele coverage for the minor base is reached
					if (this.totalCoverageDamageList.get(minorBasePos) >= this.majorAlleleCoverage) {
						this.base = this.bases.get(minorBasePos);
						this.callType = VitalStr.SN_CALL;
					} else {
						this.callType = VitalStr.PROBLEMATIC;
						this.call = false;
					}
				} else {
					this.callType = VitalStr.PROBLEMATIC;
					this.call = false;
				}
			} else {
				this.callType = VitalStr.PROBLEMATIC;
				this.call = false;
			}
		}
	}

	private double discoverPunishment(boolean[] basesB, int baseIndex,
			String base) {
		// check if punishment is necessary
		if (base.equals(VitalStr.G) || base.equals(VitalStr.C)) {
			return 1.0;
		} else {
			switch(baseIndex) {
			// base was an A, check if a G was already there
			case 0: 
				if (basesB[3]) {
					return this.punishmentRatio;
				} else {
					return 1.0;
				}
				// base was a C, check if a T was already there	
			case 1:
				if (basesB[2]) {
					return this.punishmentRatio;
				} else {
					return 1.0;
				}
				// base was a T, check if a C was already there
			case 2:
				if (basesB[1]) {
					return this.punishmentRatio;
				} else {
					return 1.0;
				}
				// base was a G, check if an A was already there
			case 3:
				if (basesB[0]) {
					return this.punishmentRatio;
				} else {
					return 1.0;
				}
			default: 
				return 1.0;
			}
		}
	}

	public StatsCounter getTagCounter() {
		return statsCounter;
	}

	public void setTagCounter(StatsCounter statsCounter) {
		this.statsCounter = statsCounter;
	}

	public String getBase() {
		return base;
	}


	public void setBase(String base) {
		this.base = base;
	}

	public int getMajorAlleleCoverage() {
		return majorAlleleCoverage;
	}

	public void setMajorAlleleCoverage(int majorAlleleCoverage) {
		this.majorAlleleCoverage = majorAlleleCoverage;
	}

	public double getConsensusRatio() {
		return consensusRatio;
	}

	public void setConsensusRatio(double consensusRatio) {
		this.consensusRatio = consensusRatio;
	}

	public double getPunishmentRatio() {
		return punishmentRatio;
	}

	public void setPunishmentRatio(double punishmentRatio) {
		this.punishmentRatio = punishmentRatio;
	}

	public double getTotalCoverage() {
		return totalCoverageThreshold;
	}

	public void setTotalCoverage(double totalCoverage) {
		this.totalCoverageThreshold = totalCoverage;
	}

	public double getTotalCoverageCount() {
		return totalCoverageCount;
	}

	public void setTotalCoverageCount(double totalCoverageCount) {
		this.totalCoverageCount = totalCoverageCount;
	}

	public double getTotalCoverageDamageCount() {
		return totalCoverageDamageCount;
	}

	public void setTotalCoverageDamageCount(double totalCoverageDamageCount) {
		this.totalCoverageDamageCount = totalCoverageDamageCount;
	}

	public VcfEntry getvE() {
		return entryToCall;
	}

	public void setvE(VcfEntry vE) {
		this.entryToCall = vE;
	}


	public double getTotalCoverageThreshold() {
		return totalCoverageThreshold;
	}

	public void setTotalCoverageThreshold(double totalCoverageThreshold) {
		this.totalCoverageThreshold = totalCoverageThreshold;
	}

	public VcfEntry getEntryToCall() {
		return entryToCall;
	}

	public void setEntryToCall(VcfEntry entryToCall) {
		this.entryToCall = entryToCall;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}

	public boolean isCall() {
		return call;
	}

	public void setCall(boolean call) {
		this.call = call;
	}

	public double[] getBaseCounter() {
		return baseCounter;
	}

	public void setBaseCounter(double[] baseCounter) {
		this.baseCounter = baseCounter;
	}

	public double getRatio() {
		return ratio;
	}

	public void setRatio(double ratio) {
		this.ratio = ratio;
	}

	private boolean baseInRef() {
		boolean baseInRef = false;
		for (String base : this.entryToCall.getRef().split(",")) {
			if (base.equals(this.base)) {
				baseInRef = true;
				return true;
			}
		}
		return baseInRef;
	}

	/**
	 * Is the current callable base a deletion?
	 * @return
	 * true, if it is a deletion, else false
	 */
	private boolean isDel() {
		boolean isDel = false;
		if (this.base.length() == 1 && this.entryToCall.isDel()) {
			String[] altSplit = this.entryToCall.getAlt().split(",");
			for (int i = 0; i < altSplit.length; i++) {
				if (altSplit[i].equals(this.base)) {
					isDel = true;
//					this.deletionBaseIndex = i;
					return isDel;
				}
			}
		} else {
			return isDel;
		}
		return isDel;
	}

}
