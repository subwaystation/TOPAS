package comparison;

import java.util.Comparator;

public class ArrayIndexComparator implements Comparator<Integer> {
    
	private String[] array;

    public ArrayIndexComparator(String[] array) {
        this.array = array;
    }

    public Integer[] createIndexArray() {
        Integer[] indexes = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2) {
         // Autounbox from Integer to int to use as array indexes
        return array[index1].compareTo(array[index2]);
    }
}
