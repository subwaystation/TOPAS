package phy_cc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;


public class PhyCcBootstrapping {

	private static String N = "N";

	// the sample names
	private List<String> sampleNames;

	// the SNP table
	private Map<Integer, String[]> snpTable;

	// the columns of the LowCoverageSamples
	private List<Integer> cols;

	// the reference list of LowCoverageSamples
	private List<LowCovSample> referenceSamples;

	// the new list of LowCoveragSamples
	private List<LowCovSample> iterationSamples;

	// the number of iterations of the bootstrapping process
	private int iterations;

	// the output file of the results of the bootstrapping
	private String outputFile;

	// the number of positions in the snpTable
	private int positions;

	// the randomly selected positions in the snpTable
	private List<Integer> randomPosList;

	// agreeing samples names map
	private Map<String, List<String>> agreeingSamplesNamesMap;

	// number of agreements of rankings in comparison to reference samples
	// but only calculating on given agreeingSamplesNamesMap!
	private Map<String, Map<String, Double>> agreeingSamplesRankings;

	// number of agreements to report
	private int numberAgreements;
	
	// random object
	private Random random;
	
	// complete sample names
	private List<String> completeSampleNames;
	
	// selected sample indices
	private Set<Integer> selectedSamplesIndices;

	public PhyCcBootstrapping(Map<Integer, String[]> snpTable, List<Integer> cols, List<LowCovSample> referenceSamples,
			int iterations, String outputFile, List<String> sampleNames,
			Map<String, List<String>> agreeingSamplesNamesMap, int numberAgreeements,
			Random random, List<String> completeSampleNames, Set<Integer> selectedSamplesIndices) {
		this.snpTable = snpTable;
		this.cols = cols;
		this.referenceSamples = referenceSamples;
		// fillIterationSampleNames();
		this.iterations = iterations;
		this.outputFile = outputFile + ".bootstrap";
		this.positions = this.snpTable.size();
		this.randomPosList = new ArrayList<>();
		this.sampleNames = sampleNames;
		this.agreeingSamplesNamesMap = agreeingSamplesNamesMap;
		this.agreeingSamplesRankings = new HashMap<>();
		this.numberAgreements = numberAgreeements;
		this.random = random;
		this.completeSampleNames = completeSampleNames;
		this.selectedSamplesIndices = selectedSamplesIndices;
	}

	private void fillIterationSampleNames() {
		List<LowCovSample> iterationSamples = new ArrayList<>();
		for (LowCovSample lowCovSample : this.referenceSamples) {
			LowCovSample iterLowCovSample = new LowCovSample(lowCovSample.getName());
			for (String name : this.sampleNames) {
				if (!name.equals(iterLowCovSample.getName())) {
					iterLowCovSample.getNumPairingImpossibleCall().put(name, 0);
					iterLowCovSample.getAgreeingSnpMap().put(name, new ArrayList<SnpCall>());
				}
			}
			iterationSamples.add(iterLowCovSample);
		}
		this.iterationSamples = iterationSamples;
	}

	private void calcRandomPosList() {
		for (int i = 0; i < this.positions; i++) {
			int randInt = this.random.nextInt(this.positions) + 1;
			this.randomPosList.add(randInt);
		}
	}

	private void reset() {
		this.iterationSamples.clear();
		this.randomPosList.clear();
	}

	public void performBootstrap() {
		// calcReferenceSamplesRanking();
		for (int iter = 0; iter < this.iterations; iter++) {
			if (iter % 100 == 0) {
				if (iter != 0) {
					System.out.println("ITERATION STEP: " + iter);
				}
			}
			// generate random positions
			calcRandomPosList();

			fillIterationSampleNames();
			parseSnpTableIntoAgreements();

			for (int i = 0; i < this.iterationSamples.size(); i++) {
				LowCovSample lowCovSample = this.iterationSamples.get(i);
				// calc z-scores
				lowCovSample.calcZStarScores(this.positions);
				// calc ranking
				lowCovSample.calcRandomRanking();
				// compare to ref ranking
				LowCovSample refLowCovSample = this.referenceSamples.get(i);
				compareRanking(lowCovSample, refLowCovSample);
			}
			this.reset();
		}
	}

	public void writeBootstrapResults() throws IOException {
		// write results
		BufferedWriter resultWriter = new BufferedWriter(new FileWriter(this.outputFile + ".tsv"));
		resultWriter.write("##BOOTSTRAP RESULTS: Percentages of agreeing Z-Score rankings per low coverage sample.");
		resultWriter.write("\n");
		resultWriter.write("\n");
		for (Map.Entry<String, List<String>> entry : this.agreeingSamplesNamesMap.entrySet()) {
			String sampleName = entry.getKey();
			List<String> sampleNames = this.agreeingSamplesNamesMap.get(sampleName);
			double alternativeAverage = 0.0;
			for (String sample : sampleNames) {
				if (this.agreeingSamplesRankings.get(sampleName) != null) {
					if (this.agreeingSamplesRankings.get(sampleName).get(sample) != null) {
						alternativeAverage += this.agreeingSamplesRankings.get(sampleName).get(sample);
					}
				}
			}
			resultWriter.write(sampleName);
			resultWriter.write("# AVERAGE: ");
			alternativeAverage = 100 * alternativeAverage / (double) (this.iterations);
			alternativeAverage = alternativeAverage / ((double) this.sampleNames.size() - 1);
			resultWriter.write(String.format("%.2f",alternativeAverage));
			for (int i = 0; i < this.numberAgreements; i++) {
				String sample = sampleNames.get(i);
				resultWriter.write("\n");
				resultWriter.write(sample);
				resultWriter.write(": ");
				if (this.agreeingSamplesRankings.get(sampleName) != null) {
					if (this.agreeingSamplesRankings.get(sampleName).get(sample) != null) {
						resultWriter
								.write(String.format("%.2f",(100.0 * (this.agreeingSamplesRankings.get(sampleName).get(sample)
										/ (double) this.iterations))));
					} else {
						resultWriter.write(String.valueOf(0.0));
					}
				} else {
					resultWriter.write(String.valueOf(0.0));
				}
			}
			resultWriter.write("\n");
			resultWriter.write("\n");
		}
		resultWriter.close();
	}

