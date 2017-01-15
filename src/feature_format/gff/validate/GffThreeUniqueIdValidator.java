package feature_format.gff.validate;

import feature_format.gff.GffThreeEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GffThreeUniqueIdValidator {

	private List<GffThreeEntry> entryList;
	private List<String> uniqueIdErrorList;
	private List<String> uniqueIdWarningList;
	HashMap<String, GffThreeEntry> uniqueIdsMap;
	private boolean multiFeatures;
	
	public GffThreeUniqueIdValidator(List<GffThreeEntry> entryList, boolean multiFeatures) {
		this.entryList = entryList;
		this.uniqueIdErrorList = new ArrayList<String>();
		this.uniqueIdWarningList = new ArrayList<String>();
		this.uniqueIdsMap = new HashMap<String, GffThreeEntry>();
		this.multiFeatures = multiFeatures;
	}

	public HashMap<String, GffThreeEntry> getUniqueIdsMap() {
		return uniqueIdsMap;
	}

	public void setUniqueIdsMap(HashMap<String, GffThreeEntry> uniqueIdsMap) {
		this.uniqueIdsMap = uniqueIdsMap;
	}

	public List<String> getUniqueIdWarningList() {
		return uniqueIdWarningList;
	}

	public void setUniqueIdWarningList(List<String> uniqueIdWarningList) {
		this.uniqueIdWarningList = uniqueIdWarningList;
	}

	public List<GffThreeEntry> getEntryList() {
		return entryList;
	}

	public void setEntryList(List<GffThreeEntry> entryList) {
		this.entryList = entryList;
	}

	public List<String> getUniqueIdErrorList() {
		return uniqueIdErrorList;
	}

	public void setUniqueIdErrorList(List<String> uniqueIdErrorList) {
		this.uniqueIdErrorList = uniqueIdErrorList;
	}

	/**
	 * @return
	 */
	public boolean validateUniqueIds() {
		boolean validateUniqueIds = true;

		// first collect all entries with non_unique IDS
		List<GffThreeEntry> notUniqueIdsList = new ArrayList<GffThreeEntry>();
		Map<String, List<Long>> indexList = new HashMap<String, List<Long>>();
		List<List<Long>> indexesList = new ArrayList<List<Long>>();
		for (int i = 0; i < this.entryList.size(); i++) {
			// check if entry has ID
			if (this.entryList.get(i).hasId()) {
				String value = this.entryList.get(i).getAttributes().get("ID");
				List<Long> indexes = indexList.get(value);
				if (indexes == null) {
					indexList.put(value, indexes = new LinkedList<Long>());
				}
				indexes.add(this.entryList.get(i).getLine());
				// found duplicate, do what you like
				if (indexes.size() > 1) {
					validateUniqueIds = false;
					indexesList.add(indexes);
				} else {
					// add entry to uniqueIdsMap because entry is unique
					this.uniqueIdsMap.put(value, this.entryList.get(i));
				}
			}			
		}
		if (!validateUniqueIds) {
			// remove duplicates out of indexesList
			HashSet<List<Long>> hashSet = new HashSet<List<Long>>();
			hashSet.addAll(indexesList);
			indexesList.clear();
			indexesList.addAll(hashSet);
			HashMap<Long, GffThreeEntry> lineGffThreeEntryMap = new HashMap<Long, GffThreeEntry>();
			for (int k = 0; k < this.entryList.size(); k++) {
				GffThreeEntry gTE = this.entryList.get(k);
				lineGffThreeEntryMap.put(gTE.getLine(), gTE);
			}
			// add nonUniqueIdsGFF3Entries to notUniqueIdsList if they are not a multiFeature
			for(int j = 0; j < indexesList.size(); j++) {
				List<GffThreeEntry> entries = findEntries(indexesList.get(j), lineGffThreeEntryMap); 
				if (!areMultiFeatures(entries)) {
					notUniqueIdsList.addAll(entries);
					long[] lines = new long[entries.size()];
					for (int i = 0; i < entries.size(); i++) {
						lines[i] = entries.get(i).getLine();
					}
					String error = "";
					error += "[lines " + Arrays.toString(lines) + "] These Entries have the same ID but don't belong to a multi-feature. Please make the IDs unique.";
					this.uniqueIdErrorList.add(error);
				} else {
					if (this.multiFeatures) {
						long[] lines = new long[entries.size()];
						for (int i = 0; i < entries.size(); i++) {
							lines[i] = entries.get(i).getLine();
						}
						String warning = "";
						warning += "[lines " + Arrays.toString(lines) + "] These entries have the same ID but are regarded as a multi-feature"
								+ " (and therefore are allowed to have the same ID)."
								+ " Nevertheless please check that statement.";
						this.uniqueIdWarningList.add(warning);
					}					
				}
			}
		}
		return validateUniqueIds;
	}

		/**
	 * For multi-feature features, such as alignments, multiple lines can share the same ID.
	 * If this is the case, the seqid, source, type (method), strand, target name and all other
	 * attributes other than target must be the same.
	 * @param entries
	 * @return
	 */
	private boolean areMultiFeatures(List<GffThreeEntry> entries) {
		boolean isMultiFeature = true;
		for (int i = 0; i < entries.size(); i++) {
			for (int j = i+1; j < entries.size(); j++) {
				if (!(entries.get(i).isMultiFeature(entries.get(j)))) {
					return false;
				}				
			}
		}
		return isMultiFeature;
	}

	/**
	 * @param entryList
	 * @param list
	 * @param lineGffThreeEntryMap 
	 * @return
	 */
	private List<GffThreeEntry> findEntries(List<Long> list, HashMap<Long, GffThreeEntry> lineGffThreeEntryMap) {
		List<GffThreeEntry> notUniqueIdsList = new ArrayList<GffThreeEntry>();
		for (int i = 0; i < list.size(); i++) {
			long line = list.get(i);
			notUniqueIdsList.add(lineGffThreeEntryMap.get(line));
		}
		return notUniqueIdsList;
	}
}
