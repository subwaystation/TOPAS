package phy_cc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import comparison.MapListSizeComparatorDesc;

public class PhyCcResultWriter {
	
	private String outputFile;
	
	private List<LowCovSample> lowCovSamples;
	
	private int numberAgreements;
	
	private int positions;
	
	// the agreeing samples given by "numberAgreements"
	private Map<String, List<String>> agreeingSamplesNamesMap;
	
	public PhyCcResultWriter(String outputFile, List<LowCovSample> lowCovSamples, int numberAgreements, int positions) {
		this.outputFile = outputFile;
		this.lowCovSamples = lowCovSamples;
		this.numberAgreements = numberAgreements;
		this.positions = positions;
	}
	
	public void writeResult() throws IOException {
		BufferedWriter resultWriter = new BufferedWriter(new FileWriter(this.outputFile + ".tsv"));
		writeResultHeader(resultWriter);
		String tab = "\t";
		StringBuilder lineBuilder = new StringBuilder();
		// write out table
		writeTable(resultWriter, tab, lineBuilder);
		resultWriter.write("\n");
		resultWriter.close();
		
		// write out unique SNPs
		resultWriter = new BufferedWriter(new FileWriter(this.outputFile + ".unique_snps.tsv"));
		writeUniqueSnps(resultWriter, tab, lineBuilder);
		resultWriter.close();
		
		// write out detailed agreeing positions into new file
		resultWriter = new BufferedWriter(new FileWriter(this.outputFile + ".agr_unnorm.tsv"));
		writeAgreeingCallsUnNorm(resultWriter, tab, lineBuilder);
		resultWriter.close();
		lineBuilder.setLength(0);
		
		// write out detailed agreeing positions normalized into new file
		resultWriter = new BufferedWriter(new FileWriter(this.outputFile + ".agr_norm.tsv"));
		writeAgreeingCallsNorm(resultWriter, tab, lineBuilder);
		
		resultWriter.close();
	}
	
	public void calcAgreeingSampleNames() {
		Map<String, List<String>> agreeingSampleNamesMap = new HashMap<>();
		for (LowCovSample lowCovSample : this.lowCovSamples) {
			String name = lowCovSample.getName();
			Map<String, Double> zStarScoresMap = lowCovSample.getZStarScores();
			SortedMap<String, Double> sortedAgreeingMap = new TreeMap<>(
					new SamplePairingNormComparator(zStarScoresMap));
			sortedAgreeingMap.putAll(zStarScoresMap);
			for (Map.Entry<String, Double> entry : sortedAgreeingMap.entrySet()) {
				String sampleName = entry.getKey();
				if (agreeingSampleNamesMap.get(name) != null) {
					List<String> sampleNames = agreeingSampleNamesMap.get(name);
					sampleNames.add(sampleName);
				} else {
					List<String> sampleNames = new ArrayList<>();
					sampleNames.add(sampleName);
					agreeingSampleNamesMap.put(name, sampleNames);
				}
			}
		}
		this.agreeingSamplesNamesMap = agreeingSampleNamesMap;
	}

	public Map<String, List<String>> getAgreeingSamplesNamesMap() {
		return agreeingSamplesNamesMap;
	}

	public void setAgreeingSamplesNamesMap(Map<String, List<String>> agreeingSamplesNamesMap) {
		this.agreeingSamplesNamesMap = agreeingSamplesNamesMap;
	}

	private void writeAgreeingCallsNorm(BufferedWriter resultWriter, String tab, StringBuilder lineBuilder) throws IOException {
		for (LowCovSample lowCovSample : this.lowCovSamples) {
			String name = lowCovSample.getName();
			Map<String, Double> zStarScoresMap = lowCovSample.getZStarScores();
			SortedMap<String, Double> sortedAgreeingMap = new TreeMap<>(new SamplePairingNormComparator(zStarScoresMap));
			sortedAgreeingMap.putAll(zStarScoresMap);
			int i = 0;
			for (Map.Entry<String, Double> entry: sortedAgreeingMap.entrySet()) {
				if (i < this.numberAgreements) {
					String sampleName = entry.getKey();
					List<SnpCall> agreeingList = lowCovSample.getAgreeingSnpMap().get(sampleName);
					lineBuilder.append("##AGREEING_POS " + name + "__" + sampleName + ": " + String.format("%.2f", entry.getValue()) + "\n");
					lineBuilder.append("#POS").append(tab).append("#REF").append(tab).append("#BASE").append("\n");
					for (SnpCall snpCall : agreeingList) {
						lineBuilder
						.append(snpCall.getPos()).append(tab)
						.append(snpCall.getRef()).append(tab)
						.append(snpCall.getSnp()).append(tab).append("\n");
					}
					i++;
				} else {
					lineBuilder.append("\n");
					break;
				}
			}
		}
		resultWriter.write(lineBuilder.toString());
	}