	private void compareRanking(LowCovSample lowCovSample, LowCovSample refLowCovSample) {
		// calc ranking stuff for every sample name for average bootstrap value
		Map<String, Integer> rankingMap = lowCovSample.getRanking();
		Map<String, Integer> refRankingMap = refLowCovSample.getRanking();

		// FIXME
		/**
		 * Printouts for validation.
		 */
//		SortedMap<String, Integer> sortedRankingMap = new TreeMap<>(rankingMap);
//		System.out.println("NEW: " + sortedRankingMap);
//		SortedMap<String, Double> sortedZScores = new TreeMap<>(lowCovSample.getZStarScores());
//		System.out.println("NEW: " + sortedZScores);
//		SortedMap<String, Integer> sortedRefRankingMap = new TreeMap<>(refRankingMap);
//		System.out.println("REF: " + sortedRefRankingMap);
//		SortedMap<String, Double> sortedRefZScores = new TreeMap<>(refLowCovSample.getZStarScores());
//		System.out.println("REF: " + sortedRefZScores);
		// FIXME
		String lowCovSampleName = lowCovSample.getName();

		// calc ranking stuff for only given sample names
		List<String> sampleNames = this.agreeingSamplesNamesMap.get(lowCovSampleName);
		for (String sample : sampleNames) {
			Integer rank = rankingMap.get(sample);
			Integer refRank = refRankingMap.get(sample);
			if (refRank.equals(rank)) {
				if (this.agreeingSamplesRankings.get(lowCovSampleName) != null) {
					Map<String, Double> rankingCountSamples = this.agreeingSamplesRankings.get(lowCovSampleName);
					if (rankingCountSamples.get(sample) != null) {
						Double count = rankingCountSamples.get(sample);
						rankingCountSamples.put(sample, count + 1);
					} else {
						rankingCountSamples.put(sample, 1.0);
					}
				} else {
					Map<String, Double> rankingCountSamples = new HashMap<>();
					rankingCountSamples.put(sample, 1.0);
					this.agreeingSamplesRankings.put(lowCovSampleName, rankingCountSamples);
				}
			}
		}
	}

	/**
	 * 
	 */
	private void parseSnpTableIntoAgreements() {
		PhyCcClassificator phyCcClassificator = new PhyCcClassificator(null, null);
		// parse into agreements
		for (int randPos : this.randomPosList) {
			String[] lineSplit = this.snpTable.get(randPos);
			int i = 0;
			for (int col : this.cols) {
				LowCovSample lowCovSample = this.iterationSamples.get(i);
				String call = lineSplit[col];
				phyCcClassificator.setCall(call);
				phyCcClassificator.setLowCovSample(lowCovSample);
				phyCcClassificator.performClassification();
				i++;
				boolean isUniqueSnpCall = true;
				for (int j = 2; j < lineSplit.length; j++) {
					// check if sample was selected in samples file
					Integer jInt = new Integer(j);
					if (!this.selectedSamplesIndices.isEmpty() && !this.selectedSamplesIndices.contains(jInt)) {
						continue;
					}
					// we don't want to see the current one
					if (j == col) {
						continue;
					} else {
						String otherCall = lineSplit[j];
						// we have an agreement
						if (PhyCcTsvCrawler.checkAgreement(call, otherCall) && !call.equals(N)) {
							isUniqueSnpCall = false;
							String pos = lineSplit[0];
							String ref = lineSplit[1];
							char snp = otherCall.charAt(0);
							boolean safe = false;
							if (Character.isUpperCase(snp)) {
								safe = true;
							}
							SnpCall snpCall = new SnpCall(this.completeSampleNames.get(j - 2), pos, ref, otherCall, safe);
							lowCovSample.addAgreeingSnp(this.completeSampleNames.get(j - 2), snpCall);
							// no agreement, but check for "N"
						} else if (call.equals(N) || otherCall.equals(N)) {
							lowCovSample.addPairingImpossibleCall(this.completeSampleNames.get(j - 2));
						}
					}
				}
				// we have a unique snp call
				if (isUniqueSnpCall && !call.equals(N) && !call.equals("R") && !call.equals(".")) {
					String pos = lineSplit[0];
					String ref = lineSplit[1];
					char snp = call.charAt(0);
					boolean safe = false;
					if (Character.isUpperCase(snp)) {
						safe = true;
					}
					SnpCall snpCall = new SnpCall(lowCovSample.getName(), pos, ref, call, safe);
					lowCovSample.addUniqueSnpCall(snpCall);
				}
			}
		}
	}

}
