package topas;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import utils.format_string.OutputStringFormatter;

public class Topas {

	@SuppressWarnings("rawtypes")
	public final static Class[] programs = new Class[]{
		ValidateFasta.class,
		CorrectFasta.class,
		IndexFasta.class,
		TabulateFasta.class,
		ExtractFasta.class,
		PrimaryBaseFasta.class,
		ValidateGFF3.class,
		FilterGFF3.class,
		SortGFF3.class,
		FormatFastq.class,
		ValidateFastq.class,
		IndexVCF.class,
		FilterVCF.class,
		AnnotateVCF.class,
		ConsensusSeqFromVCFs.class,
		AnalyseVcf.class,
		GenConS.class,
		JoinExprTables.class,
		NormExprTable.class,
		PhyCc.class,
	};

	@Retention(RetentionPolicy.RUNTIME)
	public @interface TOPASModule {
		String purpose();
	}	

	@SuppressWarnings("rawtypes")
	public static String getModuleName(Class module) {
		return module.getCanonicalName().substring(module.getCanonicalName().lastIndexOf(".")+1);
	}

	@SuppressWarnings("rawtypes")
	public static String getModulePurpose(Class module) {
		@SuppressWarnings("unchecked")
		Annotation annot = module.getAnnotation(TOPASModule.class);		
		if (annot!=null)
			return ((TOPASModule)annot).purpose();
		return "[ class does not contain ToolnameModule annotation ]";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		System.out.print("TOPAS - TOolkit for Processing and Annotation of Sequence data");

		// printBuildInfo();

		System.out.println();

		if (args.length>0) {
			// find tool and run it
			String toolname = args[0];
			toolname = toolname.toLowerCase();
			for (Class c : programs) {
				String cname = c.getCanonicalName();
				cname = cname.substring(cname.lastIndexOf('.')+1);
				cname = cname.toLowerCase();
				if (cname.equals(toolname)) {
					String[] subargs = Arrays.copyOfRange(args, 1, args.length);
					Method m = c.getDeclaredMethod("main", String[].class);
					m.invoke(null, (Object)subargs);
					System.exit(0);
				}
			}
		}
		
		System.out.println("Use \"java -jar topas.jar MODULE\" to start a specific program\n" +
				"and replace MODULE with");		
		
		int maxnamelen = 0;
		for (Class c : programs) {
			maxnamelen = Math.max(maxnamelen, getModuleName(c).length());
		}
		
		for (Class c : programs) {
			System.out.println("   "+OutputStringFormatter.align(getModuleName(c), 20) +"\t" +getModulePurpose(c));
		}

	}

}