	/**
	 * @param resultWriter
	 * @param tab
	 * @param lineBuilder
	 * @throws IOException
	 */
	private void writeAgreeingCallsUnNorm(BufferedWriter resultWriter, String tab, StringBuilder lineBuilder)
			throws IOException {
		for (LowCovSample lowCovSample : this.lowCovSamples) {
			String name = lowCovSample.getName();
			Map<String, List<SnpCall>> agreeingSnpMap = lowCovSample.getAgreeingSnpMap();
			SortedMap<String, List<SnpCall>> sortedAgreeingMap = new TreeMap<>(new MapListSizeComparatorDesc(agreeingSnpMap));
			sortedAgreeingMap.putAll(agreeingSnpMap);
			int i = 0;
			for (Map.Entry<String, List<SnpCall>> entry: sortedAgreeingMap.entrySet()) {
				if (i < this.numberAgreements) {
					String sampleName = entry.getKey();
					List<SnpCall> agreeingList = entry.getValue();
					lineBuilder.append("##AGREEING_POS " + name + "__" + sampleName + ":" + agreeingList.size() + "\n");
					lineBuilder.append("#POS").append(tab).append("#REF").append(tab).append("#BASE").append("\n");
					for (SnpCall snpCall : agreeingList) {
						lineBuilder
						.append(snpCall.getPos()).append(tab)
						.append(snpCall.getRef()).append(tab)
						.append(snpCall.getSnp()).append(tab).append("\n");
					}
					i++;
				} else {
					lineBuilder.append("\n");
					break;
				}
			}
		}
		resultWriter.write(lineBuilder.toString());
	}

	/**
	 * @param resultWriter
	 * @param tab
	 * @param lineBuilder
	 * @throws IOException
	 */
	private void writeTable(BufferedWriter resultWriter, String tab, StringBuilder lineBuilder) throws IOException {
		for (LowCovSample lowCovSample : this.lowCovSamples) {
			lineBuilder.append("\n");
			lineBuilder.append(lowCovSample.getName()).append(tab);
			lineBuilder.append(lowCovSample.getNumSafeRef() + lowCovSample.getNumSafeSnps()).append(tab);
			lineBuilder.append(lowCovSample.getNumUnsaveRef() + lowCovSample.getNumUnsaveSnps()).append(tab);
			lineBuilder.append(lowCovSample.getNumSafeSnps() + lowCovSample.getNumUnsaveSnps()).append(tab);
			lineBuilder.append(lowCovSample.getUniqueSnpList().size()).append(tab);
			appendUnsafeUniqueCallsPercent(lineBuilder, lowCovSample);
			appendMostAgreeingSamples(lineBuilder, lowCovSample, tab);
			
			// filter out samples with N
			appendMostAgreeingSamplesNorm(lineBuilder, lowCovSample);
		}
		resultWriter.write(lineBuilder.toString());
		lineBuilder.setLength(0);
	}

	private void appendMostAgreeingSamplesNorm(StringBuilder lineBuilder, LowCovSample lowCovSample) {
		Map<String, Double> zStarScoresMap = lowCovSample.getZStarScores();
		SortedMap<String, Double> sortedAgreeingMap = new TreeMap<>(new SamplePairingNormComparator(zStarScoresMap));
		sortedAgreeingMap.putAll(zStarScoresMap);
		int i = 0;
		for (Map.Entry<String, Double> entry: sortedAgreeingMap.entrySet()) {
			if (i < this.numberAgreements) {
				String sampleName = entry.getKey();
				Double normVal = entry.getValue();
				if (i != 0) {
					lineBuilder.append("\t");
				}
				lineBuilder.append(sampleName);
				lineBuilder.append(":");
				lineBuilder.append(String.format("%.2f", normVal));
				i++;
			} else {
				break;
			}
		}
	}

