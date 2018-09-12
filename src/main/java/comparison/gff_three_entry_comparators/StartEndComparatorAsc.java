package comparison.gff_three_entry_comparators;

import feature_format.gff.GffThreeEntry;

import java.util.Comparator;

public class StartEndComparatorAsc implements Comparator<GffThreeEntry> {

	@Override
	public int compare(GffThreeEntry gTE1, GffThreeEntry gTE2) {
		int compareStartResult = gTE1.getStart()-gTE2.getStart();
		if (compareStartResult == 0) {
			return gTE1.getEnd()-gTE2.getEnd();
		} else {
			return gTE1.getStart()-gTE2.getStart();
		}		
	}
	
//	private List<GffThreeEntry> gteList;
//
//    public StartEndComparatorAsc(List<GffThreeEntry> gteList) {
//        this.gteList = gteList;
//    }
//
//    public Integer[] createIndexArray() {
//        Integer[] indexes = new Integer[gteList.size()];
//        for (int i = 0; i < gteList.size(); i++) {
//            indexes[i] = i; // Autoboxing
//        }
//        return indexes;
//    }
//
//    @Override
//    public int compare(Integer index1, Integer index2) {
//         // Autounbox from Integer to int to use as array indexes
//    	int compareStartResult = gteList.get(index1).getStart() - gteList.get(index2).getStart();
//        if (compareStartResult == 0) {
//        	return gteList.get(index1).getEnd() - gteList.get(index2).getEnd();
//        } else {
//        	return compareStartResult;
//        }
//    }

}
