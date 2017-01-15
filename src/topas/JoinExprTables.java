package topas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import topas.Topas.TOPASModule;
import topas.parameters.JoinExprTablesParameters;

@TOPASModule(
		purpose = "join expression tables together (based on gene names)")

public class JoinExprTables {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(JoinExprTables.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		JoinExprTablesParameters.createInstance(args);

		String[] inputFiles = JoinExprTablesParameters.getInstance().getParameter("i").getValues();
		//		boolean iF = JoinExprTablesParameters.getInstance().getParameter("i").isPresent();
		String outputFile = JoinExprTablesParameters.getInstance().getParameter("o").toString();
		//		String inputDirectory = JoinExprTablesParameters.getInstance().getParameter("dir").toString();
		//		boolean iD = JoinExprTablesParameters.getInstance().getParameter("dir").isPresent();
		boolean htseq = JoinExprTablesParameters.getInstance().getParameter("htseq").isPresent();
		boolean nan = JoinExprTablesParameters.getInstance().getParameter("nan").isPresent();
		boolean commaIn = JoinExprTablesParameters.getInstance().getParameter("commaIn").isPresent();
		boolean commaOut = JoinExprTablesParameters.getInstance().getParameter("commaOut").isPresent();

		//		if (!iF && !iD) {
		//			System.err.println("No input files specified. Use '-i exprTable1 exprTable2 ...' xor '-dir directory'.");
		//			System.exit(1);
		//		}
		//		
		//		if (iF && iD) {
		//			System.err.println("Please either specify input files by paramater '-i exprTable1 exprTable2 ...' xor '-dir directory'.");
		//			System.exit(1);
		//		}

		System.out.println();

		System.out.println("Parameters chosen: ");
		System.out.println("Input files        : "+Arrays.toString(inputFiles));
		//		System.out.println("Input directory    : "+inputDirectory);
		System.out.println("Output file        : "+outputFile);
		System.out.println("HTSeq format       : "+htseq);
		System.out.println("Replace by NaNs    : "+nan);
		System.out.println("Input dots         : "+commaIn);
		System.out.println("Output dots        : "+commaOut);
		
		// parse all exprTableNames
		List<String> colNames = getColNames(inputFiles);
		String colNamesString = formatColNames(colNames);

		// parse all expression tables
		SortedSet<String> geneNames = new TreeSet<String>();
		double[] rowSums = new double[colNames.size()];
		List<HashMap<String, List<Double>>> exprTables = parseExprTables(inputFiles, geneNames, htseq, commaIn, rowSums);
		if (htseq) {
			geneNames.remove("__alignment_not_unique");
			geneNames.remove("__ambiguous");
			geneNames.remove("__no_feature");
			geneNames.remove("__not_aligned");
			geneNames.remove("__too_low_aQual");
			geneNames.remove("alignment_not_unique");
			geneNames.remove("ambiguous");
			geneNames.remove("no_feature");
			geneNames.remove("not_aligned");
			geneNames.remove("too_low_aQual");
			geneNames.remove("");
		}
		
		
		// write expression tables
		System.out.println();
		System.out.println("Writing expression tables to " + outputFile + ".");
		writeExprTables(outputFile, exprTables, geneNames, colNamesString, nan, commaOut);

		System.out.println();
		
		System.out.println(colNamesString);
		printRowSums(rowSums);
		System.out.println();

		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(JoinExprTables.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");

	}

	private static void printRowSums(double[] rowSums) {
		StringBuilder sB = new StringBuilder();
		String tab = "\t";
		for (double d : rowSums) {
			sB.append(tab);
			sB.append(d);
		}
		System.out.println(sB.toString());
	}

	private static String formatColNames(List<String> colNames) {
		String tab = "\t";
		StringBuilder sB = new StringBuilder();
		sB.append("");
		for (String colName : colNames) {
			sB.append(tab);
			sB.append(colName);
		}
		return sB.toString();
	}

	private static void writeExprTables(String outputFile,
			List<HashMap<String, List<Double>>> exprTables,
			SortedSet<String> geneNames, String colNamesString,
			boolean nan, boolean commaOut) throws IOException {
		BufferedWriter bW = new BufferedWriter(new FileWriter(outputFile));	
		String tab = "\t";
		String na = String.valueOf(Double.NaN);
		String zero = String.valueOf(0.0);
		String zeroComma = zero.replace('.', ',');
		bW.write(colNamesString);
		for (Iterator<String> iterator = geneNames.iterator(); iterator.hasNext();) {
			bW.newLine();
			String geneName = iterator.next();
			StringBuilder lineBuilder = new StringBuilder();
			lineBuilder.append(geneName);
			for (HashMap<String, List<Double>> exprTable : exprTables) {
				List<Double> exprValues = exprTable.get(geneName);
				if (exprValues != null) {
					for (Double exprValue : exprValues) {
						lineBuilder.append(tab);
						if (exprValue.equals(0.0) || exprValue.equals(Double.NaN)) {
							if (nan) {
								lineBuilder.append(na);
							} else {
								if (commaOut) {
									lineBuilder.append(zeroComma);
								} else {
									lineBuilder.append(zero);
								}								
							}
						} else {
							if (commaOut) {
								String exprValueComma = String.valueOf(exprValue).replace('.', ',');
								lineBuilder.append(exprValueComma);
							} else {
								lineBuilder.append(String.valueOf(exprValue));
							}							
						}
					}
				} else {
					Map.Entry<String, List<Double>> exprLine = exprTable.entrySet().iterator().next();
					int emptyVals = exprLine.getValue().size();
					for (int i = 0; i < emptyVals; i++) {
						lineBuilder.append(tab);
						if (nan) {
							lineBuilder.append(na);
						} else {
							if (commaOut) {
								lineBuilder.append(zeroComma);
							} else {
								lineBuilder.append(zero);
							}
						}
					}
				}
			}
			bW.write(lineBuilder.toString());
		}
		bW.close();
	}

	private static List<HashMap<String, List<Double>>> parseExprTables(
			String[] inputFiles, SortedSet<String> geneNames, boolean htseq,
			boolean commaIn, double[] rowSum) throws IOException {
		List<HashMap<String, List<Double>>> exprTables = new ArrayList<HashMap<String, List<Double>>>();
		for (String inputFile : inputFiles) {
			BufferedReader bR = new BufferedReader(new FileReader(inputFile));
			String line;
			boolean firstLine = false;
			HashMap<String, List<Double>> exprTable = new HashMap<String, List<Double>>();
			while ((line = bR.readLine()) != null) {
				if (!firstLine) {
					String[] lineSplit = line.split("\t");
					if (!lineSplit[0].equals("")) {
						addLineToExprTables(line, exprTable, geneNames, commaIn);
					}
					firstLine = true;
				} else {
					addLineToExprTables(line, exprTable, geneNames, commaIn);
				}
			}
			bR.close();
			if (htseq) {
				exprTable.remove("__no_feature");
				exprTable.remove("__ambiguous");
				exprTable.remove("__too_low_aQual");
				exprTable.remove("__not_aligned");
				exprTable.remove("__alignment_not_unique");
				exprTable.remove("no_feature");
				exprTable.remove("ambiguous");
				exprTable.remove("too_low_aQual");
				exprTable.remove("not_aligned");
				exprTable.remove("alignment_not_unique");
				exprTable.remove("");
			}

			exprTables.add(exprTable);
		}		
		// add sums of each row		
		for (int i = 0; i < exprTables.size(); i++) {
			HashMap<String, List<Double>> eT = exprTables.get(i);
			Collection<List<Double>> values = eT.values();
			double sum = 0.0;
			for (List<Double> dL : values) {
				for (Double d : dL) {
					sum += d;
				}
			}
			rowSum[i] = sum;
		}
		return exprTables;
	}

	private static void addLineToExprTables(String line,
			HashMap<String, List<Double>> exprTable,
			SortedSet<String> geneNames,
			boolean commaIn) {
		String[] lineSplit = line.split("\t");
		String key = lineSplit[0].trim();
		geneNames.add(key);
		List<Double> exprVals = new ArrayList<Double>();
		for (int i = 1; i < lineSplit.length; i++) {
			if (commaIn) {
				String exprVal = lineSplit[i];
				exprVal = exprVal.replace(',', '.');
				exprVals.add(Double.parseDouble(exprVal));
			} else {
				exprVals.add(Double.parseDouble(lineSplit[i]));
			}			
		}
		exprTable.put(key, exprVals);
	}

	private static List<String> getColNames(String[] inputFiles) throws IOException {
		List<String> colNames = new ArrayList<String>();
		for (String inputFile : inputFiles) {
			BufferedReader bR = new BufferedReader(new FileReader(inputFile));
			String line = bR.readLine();
			bR.close();
			String[] lineSplit = line.split("\t");
			if (lineSplit[0].equals("")) {
				for(int i = 1; i < lineSplit.length; i++) {
					colNames.add(lineSplit[i]);
				} 
			} else {
				File f = new File(inputFile);
				String[] pointSplit = f.getName().trim().split("\\.");
				String name = pointSplit[0];
				colNames.add(name);
			}
		}
		return colNames;
	}

}
