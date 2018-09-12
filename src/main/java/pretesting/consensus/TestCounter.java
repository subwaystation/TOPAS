package pretesting.consensus;

import gen_con_s.StatsCounter;
import gen_con_s.TagCounter;

public class TestCounter {

	public static void main(String[] args) {
		
		TagCounter tC = new TagCounter(0, 0, 0, 0, 0, 0, 0, 0);
		System.out.println(tC.toString());
		
		StatsCounter sC = new StatsCounter(5, 6);
		tC = sC.getTagCounter();
		tC.addNumConsensusRatio();
		tC.addNumNoPos();
		sC.setTotalCoverageCount(22);
		System.out.println(sC.toString());

	}

}
