package core.sequence;

public class SequenceUtils {
	
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
	
	static public long[] calcDnaBases(String s) {
		long[] bases = new long[5];
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == 'A') {
				bases[0] = bases[0] +1;
			}
			if(s.charAt(i) == 'C') {
				bases[1] = bases[1] +1;
			}
			if(s.charAt(i) == 'T') {
				bases[2] = bases[2] +1;
			}
			if(s.charAt(i) == 'G') {
				bases[3] = bases[3] +1;
			}
			if(s.charAt(i) == 'N') {
				bases[4] = bases[4] +1;
			}
		}		
		return bases;
	}

}
