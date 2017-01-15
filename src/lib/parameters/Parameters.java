/**
 * @author battke
 * this class was taken from the PassageToolkit written by florian battke
 * for further details of this toolkit, see the following paper: http://subs.emis.de/LNI/Proceedings/Proceedings173/21.pdf
 */

package lib.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Parameters {

	protected HashMap<String, Parameter> knownParameters = new HashMap<String, Parameter>();
	protected List<Parameter> sortedParameters = new ArrayList<Parameter>();
	
	protected List<String> catchAll = new LinkedList<String>(); //catches all parameters that nobody wants
	
	protected String usage="";
	
	public Parameters() {
		init();
	}
	
	public Parameters(String[] args) {
		init();
		parse(args);
	}
	
	protected abstract void init();

	public Parameter getParameter(String id) {
		return knownParameters.get(id);
	}
	
	protected void parse(String[] args) {
		parse(args, true);
	}
	
	protected void parse(String[] args, boolean mentionIgnored) {
		//maybe output help text
		for (String s : args) {
			if (s.equals("-?")) {
				if (usage!=null&&usage.length()>0)
					System.out.println(usage+"\n");
				for (Parameter p : sortedParameters) {
					System.out.println(p.help);
				}
				System.exit(0);
			}
		}
		
		for (int i=0; i!=args.length; /*manually incremented*/) {
			
			String cur = args[i];
			if (!cur.startsWith("-")) {
				catchAll.add(cur);
				++i;
				continue;
			}
			
			// get the argument identifier
			cur = cur.substring(1);
			// now collect arguments
			List<String> cargs = new LinkedList<String>();
			++i;
			while (i<args.length && (!args[i].startsWith("-") || args[i].contains(" "))) {
				cargs.add(args[i]);
				++i;
			}
			
			Parameter p = knownParameters.get(cur);
			if (p==null) {
				cur = cur.trim();
				if (cur.length()>0) {
					if (mentionIgnored) 
						System.out.println("Ignoring unknown parameter: "+cur);
					else {
						catchAll.add("-"+cur);
						catchAll.addAll(cargs);
					}
				}
				continue;
			}
			
			if (p.numberOfElements<0 || p.numberOfElements==cargs.size()) {
				if (p.numberOfElements==0)
					p.setValues(Parameter.IS_PRESENT);
				else
					if (!p.setValues(cargs.toArray(new String[0]))) {
						String def = p.getValues()!=null ? ", using default "+p:"";
						System.err.println("Parameter -"+p.identifier+": Incompatible arguments"+def);
						continue;
					}
			} else {
				System.err.println("Parameter -"+p.identifier+" needs "+p.numberOfElements+" arguments");				
				continue;
			}
		}
		
		// now check all params
		boolean ok=true;
		for (Parameter p : sortedParameters) {
			if (!p.isOK()) {
				System.err.println(p.error);
				ok = false;
			}
		}
		if (!ok) {
//			System.err.println("Please correct your input.");
			System.exit(1);
		}
	}
	
	protected void addParameter(Parameter p) {
		knownParameters.put(p.identifier, p);
		sortedParameters.add(p);
	}	
	
	public List<String> getUnclaimedParameters() {
		return catchAll;
	}
	
}
