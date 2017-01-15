package utils.identifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IdentifiersUtils {
	
	/**
	 * @param sequenceIds
	 * @return
	 */
	public static List<String> calcNotUniqueIdentifiersFast(HashMap<String, List<Integer>> sequenceIds) {
		
		List<String> notUniqueIdentifiersList = new ArrayList<String>();
		
		String notUniqueIdentifer = "[lines ";
		
		for (List<Integer> l : sequenceIds.values()) {
		    if (l.size() > 1) {
		    	for (int j = 0; j < l.size(); j++) {
					notUniqueIdentifer += l.get(j).toString() + "]" + " Identifiers are not unique!";
					notUniqueIdentifiersList.add(notUniqueIdentifer);
					notUniqueIdentifer = "[lines ";
		    	}
		    }
		}
		return notUniqueIdentifiersList;
	}
	
	public static List<String> calcNotUniqueIdentifiers(List<IdentifierLine> identifiers) {		
		List<String> notUniqueIdentifiersList = new ArrayList<String>();
		Map<String, List<Integer>> indexList = new HashMap<String, List<Integer>>();
		List<List<Integer>> indexesList = new ArrayList<List<Integer>>();
		for (int i = 0; i < identifiers.size(); i++) {
			String currentString = identifiers.get(i).getIndentifier();
			List<Integer> indexes = indexList.get(currentString);
			if (indexes == null) {
				indexList.put(currentString, indexes = new LinkedList<Integer>());
			}
			indexes.add(identifiers.get(i).getLine());
			// found duplicate, do what you like
			if (indexes.size() > 1) {
				indexesList.add(indexes);
			}
		}
		// remove duplicates out of indexesList
		HashSet<List<Integer>> hashSet = new HashSet<List<Integer>>();
		hashSet.addAll(indexesList);
		indexesList.clear();
		indexesList.addAll(hashSet);
		String notUniqueIdentifer = "[lines ";
		for(int j = 0; j < indexesList.size(); j++) {
			notUniqueIdentifer += indexesList.get(j).toString() + "]" + " Identifiers are not unique!";
			notUniqueIdentifiersList.add(notUniqueIdentifer);
			notUniqueIdentifer = "[lines ";
		}
		return notUniqueIdentifiersList;
	}

}
