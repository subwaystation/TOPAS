package phy_cc;

import java.util.Comparator;
import java.util.Map;

public class SamplePairingNormComparator implements Comparator<String> {
	
private Map<String, Double> zStarScores;
	
	public SamplePairingNormComparator(Map<String, Double> zStarScores) {
		this.zStarScores = zStarScores;
	}
	
	@Override
	public int compare(String key1, String key2) {
		double zStar1 = this.zStarScores.get(key2);
		double zStar2 = this.zStarScores.get(key1);
		if (zStar1 >= zStar2) {
			return 1;
		} else {
			return -1;
		}
	}
}
