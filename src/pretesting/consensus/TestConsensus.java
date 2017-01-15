package pretesting.consensus;

import java.io.IOException;
import java.util.HashMap;

import vcf.VcfEntry;
import vcf.VcfSnpLineHolder;
import vcf.VcfLineParser;

public class TestConsensus {
	
	private static final String Test = "test.vcf";

	public static void main(String[] args) throws IOException {
//		VcfLineParser vLP = new VcfLineParser(true, true, false, true, true, false, false, false, false, false);
//		VcfSnpLineHolder vLH = new VcfSnpLineHolder(Test, vLP);
//		VcfEntry vE= vLH.getVcfEntry();
//		while (vE != null) {
//			System.out.println(vE);
//			vE = vLH.getNextVcfEntry();
//		}
		
		HashMap<String, Integer> hM = new HashMap<String, Integer>();
		hM.put("a", 3);
		//System.out.println(hM.get("a"));
		//System.out.println(hM.get("B"));
		
		StringBuilder sB = new StringBuilder();
		sB.append("abcdefg");
	//System.out.println(sB.toString());
		//sB.replace(1, 2, "b");
		System.out.println(sB.toString());
		sB.replace(2, 3, "");
		System.out.println(sB.toString());
		sB.replace(2, 3, "");
		System.out.println(sB.toString());
	}

}
