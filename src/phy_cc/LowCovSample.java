package phy_cc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 * @author heumos
 * 
 * A class representing a low coverage sample.
 *
 */

public class LowCovSample {

	// the name of the sample
	private String name;

	// the number of safe SNP calls
	private int numSafeSnps;

	// the number of safe reference calls
	private int numSafeRef;

	// the number of unsafe SNP calls
	private int numUnsaveSnps;

	// the number of unsafe reference calls
	private int numUnsaveRef;

	// the list of unique SNP calls, holding both safe and unsafe SNPs
	private List<SnpCall> uniqueSnpList;

	// the agreeing SNPs of other samples
	private Map<String, List<SnpCall>> agreeingSnpMap;

	// the number of times when no call was possible in the sample itself
	// or in the pairing sample
	private Map<String, Integer> numPairingImpossibleCall;

	// the z* score in a map
	private Map<String, Double> zStarScores;

	// a ranking based on the zStarScores
	// the key is the sample name, the value the ranking of that sample
	private Map<String, Integer> ranking;

	public LowCovSample(String name) {
		this.name = name;
		this.numSafeSnps = 0;
		this.numSafeRef = 0;
		this.numUnsaveSnps = 0;
		this.numUnsaveRef = 0;
		this.uniqueSnpList = new ArrayList<SnpCall>();
		this.agreeingSnpMap = new HashMap<String, List<SnpCall>>();
		this.numPairingImpossibleCall = new HashMap<String, Integer>();
		this.zStarScores = new HashMap<String, Double>();
		this.ranking = new HashMap<>();
	}
	
	@Override
	public String toString() {
		StringBuilder sB = new StringBuilder();
		sB.append(this.name).append("\n");
		SortedMap<String, Integer> sortedPairingImpMap = new TreeMap<>(this.numPairingImpossibleCall);
		sB.append(sortedPairingImpMap).append("\n");
		SortedMap<String, Double> sortedZStarScoresMap = new TreeMap<>(this.zStarScores);
		sB.append(sortedZStarScoresMap).append("\n");
		SortedMap<String, Integer> sortedRankingMap = new TreeMap<>(this.ranking);
		sB.append(sortedRankingMap).append("\n");
		return sB.toString();
	}

	public void calcZStarScores(int positions) {
		for (Map.Entry<String, List<SnpCall>> entry : this.agreeingSnpMap.entrySet()) {
			String sampleName = entry.getKey();
			List<SnpCall> agreeingSnpList = entry.getValue();
			Double zStarScore;
			if (this.numPairingImpossibleCall.containsKey(sampleName)) {
				zStarScore = calcZStarScore(positions, positions - this.numPairingImpossibleCall.get(sampleName), agreeingSnpList.size());
//				zStarScore = calcZStarScore(positions - this.uniqueSnpList.size(), positions - this.numPairingImpossibleCall.get(sampleName), agreeingSnpList.size());
//				zStarScore = calcZStarScore(positions, positions - this.numPairingImpossibleCall.get(sampleName) - this.uniqueSnpList.size(), agreeingSnpList.size());
			} else {
				zStarScore = calcZStarScore(positions, positions, agreeingSnpList.size());
			}
			this.zStarScores.put(sampleName, zStarScore);
		}
	}

	private double calcZStarScore(int x, int y, int z) {
		if (y == 0) {
			return 0.0;
		} else {
			return new Double(z) * (new Double(x) / new Double(y));
		}
	}

	public void addPairingImpossibleCall(String sampleName) {
		// the current sample has already occured
		if (this.numPairingImpossibleCall.containsKey(sampleName)) {
			Integer val = this.numPairingImpossibleCall.get(sampleName);
			this.numPairingImpossibleCall.put(sampleName, val + 1);
			// the current sample has not occured
		} else {
			this.numPairingImpossibleCall.put(sampleName, 1);
		}
	}

	public void addSafeSnp() {
		this.numSafeSnps++;
	}

	public void addSafeRef() {
		this.numSafeRef++;
	}

	public void addUnsafeSnp() {
		this.numUnsaveSnps++;
	}

	public void addUnsafeRef() {
		this.numUnsaveRef++;
	}

	public void addUniqueSnpCall(SnpCall snpCall) {
		this.uniqueSnpList.add(snpCall);
	}

