package topas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import core.sequence.PrimaryBaser;
import topas.Topas.TOPASModule;
import topas.parameters.PrimaryBaseFastaParameters;

@TOPASModule(
		purpose = "crawl through"
				+ " every sequence in a fasta file and replace secondary"
				+ " bases with primary ones"
		)

public class PrimaryBaseFasta {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		long before = System.currentTimeMillis();

		PrimaryBaseFastaParameters.createInstance(args);

		String inputFile = PrimaryBaseFastaParameters.getInstance().getParameter("i").toString();
		String outputFile = PrimaryBaseFastaParameters.getInstance().getParameter("o").toString();

		// initialize BufferedWriter for log file
		BufferedWriter logWriter = new BufferedWriter(new FileWriter(outputFile + ".log"));

		System.out.println(PrimaryBaseFasta.class.getCanonicalName().toString());
		logWriter.write(PrimaryBaseFasta.class.getCanonicalName().toString());
		writeCurrentDate(logWriter);
		twoWrite(logWriter, "Use -? for help");

		twoWrite(logWriter, "Parameters chosen: ");
		twoWrite(logWriter, "Input FASTA file        : " + inputFile);
		twoWrite(logWriter, "Output FASTA file       : " + outputFile);
		
		performPrimaryBasing(inputFile, outputFile, logWriter);		

		twoWrite(logWriter, "");

		long now = System.currentTimeMillis();
		twoWrite(logWriter, "");
		twoWrite(logWriter, PrimaryBaseFasta.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
		logWriter.close();
	} // closing main part
	
	
	private static void performPrimaryBasing(String inputFile, String outputFile, BufferedWriter logWriter) throws IOException {
		PrimaryBaser primBaser = new PrimaryBaser("", -1);
		
		BufferedReader inputReader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));
		boolean firstLine = true;
		int lines = 0;
		String curId = "";
		for(String line = inputReader.readLine(); line != null; line = inputReader.readLine()) {
			lines++;
			// we have an identifier
			if (line.startsWith(">")) {
				if (firstLine) {
					firstLine = false;
				} else {
					outputWriter.newLine();
					twoWrite(logWriter, "Number of replaced Ns: " + primBaser.getNumNs());
					primBaser.setNumNs(0);
				}
				curId = line.substring(1);
				twoWrite(logWriter, "");
				twoWrite(logWriter, "Primary Basing " + curId + "...");
				outputWriter.write(line);
				// we obtain a sequence line
			} else {
				primBaser.setSequence(line);
				primBaser.setLines(lines);
				String seq = primBaser.primaryBaseSeqString();
				outputWriter.newLine();
				outputWriter.write(seq);
			}
		}
		twoWrite(logWriter, "Number of replaced Ns: " + primBaser.getNumNs());
		inputReader.close();
		outputWriter.close();
	}

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
