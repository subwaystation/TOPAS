package pretesting.consensus;

import gen_con_s.ConsensusBaseCaller;
import gen_con_s.StatsCounter;
import gen_con_s.TagCounter;
import vcf.VcfEntry;

public class TestConsensusBaseFinder {

	public static void main(String[] args) {
		StatsCounter sC = new StatsCounter(0, 0);
		VcfEntry vE = new VcfEntry("chr1", "4", null, "AC", "A", 60, null, null, "AD", "111,14");
		int majorAlleleCoverage = 5;
		double consensusRatio = 0.75;
		double punishmentRatio = 0.8;
		int totalCoverageThreshold = -1;
		boolean minorB = false;
		ConsensusBaseCaller cBF = new ConsensusBaseCaller(majorAlleleCoverage,
				consensusRatio, punishmentRatio, totalCoverageThreshold, vE, sC, minorB);
		cBF.findConsensusBase();
		System.out.println(cBF.getCallType());
//		System.out.println(cBF.getDeletionBaseIndex());
		cBF.reset();
		vE = new VcfEntry("chr1", "4", null, "AC", "A", 60, null, null, "AD", "11,11");
		cBF.setEntryToCall(vE);
		cBF.findConsensusBase();
		System.out.println(cBF.getCallType());
	}

}
