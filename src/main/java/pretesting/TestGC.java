package pretesting;

import vcf.CoverageFraction;

public class TestGC {

	public static void main(String[] args) {
		
		String a = "ASDGATRBETREGCGCG";
		long b = calcGC(a);
		System.out.println(b);	
		
		int c = 20;
		int d = 12;
		System.out.println(c/d);
		System.out.println(c%d);
		
		double du = 4.56577;
		CoverageFraction cFractions = new CoverageFraction(a, c, a, a, du);
		System.out.println(cFractions.toString());

	}
	
	static public long calcGC(String s) {
		long gC = 0;
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == 'C') {
				gC++;
			}
			if(s.charAt(i) == 'G') {
				gC++;
			}
		}		
		return gC;
	}

}
