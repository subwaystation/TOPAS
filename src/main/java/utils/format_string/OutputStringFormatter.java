package utils.format_string;

public class OutputStringFormatter {
	
	public static String align(String s, int length) {
		int len = s.length();
		int l = length-len;
		for (int i = 0; i < l; i++) {
			s += ' ';
		}
		return s;
	}

}
