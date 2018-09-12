package topas;

import io.fastq.FastqFormatter;

import java.io.IOException;

import topas.Topas.TOPASModule;
import topas.parameters.FormatFastqParameters;

@TOPASModule(
		purpose = "format the sequence string line(s) and the quality string line(s) of a fastq file to a certain length"
		)

public class FormatFastq {
	
	public static void main(String[] args) throws IOException {
		
		System.out.println(FormatFastq.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		FormatFastqParameters.createInstance(args);
		
		String inputFile = FormatFastqParameters.getInstance().getParameter("i").toString();
		String outputFile = FormatFastqParameters.getInstance().getParameter("o").toString();
		String format = null;
		boolean f = FormatFastqParameters.getInstance().getParameter("f").isPresent();
		if (f) {
			format = FormatFastqParameters.getInstance().getParameter("f").toString();
		} else {
			format = "infinity";
		}
		System.out.println();
		
		System.out.println("Parameters chosen: ");
		System.out.println("Input file           : "+inputFile);
		System.out.println("Output file          : "+outputFile);
		System.out.println("Format               : "+format);
		System.out.println();
		int lineLength;
		
		if (f) {
			lineLength = Integer.parseInt(format);
		} else {
			lineLength = -1;
		}
		
		FastqFormatter fF = new FastqFormatter(inputFile, outputFile, lineLength);
		fF.format();
		
		System.out.println();
		
		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(FormatFastq.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");

	}

}
