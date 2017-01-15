package topas;

import io.feature_format.GtfReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import feature_format.AFeatureFormatEntry;
import lib.normalize.CPM;
import lib.normalize.HTSeq;
import lib.normalize.RPKM;
import topas.Topas.TOPASModule;
import topas.parameters.JoinExprTablesParameters;
import topas.parameters.NormExprTableParameters;
import utils.VerifyClassType;

@TOPASModule(
		purpose = "normalize expression table")

public class NormExprTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(NormExprTable.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		NormExprTableParameters.createInstance(args);

		String inputFile = NormExprTableParameters.getInstance().getParameter("i").toString();
		String outputFile = NormExprTableParameters.getInstance().getParameter("o").toString();
		boolean cpm = NormExprTableParameters.getInstance().getParameter("cpm").isPresent();
		boolean rpkm = NormExprTableParameters.getInstance().getParameter("rpkm").isPresent();
		String gtf = NormExprTableParameters.getInstance().getParameter("gtf").toString();
		boolean gtfB = NormExprTableParameters.getInstance().getParameter("gtf").isPresent();
		String type = NormExprTableParameters.getInstance().getParameter("type").toString();
		boolean typeB = NormExprTableParameters.getInstance().getParameter("type").isPresent();
		String idAttr = NormExprTableParameters.getInstance().getParameter("idattr").toString();
		boolean idAttrB = NormExprTableParameters.getInstance().getParameter("idattr").isPresent();
		boolean commaIn = NormExprTableParameters.getInstance().getParameter("commaIn").isPresent();
		boolean htseq = NormExprTableParameters.getInstance().getParameter("htseq").isPresent();
		boolean commaOut = NormExprTableParameters.getInstance().getParameter("commaOut").isPresent();
		String h = NormExprTableParameters.getInstance().getParameter("header").toString();
		boolean headerB = false;
		if (VerifyClassType.isBoolean(h)) {
			headerB = Boolean.parseBoolean(h);
		} else  {
			System.err.println("Wrong argument for parameter 'header'. You entered " + h + " but only 'true' or 'false' are allowed.");
			System.exit(1);
		}
		

		if (cpm && rpkm) {
			System.err.println("Two normalization methods ('CPM' and 'RPKM') were chosen, but only one is allowed!"
					+ " Please either set parameter '-cpm' or '-rpkm'.");
			System.exit(1);
		}		
		if (!cpm && !rpkm) {
			System.err.println("No normalization method was chosen. Please either set parameter '-cpm' or '-rpkm'.");
			System.exit(1);
		}
		if (rpkm && !gtfB) {
			System.err.println("'RPKM' as normalization method was chosen, but you didn't specify a GTF-File. "
					+ "Please set parameter '-gtf <file.gtf>'.");
			if (!typeB) {
				System.err.println("'RPKM' as normalization method was chosen, but you didn't specify a feature type. "
						+ "Please set parameter '-type <String>'.");
			}
			if (!idAttrB) {
				System.err.println("'RPKM' as normalization method was chosen, but you didn't specify an attributes key. "
						+ "Please set parameter '-idattr <String>'.");
			}
			System.exit(1);
		}

		System.out.println();

		System.out.println("Parameters chosen: ");
		System.out.println("Input file        : "+inputFile);
		System.out.println("Output file       : "+outputFile);
		System.out.println("RPKM              : "+rpkm);
		if (rpkm) {
			System.out.println("GTF               : "+gtf);
			System.out.println("Type              : "+type);
			System.out.println("IdAtt             : "+idAttr);
		} else {
			System.out.println("CPM               : "+cpm);
		}
		System.out.println("Input comma       : "+commaIn);
		System.out.println("Output comma      : "+commaOut);
		System.out.println("Has Header        : "+headerB);
		System.out.println("HTSeq format       : "+htseq);

		System.out.println();

		if (cpm && gtfB) {
			System.out.println("'CPM' as normalization method was chosen and you specified a GTF-File. "
					+ "As this file is not needed, this parameter is ignored.");
			if (typeB) {
				System.out.println("'CPM' as normalization method was chosen and you specified a type. "
						+ "Parameter " + type + " will be ignored.");
			}
			if (idAttrB) {
				System.out.println("'CPM' as normalization method was chosen and you specified an attributes key. "
						+ "Parameter " + idAttr + " will be ignored.");
			}
		}

		System.out.println();
		System.out.println("Reading expression table from " + inputFile + "...");
		// parse all exprTableNames
		List<Integer> colsList = new ArrayList<Integer>();
		String header = getHeader(inputFile, colsList, headerB);
		int cols = colsList.get(0);

		// parse expression table
		// parse table, get geneNames in one list, get list of list of double values for exprTable,
		// get total reads in one list
		List<String> geneNames = new ArrayList<String>();
		List<Double> totalTagCounts = new ArrayList<Double>(cols);
		List<List<Double>> exprTable = parseExprTable(inputFile, header, geneNames, cols, totalTagCounts, commaIn, htseq);

		// normalize values in exprTable
		System.out.println();
		System.out.println("Normalizing...");
		if (cpm) {
			for (int i = 0; i < exprTable.size(); i++) {
				List<Double> exprValues = exprTable.get(i);
				Double totalTagCount = totalTagCounts.get(i);
				CPM.normList(exprValues, totalTagCount);
			}			
		} else {
			GtfReader gR = new GtfReader(gtf);
			System.out.println("Reading GTF file from " + gtf);
			HashMap<String, List<AFeatureFormatEntry>> gEMap = gR.getAttributesKeyMapFilterType(idAttr, type);
			System.out.println("Doing RPKM normalization...");
			for (int i = 0; i < exprTable.size(); i++) {
				List<Double> exprValues = exprTable.get(i);
				Double totalTagCount = totalTagCounts.get(i);
				HashMap<String, Double> transcriptSizes = calcTranscriptSizes(geneNames, gEMap);
				RPKM.normList(exprValues, gEMap, geneNames, totalTagCount, transcriptSizes);
			}
		}

		// write expression tables
		System.out.println();
		System.out.println("Writing normalized expression table to " + outputFile);
		writeExprTables(outputFile, exprTable, geneNames, header, commaOut);

		System.out.println();

		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(NormExprTable.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
	}

	private static void writeExprTables(String outputFile,
			List<List<Double>> exprTable, List<String> geneNames,
			String header, boolean commaOut) throws IOException {
		BufferedWriter bW = new BufferedWriter(new FileWriter(outputFile));
		boolean omitFirstNewLine = header.equals("");
		if (!header.equals("")) {
			bW.write(header);
			for (int i = 0; i < geneNames.size(); i++) {
				String geneName = geneNames.get(i);
				String row = formatRow(exprTable, i, commaOut);
				bW.newLine();				
				bW.write(geneName);
				bW.write(row);
			}
		} else {
			for (int i = 0; i < geneNames.size(); i++) {
				String geneName = geneNames.get(i);
				String row = formatRow(exprTable, i, commaOut);
				if (omitFirstNewLine) {
					omitFirstNewLine = false;
				} else {
					bW.newLine();
				}				
				bW.write(geneName);
				bW.write(row);
			}
		}
		bW.close();
	}

	private static String formatRow(List<List<Double>> exprTable, int i, boolean commaOut) {
		StringBuilder sB = new StringBuilder();
		String tab = "\t";
		for (List<Double> exprValues : exprTable) {
			sB.append(tab);
			if (commaOut) {
				String exprValue = exprValues.get(i).toString();
				exprValue = exprValue.replace('.', ',');
				sB.append(exprValue);
			} else {
				sB.append(exprValues.get(i));
			}			
		}
		return sB.toString();
	}

	private static String getHeader(String inputFile, List<Integer> cols, boolean hasHeader) throws IOException {
		String header = "";
		BufferedReader bR = new BufferedReader(new FileReader(inputFile));
		String line = bR.readLine();
		String[] lineSplit = line.split("\t");
		if (hasHeader) {
			header = line;
		}		
		line = bR.readLine();
		lineSplit = line.split("\t");
		cols.add(lineSplit.length-1);
		bR.close();
		return header;
	}

	private static List<List<Double>> parseExprTable(
			String inputFile, String header, List<String> geneNames,
			int cols, List<Double> totalTagCounts, boolean commaIn,
			boolean htseq) throws IOException {
		List<List<Double>> exprTable = new ArrayList<List<Double>>();
		for (int i = 0; i < cols; i++) {
			List<Double> lis = new ArrayList<Double>();
			exprTable.add(lis);
			totalTagCounts.add(0.0);
		}
		BufferedReader bR = new BufferedReader(new FileReader(inputFile));
		String line;
		boolean hasHeader = !header.equals("");
		while ((line = bR.readLine()) != null) {
			if (hasHeader) {
				hasHeader = false;
			} else {
				addLineToExprTables(line, exprTable, geneNames, totalTagCounts, commaIn, htseq);
			}
		}
		bR.close();
		return exprTable;
	}

	private static void addLineToExprTables(String line,
			List<List<Double>> exprTable, List<String> geneNames, List<Double> totalTagCounts,
			boolean commaIn, boolean htseq) {
		String[] lineSplit = line.split("\t");
		String geneName = lineSplit[0];
		// skip geneName that occurs in htseq if specified
		if (htseq) {
			if (HTSeq.BANNED.contains(geneName)) {
				// don't do anything with current gene name
				return;
			}
		}
		geneNames.add(geneName);
		for (int i = 1; i < lineSplit.length; i++) {
			Double exprValue = 0.0;
			if (commaIn) {
				String exprVal = lineSplit[i];
				exprVal = exprVal.replace(',', '.');
				exprValue = Double.parseDouble(exprVal);
			} else {
				exprValue = Double.parseDouble(lineSplit[i]);
			}			
			exprTable.get(i-1).add(exprValue);
			Double totalTagCount = totalTagCounts.get(i-1);
			if (exprValue.equals(Double.NaN)) {

			} else {
				totalTagCount += exprValue;
				totalTagCounts.set(i-1, totalTagCount);
			}
		}
	}
	
	private static HashMap<String, Double> calcTranscriptSizes(List<String> geneNames,
			HashMap<String, List<AFeatureFormatEntry>> gEMap) {
		HashMap<String, Double> transcriptSizes = new HashMap<String, Double>();
		for (String geneName : geneNames) {
			List<AFeatureFormatEntry> gEList = gEMap.get(geneName);
			double baseOfTranscript = 0.0;
			if (gEList != null) {
				for (AFeatureFormatEntry aFFE : gEList) {
					baseOfTranscript += aFFE.getFeatureLength();
				}
				transcriptSizes.put(geneName, baseOfTranscript);
			} else {
				System.out.println("Key '" + geneName + "' not found in GTF");
			}
		}
				
		return transcriptSizes;
	}
}
