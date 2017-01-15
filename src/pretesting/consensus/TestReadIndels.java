package pretesting.consensus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import gen_con_s.ReadIndels;
import vcf.VcfEntry;

public class TestReadIndels {

	public static void main(String[] args) throws IOException {
		
		//String indelFile = "big_test.indels.vcf";
		//ReadIndels rI = new ReadIndels(indelFile);
		
		//HashMap<String, TreeMap<Integer, VcfEntry>> indels = rI.readIndels();
		//System.out.println(indels);
		//ArrayList<String> warnings = rI.getWarningList();
		//System.out.println(warnings);
		
		ReadIndels rI = new ReadIndels("/home/heumos/Desktop/test/1422_circ.indels.vcf");
		HashMap<String, TreeMap<Integer, VcfEntry>> indels = rI.readIndels();
		System.out.println(indels);
		ArrayList<String> warnings = rI.getWarningList();
		System.out.println(warnings);

	}

}
