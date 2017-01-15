package core.fastq;

/** This class provides several functions which operate on fastq quality strings
 * @author bli.blau.blubb
 *
 */
public class QualityUtils {
	
	/**
	 * @param quality - the quality string
	 * @return a double value, which is a value between 0 and 126 as specified in
	 * ascii-encoding of a quality string
	 */
	public static double calcRQ(String quality) {
		double rQs = 0.0;
		for (int i = 0; i < quality.length(); i++) {
			rQs = rQs + (int) quality.charAt(i);
		}
		return rQs/quality.length();
	}
	
	/**
	 * @param quality - the quality string
	 * @return int - the highest base quality of the quality string 
	 */
	public static int calcHighestBaseQuality(String quality) {
		int hBQ = Integer.MIN_VALUE;
		for (int i = 0; i < quality.length(); i++) {
			hBQ = Math.max(hBQ, (int) quality.charAt(i));
		}
		return hBQ;
	}
	
	/**
	 * @param quality - the quality string
	 * @return int - the lowest base quality of the quality string
	 */
	public static int calcLowestBaseQuality(String quality) {
		int hLQ = Integer.MAX_VALUE;
		for (int i = 0; i < quality.length(); i++) {
			hLQ = Math.min(hLQ, (int) quality.charAt(i));
		}
		return hLQ;
	}

}
