package template;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import topas.Topas;
import topas.Topas.TOPASModule;

@TOPASModule(
		purpose = "crawl through"
				+ " every sequence in a fasta file and replace secondary"
				+ " bases with primary ones"
		)

public class Template {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		long before = System.currentTimeMillis();

		TemplateParameters.createInstance(args);

		String inputFile = TemplateParameters.getInstance().getParameter("i").toString();
		String outputFile = TemplateParameters.getInstance().getParameter("o").toString();

		// initialize BufferedWriter for log file
		BufferedWriter logWriter = new BufferedWriter(new FileWriter(outputFile + ".log"));

		System.out.println(Template.class.getCanonicalName().toString());
		logWriter.write(Template.class.getCanonicalName().toString());
		writeCurrentDate(logWriter);
		twoWrite(logWriter, "Use -? for help");

		twoWrite(logWriter, "Parameters chosen: ");
		twoWrite(logWriter, "Input FASTA file        : " + inputFile);
		twoWrite(logWriter, "Output FASTA file       : " + outputFile);

		twoWrite(logWriter, "");

		long now = System.currentTimeMillis();
		twoWrite(logWriter, "");
		twoWrite(logWriter, Template.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
		logWriter.close();
	} // closing main part
	
	private static void twoWrite(BufferedWriter logWriter, String string) throws IOException {
		System.out.println(string);
		logWriter.newLine();
		logWriter.write(string);
	}
	
	private static void writeCurrentDate(BufferedWriter logWriter) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//get current date time with Date()
		Date date = new Date();
		twoWrite(logWriter, dateFormat.format(date));
	}

}
