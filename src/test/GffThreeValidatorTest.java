package test;

import java.io.IOException;

import feature_format.gff.validate.GffThreeValidator;

public class GffThreeValidatorTest {
	
	private static final String gff = "test.gff";

	public static void main(String[] args) throws IOException {
		
		GffThreeValidator gV = new GffThreeValidator(gff, true);
		boolean vG = gV.validateGffThree();
		
		if (!vG) {
			System.out.println();
			System.out.println("EntryErrors: ");
			System.out.println();
			for (int i = 0; i < gV.getEntryErrorList().size(); i++) {
				System.out.println(gV.getEntryErrorList().get(i));
			}
			System.out.println();
			System.out.println("UniqueIdErrors: ");
			System.out.println();
			for (int i = 0; i < gV.getUniqueIdErrorList().size(); i++) {
				System.out.println(gV.getUniqueIdErrorList().get(i));
			}
			System.out.println();
			System.out.println("RelationshipErrors: ");
			System.out.println();
			for (int i = 0; i < gV.getRelationshipErrorList().size(); i++) {
				System.out.println(gV.getRelationshipErrorList().get(i));
			}
			System.out.println();
			System.out.println("Warnings: ");
			System.out.println();
			for (int i = 0; i < gV.getWarningList().size(); i++) {
				System.out.println(gV.getWarningList().get(i));
			}
		} else {
			System.out.println("ValidationSuccess!");
		}
		
		// List<GffThreeEntry> gteList = gV.getEntryList();
		
		// SORTING
//		SeqIdComparatorAsc seqIdCom = new SeqIdComparatorAsc(gteList);
//		Integer[] seqIdIndexes = seqIdCom.createIndexArray();
//		Arrays.sort(seqIdIndexes, seqIdCom);
//		System.out.println(Arrays.toString(seqIdIndexes));
//		
//		// find all different seqIds
//		Set<String> uniqueSeqIds = new HashSet<String>();
//		for (int i = 0; i < seqIdIndexes.length; i++) {
//			uniqueSeqIds.add(gteList.get(seqIdIndexes[i]).getSeqId());
//		}		
//		System.out.println(uniqueSeqIds);
//		
//		// create a list of the different seqIds corresponding gff3entries
//		List<List<GffThreeEntry>> gteListList = new ArrayList<List<GffThreeEntry>>();
//		for (int i = 0; i < uniqueSeqIds.size(); i++) {
//			gteListList.add(new ArrayList<GffThreeEntry>());
//		}
//		String[] uniqueSeqIdsArray = uniqueSeqIds.toArray(new String[0]);
//		for (int i = 0; i < seqIdIndexes.length; i++) {
//			for (int j = 0; j < uniqueSeqIdsArray.length; j++) {
//				if (uniqueSeqIdsArray[j].equals(gteList.get(seqIdIndexes[i]).getSeqId())) {
//					gteListList.get(j).add(gteList.get(seqIdIndexes[i]));
//				}
//			}
//		}
//		for (int i = 0; i < gteListList.size(); i++) {
//			System.out.println(gteListList.get(i));
//		}
		
		// VALIDATE uniqueness of IDs
		
		
	}

}
