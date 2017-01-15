package io.vcf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import vcf.VcfEntry;
import vcf.VcfLineParser;

public class VcfReader {
	
	// the VCF file
	private String pathToVcfFile;
	
	// the resulting VCF entries
	private List<VcfEntry> vcfEntries;
	
	// the resulting list of identifiers
	private List<String> identifiers;
	
	// the resulting list of contig lengths
	private List<Integer> contigLengths;
	
	public VcfReader(String pathToVcfFile) {
		this.pathToVcfFile = pathToVcfFile;
		this.identifiers = new ArrayList<>();
		this.contigLengths = new ArrayList<>();
	}
	
	/**
	 * Parse a VCF file and give a list of VCF entries back.
	 * @return a list of vcf entries.
	 * @throws IOException
	 */
	public HashMap<String, List<VcfEntry>> parseVcfFile(String[] contigNames) throws IOException {
		System.out.println("[VCF_READER] Reading VCF from file " + this.pathToVcfFile);
		String line;
		BufferedReader bufferedReader = null;
		HashMap<String, List<VcfEntry>> entriesMap = new HashMap<>();
		// check if file is zipped
		if (this.pathToVcfFile.endsWith(".gz")) {
			GZIPInputStream gzipInputStream =  new GZIPInputStream(new FileInputStream(this.pathToVcfFile));
			bufferedReader = new BufferedReader(new InputStreamReader(gzipInputStream));
		} else {
			bufferedReader = new BufferedReader(new FileReader(this.pathToVcfFile));
		}
		VcfLineParser vcfLineParser = new VcfLineParser();
		boolean processedHeader = false;
		VcfEntry nullEntry = new VcfEntry();
		while ((line = bufferedReader.readLine()) != null) {
			if (!line.startsWith("#")) {
				if (!processedHeader) {
					for (int i = 0; i < this.contigLengths.size(); i++) {
						List<VcfEntry> vcfEntries = new ArrayList<>(this.contigLengths.get(i));
						for (int j = 0; j < this.contigLengths.get(i); j++) {
							vcfEntries.add(nullEntry);
						}
						entriesMap.put(this.identifiers.get(i), vcfEntries);
					}
					processedHeader = true;
					VcfEntry vcfEntry = vcfLineParser.createVcfEntryFromLine(line);
					List<VcfEntry> vcfEntries = entriesMap.get(vcfEntry.getChrom());
					vcfEntries.set(Integer.parseInt(vcfEntry.getPos()) - 1, vcfEntry);
				} else {
					if (entriesMap.size() > 0) {
						VcfEntry vcfEntry = vcfLineParser.createVcfEntryFromLine(line);
						List<VcfEntry> vcfEntries = entriesMap.get(vcfEntry.getChrom());
						vcfEntries.set(Integer.parseInt(vcfEntry.getPos()) - 1, vcfEntry);
					} else {
						System.out.println("[VCF_READER] No specified contig found. Can't process any VCF records. Exit...");
						System.exit(0);
					}
				}
			} else {
				if (line.startsWith("##contig")) {
					parseContig(line, contigNames);
				}
			}
		}
		bufferedReader.close();
//		return this.vcfEntries;
		return entriesMap;
	}
	
	/**
	 * Parses the given contig line and adds identifier and length of contig
	 * to the local lists.
	 * @param line
	 */
	private void parseContig(String line, String[] contigNames) {
		String[] lineSplit = line.split("<");
		String contigPart = lineSplit[1].substring(0, lineSplit[1].length() - 1);
		String[] contigPartSplit = contigPart.split(",");
		String identifier = contigPartSplit[0].split("=")[1];
		String contigLength = contigPartSplit[1].split("=")[1];
		if (contigNames.length > 0) {
			Set<String> contigSet = new HashSet<String>(Arrays.asList(contigNames));
			if (contigSet.contains(identifier)) {
				this.identifiers.add(identifier);
				this.contigLengths.add(Integer.parseInt(contigLength));	
			}
		} else {
			this.identifiers.add(identifier);
			this.contigLengths.add(Integer.parseInt(contigLength));
		}
	}
	
	public VcfEntry[] getVcfEntriesArray() {
		int contigLen = this.contigLengths.get(0);
		VcfEntry[] vcfEntryArray = new VcfEntry[contigLen];
		for (int i = 0; i < this.vcfEntries.size(); i++) {
			VcfEntry vcfEntry= this.vcfEntries.get(i);
			int pos = Integer.parseInt(vcfEntry.getPos());
			vcfEntryArray[pos - 1] = vcfEntry;
		}
		return vcfEntryArray;
	}
	
	public HashMap<String, VcfEntry[]> getVcfEntriesMap() {
		int chromosomes = 0;
		String currentChrom = "";
		List<VcfEntry[]> vcfEntriesArrays = new ArrayList<>();
		for (int len : this.contigLengths) {
			VcfEntry[] vcfEntryArray = new VcfEntry[len];
			vcfEntriesArrays.add(vcfEntryArray);
		}
		VcfEntry[] curArray = null;
		for (int i = 0; i < this.vcfEntries.size(); i++) {
			VcfEntry vcfEntry = this.vcfEntries.get(i);
			if (!currentChrom.equals(vcfEntry.getChrom())) {
				curArray = vcfEntriesArrays.get(chromosomes);
				int pos = Integer.parseInt(vcfEntry.getPos());
				curArray[pos - 1] = vcfEntry;
				chromosomes++;
				currentChrom = vcfEntry.getChrom();
			} else {
				int pos = Integer.parseInt(vcfEntry.getPos());
				curArray[pos - 1] = vcfEntry;
			}
		}
		HashMap<String, VcfEntry[]> hashMap = new HashMap<>();
		if (vcfEntriesArrays.size() > 0) {
			for (VcfEntry[] entries : vcfEntriesArrays) {
				hashMap.put(entries[0].getChrom(), entries);
			}
			return hashMap;
		} else {
			System.out.println("[VCF_READER] No specified contig found. Can't process any VCF records. Exit...");
			System.exit(0);
			return hashMap;
		}
	}

	public String getPathToVcfFile() {
		return pathToVcfFile;
	}

	public void setPathToVcfFile(String pathToVcfFile) {
		this.pathToVcfFile = pathToVcfFile;
	}

	public List<VcfEntry> getVcfEntries() {
		return vcfEntries;
	}

	public void setVcfEntries(List<VcfEntry> vcfEntries) {
		this.vcfEntries = vcfEntries;
	}

	public List<String> getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(List<String> identifiers) {
		this.identifiers = identifiers;
	}

	public List<Integer> getContigLengths() {
		return contigLengths;
	}

	public void setContigLengths(List<Integer> contigLenghts) {
		this.contigLengths = contigLenghts;
	}
	
	

}
