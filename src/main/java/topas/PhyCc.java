package topas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import phy_cc.LowCovSample;
import phy_cc.PhyCcBootstrapping;
import phy_cc.PhyCcResultWriter;
import phy_cc.PhyCcTsvCrawler;
import topas.Topas.TOPASModule;
import topas.parameters.PhyCcParameters;
import utils.VerifyClassType;

@TOPASModule(
		purpose = "crawl through"
				+ " a given SNP table in tsv format and calculate simple statistics "
		)

public class PhyCc {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		long before = System.currentTimeMillis();

		PhyCcParameters.createInstance(args);

		String inputFile = PhyCcParameters.getInstance().getParameter("i").toString();
		String outputFile = PhyCcParameters.getInstance().getParameter("o").toString();
		String colsString = PhyCcParameters.getInstance().getParameter("cols").toString();
		String numAgreements = PhyCcParameters.getInstance().getParameter("num_agreements").toString();
		boolean bootstrappingB = PhyCcParameters.getInstance().getParameter("bootstrapping").isPresent();
		String bootstrapping = PhyCcParameters.getInstance().getParameter("bootstrapping").toString();
		boolean samplesFileB = PhyCcParameters.getInstance().getParameter("selected_samples").isPresent();
		String samplesFile = PhyCcParameters.getInstance().getParameter("selected_samples").toString();

		// initialize BufferedWriter for log file
		outputFile = shapeOutputFile(outputFile);
		BufferedWriter logWriter = new BufferedWriter(new FileWriter(outputFile + ".log"));

		System.out.println(PhyCc.class.getCanonicalName().toString());
		logWriter.write(PhyCc.class.getCanonicalName().toString());
		writeCurrentDate(logWriter);
		twoWrite(logWriter, "Use -? for help");

		twoWrite(logWriter, "Parameters chosen: ");
		twoWrite(logWriter, "Input TSV SNP file        : " + inputFile);
		twoWrite(logWriter, "Result file               : " + outputFile);
		twoWrite(logWriter, "Low coverage columns      : " + colsString);
		twoWrite(logWriter, "Number agreements         : " + numAgreements);
		if (bootstrappingB) {
			twoWrite(logWriter, "Bootstrapping:            : " + bootstrapping);
		}
		if (samplesFileB) {
			twoWrite(logWriter, "Samples File:             : " + samplesFile);
		}

		twoWrite(logWriter, "");
		
		// parse low coverage sample columns
		List<Integer> cols = parseCols(colsString, inputFile);
		
		Set<String> selectedSamples = new HashSet<>();
		
		if (samplesFileB) {
			selectedSamples = parseSamplesFile(samplesFile);
		}
		
		// input
		Random random = new Random();
		PhyCcTsvCrawler phyCcTsvCrawler = new PhyCcTsvCrawler(inputFile, cols, bootstrappingB, selectedSamples);
		List<LowCovSample> lowCovSamples = phyCcTsvCrawler.crawlTsv();
		int calls = phyCcTsvCrawler.getCalls();
		
		// output
		PhyCcResultWriter phyCcResultWriter = new PhyCcResultWriter(outputFile, lowCovSamples, Integer.valueOf(numAgreements), calls);
		phyCcResultWriter.writeResult();
		
		// bootstrapping?
		if (bootstrappingB) {
			System.out.println();
			System.out.println("Performing Bootstrapping...");
			phyCcResultWriter.calcAgreeingSampleNames();
			Map<String, List<String>> agreeingSamplesNamesMap = phyCcResultWriter.getAgreeingSamplesNamesMap();
			PhyCcBootstrapping phyCcBootstrapping = new PhyCcBootstrapping(phyCcTsvCrawler.getSnpTable(),
					cols, lowCovSamples, Integer.parseInt(bootstrapping), outputFile,
					phyCcTsvCrawler.getSampleNames(), agreeingSamplesNamesMap, Integer.valueOf(numAgreements),
					random, phyCcTsvCrawler.getCompleteSampleNames(), phyCcTsvCrawler.getSelectedSamplesIndices());
			phyCcBootstrapping.performBootstrap();
			phyCcBootstrapping.writeBootstrapResults();
		}

		long now = System.currentTimeMillis();
		twoWrite(logWriter, "");
		twoWrite(logWriter, PhyCc.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
		logWriter.close();
	} // closing main part
	
	private static Set<String> parseSamplesFile(String samplesFile) throws IOException {
		Set<String> samplesSet = new HashSet<>();
		BufferedReader bR = new BufferedReader(new FileReader(samplesFile));
		String line = "";
		while ((line = bR.readLine()) != null) {
			String[] lineSplit = line.split(" ");
			if (!line.isEmpty()) {
				if (lineSplit[1].trim().equals("1")) {
					samplesSet.add(lineSplit[0].trim());
				}
			}
		}
		bR.close();
		return samplesSet;
	}

	private static String shapeOutputFile(String outputFile) {
		if (!outputFile.endsWith(".phycc")) {
			outputFile = outputFile + ".phycc";
		}
		return outputFile;
	}

	private static List<Integer> parseCols(String colsString, String inputFile) throws IOException {
		// read header line
		BufferedReader bR = new BufferedReader(new FileReader(inputFile));
		String header = bR.readLine();
		bR.close();
		String[] headerSplit = header.split("\t");
		
		String[] colStringSplit = colsString.split(",");
		Set<Integer> cols = new HashSet<Integer>();
		for (String s : colStringSplit) {
			s = s.trim();
			// check if string is integer or really a string
			if (VerifyClassType.isInteger(s)) {
				cols.add(Integer.parseInt(s)-1);
				// find column number in header
			} else {
				// no ":" allowed in name!
				String[] colonSplit = s.split(":");
				if (colonSplit.length > 1) {
					int minIndex = Integer.parseInt(colonSplit[0]);
					int maxIndex = Integer.parseInt(colonSplit[1]);
					for (int i = minIndex; i < maxIndex + 1; i++) {
						cols.add(i-1);
					}
				} else {
					boolean added = false;
					for (int i = 2; i < headerSplit.length; i++) {
						String colName = headerSplit[i].trim();
						if (colName.equals(s)) {
							cols.add(i);
							added = true;
							break;
						}
					}
					if (!added) {
						System.out.println("[WARNING]: Given sample name '" + s
								+ "' was not found in the given file.");
					}
				}
			}
		}
		return new ArrayList<Integer>(cols);
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
