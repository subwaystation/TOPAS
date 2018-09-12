package pretesting.consensus;

import java.io.IOException;

import gen_con_s.VcfSnpHolder;
import vcf.VcfEntry;
import vcf.VcfLineParser;

public class TestVcfSnpHolder {

	private static final String VCF = "big_test.vcf";

	public static void main(String[] args) throws IOException {
		
		boolean f = false;
		boolean t = true;
		VcfLineParser vLP = new VcfLineParser(t, t, f, t, t, t, f, f, t, t);
		VcfSnpHolder vSH = new VcfSnpHolder(VCF, vLP);
		
		VcfEntry vE = vSH.getVcfEntry();
		
		while (!vE.isEmpty()) {
			System.out.println(vE);
			vE = vSH.getNextVcfEntry();
		}

	}

}
