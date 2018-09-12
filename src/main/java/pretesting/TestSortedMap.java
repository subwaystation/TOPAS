package pretesting;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


public class TestSortedMap {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
	static ArrayList<String> la = new ArrayList<String>();
	
	
	
	public static void main(String[] args) throws IOException {
		la.add("ID");
		la.add("Name");
		la.add("Gap");
		// TODO Auto-generated method stub
//		List<Map.Entry<String, String>> sortedAttributes = new LinkedList<Map.Entry<String, String>>();
		SortedMap<String, String> myMap = new TreeMap<String, String>(
				new Comparator<String>() {
            @Override
            public int compare(String e1key, String e2key) {
                return compareAttributes(e1key, e2key);
            }
        });
//		Dbxref=NCBI_GP:ABC98235.1;ID=cds1;Name=ABC98235.1;Parent=gene1;gbkey=CDS;product=conserved hypothetical protein;protein_id=ABC98235.1;transl_table=11
	    myMap.put("Dbxref", "cds1");
	    myMap.put("Parent", "Gene");
	    myMap.put("Name", "ABC98235.1");
	    myMap.put("ID", "NCBI_GP:ABC98235.1");
	    myMap.put("gbkey", "CDS");
	    myMap.put("product", "conserved hypothetical protein");
	    myMap.put("protein_id", "ABC98235.1");
	    myMap.put("transl_table", "11");
//	    sortedAttributes.addAll(myMap.entrySet());
//	    Collections.sort(sortedAttributes, 
//	            new Comparator<Map.Entry<String, String>>() {
//	                @Override
//	                public int compare(Map.Entry<String, String> e1,
//	                        Map.Entry<String, String> e2) {
//	                    return compareAttributes(e1.getKey(), e2.getKey());
//	                }
//	            });
	    //rtedAttributes.addAll(myMap.entrySet());
	    System.out.println(myMap);
	    
	}
	
	public static int compareAttributes(String a1, String a2) {
		int transA = transmuteAttributeToInt(a1);
		int transB = transmuteAttributeToInt(a2);
		
		if(transA == 12 && transB == 12) {
			return a1.compareTo(a2);
		}

		int returnValue = transA - transB;
		return returnValue;
		
	}

	private static int transmuteAttributeToInt(String a1) {
		if(a1.equals("ID")) {
			return 1;
		} 
		if (a1.equals("Name")) {
			return 2;
		}
		if (a1.equals("Alias")) {
			return 3;
		} 
		if (a1.equals("Parent")) {
			return 4;
		}
		if (a1.equals("Target")) {
			return 5;
		}
		if (a1.equals("Gap")) {
			return 6;
		}
		if (a1.equals("Derives_from")) {
			return 7;
		}
		if (a1.equals("Note")) {
			return 8;
		}
		if (a1.equals("Dbxref")) {
			return 9;
		}
		if (a1.equals("Ontology_term")) {
			return 10;
		}
		if (a1.equals("Is_circular")) {
			return 11;
		}
		return 12;
	}
	
	
}
