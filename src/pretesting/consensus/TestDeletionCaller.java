package pretesting.consensus;

import gen_con_s.DeletionCaller;
import vcf.VcfEntry;

public class TestDeletionCaller {

	public static void main(String[] args) {
		
		VcfEntry del = new VcfEntry("chr1", "4", null, "AC", "A", 60, null, null, "AD", "111,14");
		VcfEntry snp1 = new VcfEntry("chr1", "5", null, "C", "G", 60, null, null, "AD", "11,14");
		VcfEntry snp2 = new VcfEntry("chr1", "5", null, "C", "G", 60, null, null, "AD", "111,123");
		int deletionBaseIndex = 0;
		double consensusRatio = 0.8;
		
		DeletionCaller dC = new DeletionCaller(del, deletionBaseIndex, consensusRatio);
		dC.addSnp(snp1);
		dC.finishDel();
		String callType = dC.getCallType();
		System.out.println(callType);
		if (callType.equals("del")) {
			System.out.println(dC.getDeletionBases());
		}
		dC.reset();
		del = new VcfEntry("chr1", "4", null, "AC", "A", 60, null, null, "AD", "14,3");
		dC.setDeletion(del);
		// no call
		// dC.addSnp(snp1);
		// call on snp
		dC.addSnp(snp2);
		dC.finishDel();
		System.out.println(dC.getCallType());
	}

}