	public void addAgreeingSnp(String sampleName, SnpCall snpCall) {
		// the current sample has already occured
		if (this.agreeingSnpMap.containsKey(sampleName)) {
			this.agreeingSnpMap.get(sampleName).add(snpCall);
			// the current sample has not occured
		} else {
			List<SnpCall> agreeingList = new ArrayList<SnpCall>();
			agreeingList.add(snpCall);
			this.agreeingSnpMap.put(sampleName, agreeingList);
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumSafeSnps() {
		return numSafeSnps;
	}

	public void setNumSafeSnps(int numSafeSnps) {
		this.numSafeSnps = numSafeSnps;
	}

	public int getNumSafeRef() {
		return numSafeRef;
	}

	public void setNumSafeRef(int numSafeRef) {
		this.numSafeRef = numSafeRef;
	}

	public int getNumUnsaveSnps() {
		return numUnsaveSnps;
	}

	public void setNumUnsaveSnps(int numUnsaveSnps) {
		this.numUnsaveSnps = numUnsaveSnps;
	}

	public int getNumUnsaveRef() {
		return numUnsaveRef;
	}

	public void setNumUnsaveRef(int numUnsaveRef) {
		this.numUnsaveRef = numUnsaveRef;
	}

	public List<SnpCall> getUniqueSnpList() {
		return uniqueSnpList;
	}

	public void setUniqueSnpList(List<SnpCall> uniqueSnpList) {
		this.uniqueSnpList = uniqueSnpList;
	}

	public Map<String, List<SnpCall>> getAgreeingSnpMap() {
		return agreeingSnpMap;
	}

	public void setAgreeingSnpMap(Map<String, List<SnpCall>> agreeingSnpMap) {
		this.agreeingSnpMap = agreeingSnpMap;
	}

	public Map<String, Integer> getNumPairingImpossibleCall() {
		return numPairingImpossibleCall;
	}

	public void setNumPairingImpossibleCall(Map<String, Integer> numPairingImpossibleCall) {
		this.numPairingImpossibleCall = numPairingImpossibleCall;
	}

	public Map<String, Double> getZStarScores() {
		return zStarScores;
	}

	public void setZStarScores(Map<String, Double> zStarScores) {
		this.zStarScores = zStarScores;
	}

	public Map<String, Double> getzStarScores() {
		return zStarScores;
	}

	public void setzStarScores(Map<String, Double> zStarScores) {
		this.zStarScores = zStarScores;
	}

	public Map<String, Integer> getRanking() {
		return ranking;
	}

	public void setRanking(Map<String, Integer> ranking) {
		this.ranking = ranking;
	}

	public void calcRanking() {
		SortedMap<String, Double> sortedAgreeingMap = new TreeMap<>(new SamplePairingNormComparator(this.zStarScores));
		sortedAgreeingMap.putAll(this.zStarScores);
		double refVal = -2.0;
		int rank = 1;
		for (Map.Entry<String, Double> entry: sortedAgreeingMap.entrySet()) {
			String sampleName = entry.getKey();
			Double zStarScore = entry.getValue();
			if (zStarScore < refVal) {
				rank++;
			}
			this.ranking.put(sampleName, rank);
			refVal = zStarScore;
		}
	}
	
	public void calcRandomRanking() {
		Map<Double, List<String>> zStarScoresMap = new HashMap<Double, List<String>>();
		// fill zStarScoresMap
		for (Map.Entry<String, Double> entry : this.zStarScores.entrySet()) {
			String name = entry.getKey();
			Double zScore = entry.getValue();
			if (zStarScoresMap.containsKey(zScore)) {
				List<String> names = zStarScoresMap.get(zScore);
				names.add(name);
			} else {
				List<String> names = new ArrayList<>();
				names.add(name);
				zStarScoresMap.put(zScore, names);
			}
		}
		// sort zStarScoreMap
		zStarScoresMap = new TreeMap<Double, List<String>>(zStarScoresMap);
		// now resolve ranking
		int rank = 1;
		for (Map.Entry<Double, List<String>> entry : zStarScoresMap.entrySet()) {
			List<String> names = entry.getValue();
			if (names.size() == 1) {
				this.ranking.put(names.get(0), rank);
				rank++;
			} else {
				Collections.shuffle(names);
				for (String name : names) {
					this.ranking.put(name, rank);
					rank++;
				}
			}
		}
	}
}