	/**
	 * @param resultWriter
	 * @param tab
	 * @param lineBuilder
	 * @throws IOException
	 */
	private void writeUniqueSnps(BufferedWriter resultWriter, String tab, StringBuilder lineBuilder)
			throws IOException {
		for (LowCovSample lowCovSample : this.lowCovSamples) {
			String name = lowCovSample.getName();
			List<SnpCall> uniqueSnps = lowCovSample.getUniqueSnpList();
			if (uniqueSnps.size() >= 1) {
				lineBuilder.append("##UNIQUE_SNP " + name + "\n");
				lineBuilder.append("#SAMPLE").append(tab).append("#POS").append(tab).append("#REF").append(tab).append("#SNP").append("\n");
				for (SnpCall snpCall : uniqueSnps) {
					lineBuilder.append(snpCall.getSampleName()).append(tab)
					.append(snpCall.getPos()).append(tab)
					.append(snpCall.getRef()).append(tab)
					.append(snpCall.getSnp()).append(tab).append("\n");
				}
				lineBuilder.append("\n");
			}
		}
		resultWriter.write(lineBuilder.toString());
		lineBuilder.setLength(0);
	}

	private void appendMostAgreeingSamples(StringBuilder lineBuilder, LowCovSample lowCovSample, String tab) {
		Map<String, List<SnpCall>> agreeingSnpMap = lowCovSample.getAgreeingSnpMap();
		SortedMap<String, List<SnpCall>> sortedAgreeingMap = new TreeMap<>(new MapListSizeComparatorDesc(agreeingSnpMap));
		sortedAgreeingMap.putAll(agreeingSnpMap);
		int i = 0;
		for (Map.Entry<String, List<SnpCall>> entry: sortedAgreeingMap.entrySet()) {
			if (i < this.numberAgreements) {
				String sampleName = entry.getKey();
				List<SnpCall> agreeingList = entry.getValue();
				if (i != 0) {
					lineBuilder.append(tab);
				}
				lineBuilder.append(sampleName);
				lineBuilder.append(":");
				lineBuilder.append(agreeingList.size());
				i++;
			} else {
				break;
			}
			
		}
		lineBuilder.append("\t");
	}

	private void appendUnsafeUniqueCallsPercent(StringBuilder lineBuilder, LowCovSample lowCovSample) {
		double safe = 0.0;
		double unsafe = 0.0;
		for (SnpCall snpCall : lowCovSample.getUniqueSnpList()) {
			if (snpCall.isSafe()) {
				safe += 1.0;
			} else {
				unsafe += 1.0;
			}
		}
		double percentCalls = 0.0;
		if (safe + unsafe != 0.0) {
			if (unsafe == 0.0) {
				percentCalls = 100.0;
			} else {
				percentCalls = unsafe / (safe + unsafe);
				percentCalls = percentCalls * 100.0;
			}
		}
		lineBuilder.append(String.format("%.2f", percentCalls)).append("\t");
	}

	private void writeResultHeader(BufferedWriter resultWriter) throws IOException {
		StringBuffer headerBuffer = new StringBuffer();
		String tab = "\t";
		// explanation part
		headerBuffer.append("##TOTAL POS IN FILE:" + tab).append(this.positions).append("\n");
		// sample name
		headerBuffer.append("#SAMPLE").append(tab);
		// number of good calls
		headerBuffer.append("#SAFE_CALLS").append(tab);
		// number of unsafe calls
		headerBuffer.append("#UNSAFE_CALLS").append(tab);
		// number of SNP calls, both safe and unsafe
		headerBuffer.append("#SNP_CALLS").append(tab);
		// number of unique SNPs
		headerBuffer.append("#UNIQUE_CALLS").append(tab);
		// percentage of unsafe unique SNPS
		headerBuffer.append("%UNSAFE_UNIQUE_CALLS").append(tab);
		// most agreeing samples -> agreeing samples
		headerBuffer.append("AGR_SAMP").append(tab);
		// append tabs
		for (int i = 0; i < this.numberAgreements - 1; i++) {
			headerBuffer.append(tab);
		}
		// most agreeing samples normalized
		headerBuffer.append("AGR_SAMP_NORM");
		resultWriter.write(headerBuffer.toString());
	}

}
