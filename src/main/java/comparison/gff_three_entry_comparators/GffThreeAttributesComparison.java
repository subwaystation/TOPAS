package comparison.gff_three_entry_comparators;

/**
 * @author heumos
 * a class providing methods for the comparison of gff-features 
 */
public class GffThreeAttributesComparison {
	
	/**
	 * @param a1
	 * @param a2
	 * @return compares two attributes
	 */
	public static int compareAttributes(String a1, String a2) {
		int transA = transmuteAttributeToInt(a1);
		int transB = transmuteAttributeToInt(a2);
		
		// given attributes are not predefined in gff3, so the internal compareTo method is required
		if(transA == 12 && transB == 12) {
			return a1.compareTo(a2);
		}
		// given attributes are predefined in gff3
		int returnValue = transA - transB;
		return returnValue;
	}

	/**
	 * @param a1
	 * @return int
	 * calculates for a given attribute the corresponding value for sorting
	 * this method represents the recommended order of attributes from http://www.sequenceontology.org/gff3.shtml
	 */
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
