package phy_cc;

public class PhyCcClassificator {
	
	// no call was possible
	private static final String N = "N";
	// a good quality reference call
	private static final String DOT = ".";
	// a good call "A"
	private static final String A = "A";
	// a good call "C"
	private static final String C = "C";
	// a good call "G"
	private static final String G = "G";
	// a good call "T"
	private static final String T = "T";
	// a bad quality reference call
	private static final String R = "R";
	// a bad "A" call
	private static final String a = "a";
	// a bad "c" call
	private static final String c = "c";
	// a bad "g" call
	private static final String g = "g";
	// a bad "t" call
	private static final String t = "t";

	
	// the actual call
	private String call;
	
	// the low coverage sample to the corresponding call
	private LowCovSample lowCovSample;
	
	public PhyCcClassificator(String call, LowCovSample lowCovSample) {
		this.call = call;
		this.lowCovSample = lowCovSample;
	}
	
	public void performClassification() {
		switch(this.call) {
		case N: 
			break;
		case DOT: 
			this.lowCovSample.addSafeRef();
			break;
		case A: case C: case G: case T:
			this.lowCovSample.addSafeSnp();
			break;
		case R:
			this.lowCovSample.addUnsafeRef();
		case a: case c: case g: case t:
			this.lowCovSample.addUnsafeSnp();
			break;
		default: break;
		}
	}

	public String getCall() {
		return call;
	}

	public void setCall(String call) {
		this.call = call;
	}

	public LowCovSample getLowCovSample() {
		return lowCovSample;
	}

	public void setLowCovSample(LowCovSample lowCovSample) {
		this.lowCovSample = lowCovSample;
	}

}
