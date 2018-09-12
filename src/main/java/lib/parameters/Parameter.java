/**
 * @author battke
 * this class was taken from the PassageToolkit written by florian battke
 * for further details of this toolkit, see the following paper: http://subs.emis.de/LNI/Proceedings/Proceedings173/21.pdf
 */
package lib.parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parameter<T> {
	
	public final static String[] IS_PRESENT = new String[0];
	
	public final static Parser<Integer> INT_PARSER = new Parser<Integer>() {
		public Integer parse(String s) {
			try {
				return Integer.parseInt(s);
			} catch(Exception e) {}
			return null;
		}
	};
	
	public final static Parser<Double> DOUBLE_PARSER = new Parser<Double>() {
		public Double parse(String s) {
			try {
				return Double.parseDouble(s);
			} catch(Exception e) {}
			return null;
		}
	};
	
	public final static Parser<File> EXISTING_FILE_PARSER = new Parser<File>() {
		public File parse(String s) {
			File f = new File(s);
			if (!f.exists()) {
				System.err.println("File does not exist: "+s);
				return null;
			}
			if (f.isDirectory()) {
				System.err.println("File is a directory: "+s);
				return null;
			}
			return f;
		}
	};
	
	public final static Parser<File> DIRECTORY_PARSER = new Parser<File>() {
		public File parse(String s) {
			File f = new File(s);
			if (!f.exists())
				f.mkdirs();
			if (f.isDirectory())
				return f;
			else
				System.err.println("Not a directory: "+s);
			return null;
		}
	};
		
	public String identifier, error, help;	
	public int numberOfElements;
	public boolean allowMissing;
	private String[] value;
	private List<T> parsedValues;		
	
	protected Parser<T> parser;
	
	public interface Parser<T> {
		public T parse(String s);
	}
	

	/** Constructs a new parameter that may not be omitted */
	public Parameter(String id, int noe, String helpMessage, String errorMessage)  {
		this(id,noe,null, helpMessage,errorMessage);
	}
	
	/** Constructs a new parameter that may not be omitted and is parsed*/
	public Parameter(String id, int noe, Parser<T> parser, String helpMessage, String errorMessage)  {
		this(id, noe, false, parser, helpMessage, errorMessage, null);
	}
	
	/** Constructs a new parameter that may be omitted and is parsed*/
	public Parameter(String id, int noe, boolean allowMiss, Parser<T> parser, String helpMessage, String errorMessage)  {
		this(id, noe, allowMiss, parser, helpMessage, errorMessage, null);
	}
	
	/** Constructs a new parameter that may be omitted and is parsed*/
	public Parameter(String id, int noe, boolean allowMiss, String helpMessage, String errorMessage)  {
		this(id, noe, allowMiss, helpMessage, errorMessage, null);
	}
	
	/** Constructs a new parameter*/
	public Parameter(String id, int noe, boolean allowMiss, String helpMessage, String errorMessage, String[] defVal)  {
		this(id, noe, allowMiss, null, helpMessage, errorMessage, defVal);
	}

	
	/** Constructs a new parameter that is parsed*/
	public Parameter(String id, int noe, boolean allowMiss, Parser<T> parser, String helpMessage, String errorMessage, String[] defVal)  {
		identifier=id;
		numberOfElements = noe;
		allowMissing = allowMiss;
		error = errorMessage;
		help = helpMessage;
		this.parser=parser;
		setValues(defVal);
	}
	
	public boolean isPresent() {
		return value == IS_PRESENT || value!=null;
	}
	
	public String[] getValues() {
		return value;
	}
	
	public List<T> getParsedValues() {
		return parsedValues;
	}
	
	
	public T getParsedValue() {
		return parsedValues.get(0);
	}
	
	public boolean isOK() {
		return allowMissing || value!=null;
	}
	
	public boolean containsValue(String avalue) {
		for (String s : value)
			if (s.equals(avalue))
				return true;
		return false;
	}
	
	public boolean setValues(String[] values) {
		if (parser!=null && values!=null) {
			List<T> l = new ArrayList<T>();
			for (String s : values) {
				T val = parser.parse(s);
				if (val==null)
					return false;
				l.add(val);
			}
			parsedValues = l;
		}
		this.value = values;
		return true;
	}
	
	public String toString() {
		if (parser!=null && parsedValues!=null) {
			if (parsedValues.size()==1)
				return parsedValues.get(0).toString();
			return parsedValues.toString();
		}
		if (value==IS_PRESENT)
			return "yes";
		if (value==null && numberOfElements==0)
			return "no";
		if (value!=null) {
			if (value.length==1)
				return value[0].toString();
			return Arrays.toString(value);
		}
		return "--";
	}
}