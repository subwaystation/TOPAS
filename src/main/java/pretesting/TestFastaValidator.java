package pretesting;

public class TestFastaValidator {

	public static void main(String[] args) {
		
		long[] a = new long[5];
		
		long[] b = new long[5];
		
		b[0] = 0;
		b[1] = 1;
		b[2] = 2;
		b[3] = 3;
		b[4] = 4;
		
		String bb = "";
		
		for(int i = 0; i < b.length; i++){
		    bb = bb + String.valueOf(b[i]);
		}
		System.out.println(bb);
		
		a = b;
		String aa = "";
		for(int i = 0; i < a.length; i++){
			aa = aa + String.valueOf(a[i]);
		}
		System.out.println(aa);

	}

}
