package utils;

import java.lang.reflect.Array;

public class ArrayUtils {
	
	public static int countTrue(boolean[] b) {
		int count = 0;
		for (int i = 0; i < b.length; i++) 
			if (b[i]) {
				count++;
			}
		return count;
	}
	
	public static int[] findMaxPosMaxValue(int[] array) {
		int maxPos = -1;
		int maxValue = Integer.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			int curValue = array[i];
			if (curValue > maxValue) {
				maxPos = i;
				maxValue = curValue;
			}
		}
		return new int[]{maxPos, maxValue};
	}
	
	public static boolean isInArray(int[] array, int num) {
		boolean isIn = false;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == num) {
				return true;
			}
		}
		return isIn;
	}
	
	public static <T> T[] concatenate (T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}

}
