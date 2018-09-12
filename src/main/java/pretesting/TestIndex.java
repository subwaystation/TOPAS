package pretesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestIndex {

	public static void main(String[] args) {
		
		List<String> yourList = new ArrayList<String>();
		yourList.add("a");
		yourList.add("b");
		yourList.add("b");
		yourList.add("a");
		yourList.add("c");
		yourList.add("d");
		
		Map<String, List<Integer>> indexList = new HashMap<String, List<Integer>>();
		for (int i = 0; i < yourList.size(); i++) {
		    String currentString = yourList.get(i);
		    List<Integer> indexes = indexList.get(currentString);
		    if (indexes == null) {
		         indexList.put(currentString, indexes = new LinkedList<Integer>());
		    }
		    indexes.add(i);
		    if (indexes.size() > 1) {
		        // found duplicate, do what you like
		    	System.out.println(indexes.toString());
		    }
		}
		// if you skip the last if in the for loop you can do this:
		for (String string : indexList.keySet()) {
		    if (indexList.get(string).size() > 1) {
		        // String string has multiple occurences
		        // List of corresponding indexes:
		        List<Integer> indexes = indexList.get(string);
		        // do what you want
		    }
		}

	}

}
