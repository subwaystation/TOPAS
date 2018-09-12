package core.sequence;

public class OutputSequenceFormatter {
	
	public static String formatSequence(String s, int lineWidth) {
		StringBuilder result = new StringBuilder();
		int len = s.length();
		int full = len/lineWidth;
		int rest = len%lineWidth;
		int begin = 0;
		int end = lineWidth;
		for(int i = 0; i < full; i++) {
			result.append(s.substring(begin, end));
			result.append("\n");
			begin += lineWidth;
			end += lineWidth;
		}
		if (rest != 0) {
			result.append(s.substring(len-rest, len));
		} else {
			result.deleteCharAt(result.length()-1);
		}
		return result.toString();
	}

}
