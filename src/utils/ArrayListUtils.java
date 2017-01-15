package utils;

import feature_format.gff.GffThreeEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArrayListUtils {
	
	public static long[] sumTwoLongArrays(long[] l1, long[] l2) throws Exception {
		long[] l = new long [l1.length];
	       
		if(l1.length == l2.length){
			for(int i = 0; i < l1.length; i++) {
                l[i] = l1[i] + l2[i];
            }
            return l;
        } else{
            throw new Exception();         
        }
    }
	
	public static <T> List<T> union(List<T> list1, List<T> list2) {
        Set<T> set = new HashSet<T>();

        set.addAll(list1);
        set.addAll(list2);
        
        return new ArrayList<T>(set);
    }
	
	public static List<GffThreeEntry> unionGffThreeEntryList(List<GffThreeEntry> list1, List<GffThreeEntry> list2) {
		ArrayList<GffThreeEntry> result = new ArrayList<GffThreeEntry>();
		result.addAll(list1);
		boolean inList1 = false;
		for (int i = 0; i < list2.size(); i++) {
			for (int j = 0; j < list1.size(); j++) {
				if (list2.get(i).equals(list1.get(j))) {
					inList1 = true;
				}
			}
			if (!inList1) {
				result.add(list2.get(i));
			}
			inList1 = false;
		}
		return result;
	}
	
	public static double sum(List<Double> lis) {
		double sum = 0;
		for (double d : lis) {
			sum += d;
		}
		return sum;
	}
}
