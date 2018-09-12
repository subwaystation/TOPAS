package feature_format.gff.validate;

import feature_format.gff.GffThreeEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import comparison.gff_three_entry_comparators.GffThreeAttributesComparison;

/**
 * @author heumos
 *
 */
public class GffThreeValidator {

	private String inputFile;
	private List<GffThreeEntry> entryList;
	private List<String> entryErrorList;
	private List<String> uniqueIdErrorList;
	private List<String> relationshipErrorList;
	private List<String> warningList;
	private boolean multiFeatures;

	public GffThreeValidator(String inputFile, boolean multiFeatures) {
		this.inputFile = inputFile;
		this.entryList = new ArrayList<GffThreeEntry>();
		this.entryErrorList = new ArrayList<String>();
		this.uniqueIdErrorList = new ArrayList<String>();
		this.relationshipErrorList = new ArrayList<String>();
		this.warningList = new ArrayList<String>();
		this.multiFeatures = multiFeatures;
	}

	public List<String> getWarningList() {
		return warningList;
	}

	public void setWarningList(List<String> warningList) {
		this.warningList = warningList;
	}

	public List<String> getUniqueIdErrorList() {
		return uniqueIdErrorList;
	}

	public void setUniqueIdErrorList(List<String> uniqueIdErrorList) {
		this.uniqueIdErrorList = uniqueIdErrorList;
	}

	public List<String> getRelationshipErrorList() {
		return relationshipErrorList;
	}

	public void setRelationshipErrorList(List<String> relationshipErrorList) {
		this.relationshipErrorList = relationshipErrorList;
	}

	public List<GffThreeEntry> getEntryList() {
		return entryList;
	}

	public void setEntryList(List<GffThreeEntry> entryList) {
		this.entryList = entryList;
	}

	public List<String> getEntryErrorList() {
		return entryErrorList;
	}

	public void setEntryErrorList(List<String> errorList) {
		this.entryErrorList = errorList;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public boolean validateGffThree() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(this.inputFile));
		String line;
		long totalLines = 0;
		boolean validateGffThree = true;
		System.out.println("Validating format of GFF3Entries...");
		while ((line = br.readLine()) != null) {
			totalLines++;
			// commentLine
			if(line.startsWith("#")) {

				// featureLine
			} else {
				if (line.split("\t").length!=9) {
					int len = line.split("\t").length;
					this.entryErrorList.add("[line " + totalLines + "] A GFF3 Entry must consist of 9 tab-separated fields. GFF3Validator found " + len + " field(s).");
					validateGffThree = false;
				} else {
					if (!validateGffThreeEntry(line, totalLines)) {
						validateGffThree = false;
					}
				}

			}


		}
		br.close();

//		// sort entryList by seqIds and split this list by the different seqIds
//		SeqIdComparatorAsc seqIdCom = new SeqIdComparatorAsc(this.entryList);
//		Integer[] seqIdIndexes = seqIdCom.createIndexArray();
//		Arrays.sort(seqIdIndexes, seqIdCom);
//
//		// find all different seqIds
//		Set<String> uniqueSeqIds = new HashSet<String>();
//		for (int i = 0; i < seqIdIndexes.length; i++) {
//			uniqueSeqIds.add(this.entryList.get(seqIdIndexes[i]).getSeqId());
//		}		
//
//		// create a list of the different seqIds corresponding gff3entries
//		List<List<GffThreeEntry>> gteListList = new ArrayList<List<GffThreeEntry>>();
//		for (int i = 0; i < uniqueSeqIds.size(); i++) {
//			gteListList.add(new ArrayList<GffThreeEntry>());
//		}
//		String[] uniqueSeqIdsArray = uniqueSeqIds.toArray(new String[0]);
//		for (int i = 0; i < seqIdIndexes.length; i++) {
//			for (int j = 0; j < uniqueSeqIdsArray.length; j++) {
//				if (uniqueSeqIdsArray[j].equals(this.entryList.get(seqIdIndexes[i]).getSeqId())) {
//					gteListList.get(j).add(this.entryList.get(seqIdIndexes[i]));
//				}
//			}
//		}

		/**
		 * if a GFF3Entry has an ID, its uniquenesss is validated
		 * 
		 * ID attributes provided for features must be unique throughout the gff3 file (for each different seqId!). 
		 * These ID's are used to make "part_of" (Parent) associations between features.
		 * Each line, if contains an ID, must be unique. For multi-feature features, such as alignments,
		 * multiple lines can share the same ID. If this is the case, the seqid, source, type (method),
		 * strand, target name and all other attributes other than target must be the same.
		 * This step goes through the features that have an ID and checks for uniqueness, taking into account multi-feature cases.
		 */
		// list of the entries who have a unique id or are part of a multi-feature
		
