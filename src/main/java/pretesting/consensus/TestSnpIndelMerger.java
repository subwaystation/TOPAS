package pretesting.consensus;

import gen_con_s.SnpIndelMerger;
import vcf.VcfEntry;

public class TestSnpIndelMerger {

	public static void main(String[] args) {
		
		VcfEntry snp = new VcfEntry("chr1", "4", null, "A", ".", 30, null, null, "GT:DP", "0/1:34");
		
		VcfEntry inDel = new VcfEntry("chr1", "4", null, "A", "AG, ATC", 60, null, null, "GT:AD", "1/2:6,14,15");
		
		SnpIndelMerger sIM = new SnpIndelMerger(snp, inDel);
		
		System.out.println(sIM.merge());
		
		snp = new VcfEntry("chr1", "4", null, "A", "G", 30, null, null, "GT:AD", "0/1:34,45");
		inDel = new VcfEntry("chr1", "4", null, "AG", "A", 60, null, null, "GT:AD", "1/2:6,55");
		sIM.setInDel(inDel);
		sIM.setSnp(snp);
		System.out.println(sIM.merge());
	}

}
