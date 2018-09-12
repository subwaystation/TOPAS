package comparison;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import phy_cc.SnpCall;

public class MapListSizeComparatorDesc implements Comparator<String> {
	
	private Map<String, List<SnpCall>> agreeingSnpMap;
	
	public MapListSizeComparatorDesc(Map<String, List<SnpCall>> agreeingSnpMap) {
		this.agreeingSnpMap = agreeingSnpMap;
	}
	
	@Override
	public int compare(String key1, String key2) {
		Integer size1 = (Integer) this.agreeingSnpMap.get(key2).size();
		Integer size2 = (Integer) this.agreeingSnpMap.get(key1).size();
		if (size1 >= size2) {
			return 1;
		} else {
			return -1;
		}
	}
}
