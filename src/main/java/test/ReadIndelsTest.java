package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import gen_con_s.ReadIndels;
import vcf.VcfEntry;

public class ReadIndelsTest {

	public static void main(String[] args) throws IOException {

		String indelFile = "indels.vcf";

		ReadIndels rI = new ReadIndels(indelFile);
		HashMap<String, TreeMap<Integer, VcfEntry>> indels = rI.readIndels();
		ArrayList<String> warnings = rI.getWarningList();
		TreeMap<Integer, VcfEntry> poss = indels.get("gi|251831106|ref|NC_012920.1|");
		for (VcfEntry vE : poss.values()) {
			System.out.println(vE);
		}
		TreeMap<Integer, VcfEntry> posss = indels.get("gi|251831106|ref|NC_012920.2|");
		for (VcfEntry vE : posss.values()) {
			System.out.println(vE);
		}
		for (String warning : warnings) {
			System.out.println(warning);
		}
		System.out.println();
		System.out.println("Solution:");
		String solution = "gi|251831106|ref|NC_012920.1|	309	.	C	CCT	57.73	.	AC=1;AF=0.500;AN=2;BaseQRankSum=-0.318;DP=19;FS=8.989;MLEAC=1;MLEAF=0.500;MQ=40.97;MQ0=0;MQRankSum=-1.694;QD=3.04;ReadPosRankSum=0.106;SOR=1.421	GT:AD:DP:GQ:PL	0/1:9,7:19:95:95,0,269\n"
				+"gi|251831106|ref|NC_012920.1|	310	.	ACG	A	57.73	.	AC=1;AF=0.500;AN=2;BaseQRankSum=-0.318;DP=19;FS=8.989;MLEAC=1;MLEAF=0.500;MQ=40.97;MQ0=0;MQRankSum=-1.694;QD=3.04;ReadPosRankSum=0.106;SOR=1.421	GT:AD:DP:GQ:PL	0/1:100,7:19:95:95,0,269\n"
				+"gi|251831106|ref|NC_012920.1|	312	.	ATG	A	57.73	.	AC=1;AF=0.500;AN=2;BaseQRankSum=-0.318;DP=19;FS=8.989;MLEAC=1;MLEAF=0.500;MQ=40.97;MQ0=0;MQRankSum=-1.694;QD=3.04;ReadPosRankSum=0.106;SOR=1.421	GT:AD:DP:GQ:PL	0/1:9,7:19:95:95,0,269\n"
				+"gi|251831106|ref|NC_012920.1|	345	.	C	CCT	57.73	.	AC=1;AF=0.500;AN=2;BaseQRankSum=-0.318;DP=19;FS=8.989;MLEAC=1;MLEAF=0.500;MQ=40.97;MQ0=0;MQRankSum=-1.694;QD=3.04;ReadPosRankSum=0.106;SOR=1.421	GT:AD:DP:GQ:PL	0/1:9,7:19:95:95,0,269\n"
				+"gi|251831106|ref|NC_012920.1|	347	.	C	CCT	57.73	.	AC=1;AF=0.500;AN=2;BaseQRankSum=-0.318;DP=19;FS=8.989;MLEAC=1;MLEAF=0.500;MQ=40.97;MQ0=0;MQRankSum=-1.694;QD=3.04;ReadPosRankSum=0.106;SOR=1.421	GT:AD:DP:GQ:PL	0/1:9,7:19:95:95,0,269\n"
				+"gi|251831106|ref|NC_012920.1|	348	.	AC	A	57.73	.	AC=1;AF=0.500;AN=2;BaseQRankSum=-0.318;DP=19;FS=8.989;MLEAC=1;MLEAF=0.500;MQ=40.97;MQ0=0;MQRankSum=-1.694;QD=3.04;ReadPosRankSum=0.106;SOR=1.421	GT:AD:DP:GQ:PL	0/1:9,7:19:95:95,0,269\n"
				+"gi|251831106|ref|NC_012920.1|	555	.	CG	C	57.73	.	AC=1;AF=0.500;AN=2;BaseQRankSum=-0.318;DP=19;FS=8.989;MLEAC=1;MLEAF=0.500;MQ=40.97;MQ0=0;MQRankSum=-1.694;QD=3.04;ReadPosRankSum=0.106;SOR=1.421	GT:AD:DP:GQ:PL	0/1:9,7:19:95:95,0,269\n"
				+"gi|251831106|ref|NC_012920.1|	655	.	CG	C	57.73	.	AC=1;AF=0.500;AN=2;BaseQRankSum=-0.318;DP=19;FS=8.989;MLEAC=1;MLEAF=0.500;MQ=40.97;MQ0=0;MQRankSum=-1.694;QD=3.04;ReadPosRankSum=0.106;SOR=1.421	GT:AD:DP:GQ:PL	0/1:9,7:19:95:95,0,269\n"
				+"gi|251831106|ref|NC_012920.2|	309	.	C	CCT	57.73	.	AC=1;AF=0.500;AN=2;BaseQRankSum=-0.318;DP=19;FS=8.989;MLEAC=1;MLEAF=0.500;MQ=40.97;MQ0=0;MQRankSum=-1.694;QD=3.04;ReadPosRankSum=0.106;SOR=1.421	GT:AD:DP:GQ:PL	0/1:9,7:19:95:95,0,269\n";
		System.out.println(solution);
	}

}
