package pretesting;

import java.util.Arrays;
import java.util.HashMap;

public class TestString {

	public static void main(String[] args) {
		String s = "ab\tab";
		
		String g = s.substring(0, 4) + "gg";
		
		System.out.println(s);
		System.out.println(g);
		
		String a = "id2 \"0340\"; blubb \"ab\";";
		String[] split = a.split(";");
		for (int i = 0; i < split.length; i++) {
			System.out.println(split[i].trim());
		}
		System.out.println(Arrays.toString(split));
		
		String b = "\ttest";
		String[] bsplit = b.split("\t");
		System.out.println(bsplit.length);
		System.out.println(bsplit[0]);
		System.out.println(bsplit[1]);
		System.out.println(bsplit[0].equals(""));
		
		double[] dd = new double[3];
		dd[1] = 0.5;
		System.out.println(Arrays.toString(dd));
		HashMap<String, String> test = new HashMap<String, String>();
		test.put("ab", "value1");
		test.put("abc", "value2");
		System.out.println(test.get("ab"));
		System.out.println(test.get("abc"));
		System.out.println(test.get("abawf"));
		String test3 = test.get("asdgas");
		System.out.println(test3);
	}

}