		System.out.println("Validating uniques of IDs...");
		GffThreeUniqueIdValidator gV = new GffThreeUniqueIdValidator(this.entryList, multiFeatures);
		boolean validateUniqueIds = gV.validateUniqueIds();
		this.warningList.addAll(gV.getUniqueIdWarningList());
		HashMap<String, GffThreeEntry> uniqueIdsMap = gV.getUniqueIdsMap();
		if (!validateUniqueIds) {
			this.uniqueIdErrorList.addAll(gV.getUniqueIdErrorList());
			validateGffThree = false;
		}
				
//		for (int i = 0; i < gteListList.size(); i++) {
//			GffThreeUniqueIdValidator gV = new GffThreeUniqueIdValidator(gteListList.get(i));
//			boolean validateUniqueIds = gV.validateUniqueIds();
//			this.warningList.addAll(gV.getUniqueIdWarningList());
//			uniqueIdsListList.add(gV.getEntryList());
//			if (!validateUniqueIds) {
//				this.uniqueIdErrorList.addAll(gV.getUniqueIdErrorList());
//				validateGffThree = false;
//			}
//		}

		// validate parentage on uniqueIdsMap
		System.out.println("Validating 'part of' relationship...");
		GffThreeRelationshipsValidator gR = new GffThreeRelationshipsValidator(uniqueIdsMap);
		boolean validateRelationships = gR.validateRelationships();
		if (!validateRelationships) {
			this.relationshipErrorList.addAll(gR.getErrorList());
			validateGffThree = false;
		}
		return validateGffThree;

	}

	 /**
	  * @param line
	  * parses the current line and adds it to the GffThreeEntryList if it is a verified Gff3Entry
	  */
	 private boolean validateGffThreeEntry(String line, long totalLines) {

		 // split entryLine by tab to get all the different columns
		 String[] split = line.split("\t");

		 // fetch the seqId
		 String seqId = split[0];

		 // fetch the source
		 String source = split[1];

		 // fetch the type
		 String type = split[2];

		 // fetch the start
		 String start = split[3];

		 // fetch the end
		 String end = split[4];

		 // fetch the score
		 String score = split[5];

		 // fetch the strand
		 String strand = split[6];

		 // fetch the phase
		 String phase = split[7];

		 // fetch the attributes
		 String attributes = split[8];

		 // verify the Gff3 Entry
		 GffThreeEntryValidator gV = new GffThreeEntryValidator(seqId, source, type, start, end, score, strand, phase, attributes, totalLines);
		 boolean validateEntry = gV.validate();

		 if (validateEntry) {
			 // entry was validated, add entry to entryList for further analysis (sorting, unique id and parentage verification)
			 SortedMap<String, String> sortedAttributes = createAttributes(attributes);
			 addToGffThreeEntryList(seqId, source, type, start, end, score, strand, phase, sortedAttributes, totalLines);
			 return true;
		 } else {
			 // entry was not validated, collect all the errors
			 this.entryErrorList.addAll(gV.getErrorList());
			 return false;
		 }
	 }

	 /**
	  * @param attributesString
	  * @return
	  */
	 private SortedMap<String, String> createAttributes(String attributesString) {
		 SortedMap<String, String> sortedAttributes = new TreeMap<String, String>(
				 new Comparator<String>() {
					 @Override
					 public int compare(String e1Key, String e2Key) {
						 return GffThreeAttributesComparison.compareAttributes(e1Key, e2Key);
					 }
				 });
		 String[] attributes = attributesString.split(";");
		 for(int i = 0; i < attributes.length; i++) {
			 String[] splitEqual = attributes[i].split("=");
			 sortedAttributes.put(splitEqual[0], splitEqual[1]);
		 }
		 return sortedAttributes;
	 }

	 /**
	  * @param seqId
	  * @param source
	  * @param type
	  * @param start
	  * @param end
	  * @param score
	  * @param strand
	  * @param phase
	  * @param attributes
	  */
	 private void addToGffThreeEntryList(String seqId, String source,
			 String type, String start, String end, String score, String strand,
			 String phase, SortedMap<String, String> attributes, long totalLines) {
		 GffThreeEntry gTF = new GffThreeEntry(seqId, source, type,
				 Integer.parseInt(start), Integer.parseInt(end), score, strand, phase, attributes, totalLines);
		 this.entryList.add(gTF);

	 }

}
