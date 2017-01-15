package phy_cc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PhyCcTsvCrawler {
	
	private static final String N = "N";
	private static final String DOT = ".";
	private static final String R = "R";

	// path to the SNP table file
	private String sampleTsv;

	// which columns are low coverage samples?
	private List<Integer> cols;

	// the number of positions in the given SNP table file
	private int positions;
	
	// the sample names
	List<String> sampleNames;
	
	// the table itself without the header, a hash map by position
	Map<Integer, String[]> snpTable;
	
	// do we collect the snpTable?
	boolean bootstrappingB;
	
	// the resulting list of low coverage samples
	List<LowCovSample> lowCovSamples;
	
	// the selected samples from the .tsv table
	Set<String> selectedSamples;
	
	// the selected samples from the .tsv table's indices
	Set<Integer> selectedSamplesIndices = new HashSet<>();
	
	// complete sample names
	List<String> completeSampleNames;

	public List<String> getCompleteSampleNames() {
		return completeSampleNames;
	}

	public void setCompleteSampleNames(List<String> completeSampleNames) {
		this.completeSampleNames = completeSampleNames;
	}

	public PhyCcTsvCrawler(String sampleTsv, List<Integer> cols, boolean bootstrappingB,
			Set<String> selectedSamples) {
		this.sampleTsv = sampleTsv;
		this.cols = cols;
		this.positions = 0;
		this.sampleNames = new ArrayList<String>();
		this.snpTable = new HashMap<Integer, String[]>();
		this.bootstrappingB = bootstrappingB;
		this.selectedSamples = selectedSamples;
		this.completeSampleNames = new ArrayList<>();
	}

	public List<LowCovSample> crawlTsv() throws IOException {

		BufferedReader bufferedReader = new BufferedReader(new FileReader(this.sampleTsv));
		String line = null;
		List<LowCovSample> lowCovSamples = new ArrayList<LowCovSample>();
		PhyCcClassificator phyCcClassificator = new PhyCcClassificator(null, null);

		boolean headerWasRead = false;

		while ((line = bufferedReader.readLine()) != null) {
			String[] lineSplit = line.split("\t");
			// read the header line
			if (!headerWasRead) {
				for (int i = 2; i < lineSplit.length; i++) {
					Integer iInt = new Integer(i);
					String name = lineSplit[i];
					this.completeSampleNames.add(name);
					if (this.cols.contains(iInt)) {
						LowCovSample lowCovSample = new LowCovSample(name);
						lowCovSamples.add(lowCovSample);
						this.sampleNames.add(name);
						this.selectedSamplesIndices.add(iInt);
					} else {
						if (!this.selectedSamples.isEmpty() && this.selectedSamples.contains(name)) {
							this.sampleNames.add(name);
							this.selectedSamplesIndices.add(iInt);
						} else {
							if (this.selectedSamples.isEmpty()) {
								this.sampleNames.add(name);
							}
						}
					}
				}
				// fill numPairingImpossibleCallMap
				for (LowCovSample lowCovSample : lowCovSamples) {
					for (String name : this.sampleNames) {
						if (!name.equals(lowCovSample.getName())) {
							lowCovSample.getNumPairingImpossibleCall().put(name, 0);
							lowCovSample.getAgreeingSnpMap().put(name, new ArrayList<SnpCall>());
						}
					}
				}
				headerWasRead = true;
				// read the file, position for position
			} else {
				this.positions++;
				if (this.bootstrappingB) {
					this.snpTable.put(this.positions, lineSplit);
				}
				int i = 0;
				for (int col : this.cols) {
					LowCovSample lowCovSample = lowCovSamples.get(i);
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
							if (checkAgreement(call, otherCall) && !call.equals(N)) {
								isUniqueSnpCall = false;
								String pos = lineSplit[0];
								String ref = lineSplit[1];
								char snp = otherCall.charAt(0);
								boolean safe = false;
								if (Character.isUpperCase(snp)) {
									safe = true;
								}
								SnpCall snpCall = new SnpCall(this.completeSampleNames.get(j-2), pos, ref, otherCall, safe);
								lowCovSample.addAgreeingSnp(this.completeSampleNames.get(j-2), snpCall);
								// no agreement, but check for "N"
							} else if (call.equals(N) || otherCall.equals(N)) {
								lowCovSample.addPairingImpossibleCall(this.completeSampleNames.get(j-2));
							}
						}
					}
					// we have a unique snp call
					if (isUniqueSnpCall && !call.equals(N) && !call.equals(R) && !call.equals(DOT)) {
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
		bufferedReader.close();
		for (LowCovSample lowCovSample : lowCovSamples) {
			lowCovSample.calcZStarScores(this.positions);
			lowCovSample.calcRandomRanking();
		}
		this.lowCovSamples = lowCovSamples;
		return lowCovSamples;
	}
	
	public static boolean checkAgreement(String call, String otherCall) {
		boolean agreeing = false;
		if (call.toLowerCase().equals(otherCall.toLowerCase())) {
			return true;
		} else {
			if ((call.equals(DOT) && otherCall.equals(R)) ||
					call.equals(R) && otherCall.equals(DOT)) {
				return true;
			}
		}
		return agreeing;
	}

	public String getSampleTsv() {
		return sampleTsv;
	}

	public void setSampleTsv(String sampleTsv) {
		this.sampleTsv = sampleTsv;
	}

	public List<Integer> getCols() {
		return cols;
	}

	public void setCols(List<Integer> cols) {
		this.cols = cols;
	}

	public int getCalls() {
		return positions;
	}

	public void setCalls(int calls) {
		this.positions = calls;
	}

	public int getPositions() {
		return positions;
	}

	public void setPositions(int positions) {
		this.positions = positions;
	}

	public List<String> getSampleNames() {
		return sampleNames;
	}

	public void setSampleNames(List<String> sampleNames) {
		this.sampleNames = sampleNames;
	}

	public Map<Integer, String[]> getSnpTable() {
		return snpTable;
	}

	public void setSnpTable(Map<Integer, String[]> snpTable) {
		this.snpTable = snpTable;
	}

	public Set<Integer> getSelectedSamplesIndices() {
		return selectedSamplesIndices;
	}

	public void setSelectedSamplesIndices(Set<Integer> selectedSamplesIndices) {
		this.selectedSamplesIndices = selectedSamplesIndices;
	}
	
	
}
