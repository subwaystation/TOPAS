package utils;

public class VerifyClassType {
	
	/**
	 * @param s
	 * @return
	 * true, if the given String could be parsed as an Integer
	 * false, if the given String could not be parsed as an Integer
	 */
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	/**
	 * @param s
	 * @return
	 * true, if the given String could be parsed as an Double
	 * false, if the given String could not be parsed as an Double
	 */
	public static boolean isDouble(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}
	
	/**
	 * @param s
	 * @return
	 * true, if the given String could be parsed as an Boolean
	 * false, if the given String could not be parsed as an Boolean
	 */
	public static boolean isBoolean(String s) {
	    try { 
	        Boolean.parseBoolean(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}

}
