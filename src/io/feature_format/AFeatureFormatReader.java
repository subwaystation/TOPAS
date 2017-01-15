package io.feature_format;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import feature_format.AFeatureFormatEntry;

/**
 * An abstract class representing a Feature Format Reader
 * @author heumos
 *
 */
public abstract class AFeatureFormatReader {

	private String inputFile;
	private List<AFeatureFormatEntry> featureList;
	private String header;

	/**
	 * @param inputFile
	 */
	public AFeatureFormatReader(String inputFile) {
		this.inputFile = inputFile;
		this.featureList = new ArrayList<AFeatureFormatEntry>();
		this.header = "";
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public List<AFeatureFormatEntry> getFeatureList() {
		return featureList;
	}

	public void setFeatureList(List<AFeatureFormatEntry> featureList) {
		this.featureList = featureList;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * @throws IOException 
	 */
	public List<AFeatureFormatEntry> scanFile() throws IOException {
		BufferedReader bR = new BufferedReader(new FileReader(this.inputFile));
		return parseFile(bR);
	}

	/**
	 * @param bR
	 * @return
	 * @throws IOException 
	 */
	protected abstract List<AFeatureFormatEntry> parseFile(BufferedReader bR) throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	public int linesBeforeFirstEntry() throws IOException {
		int lines = 0;
		BufferedReader br = new BufferedReader(new FileReader(this.inputFile));
		String line;
		while ((line = br.readLine()) != null) {

			// comment line
			if(line.startsWith("#")) {
				lines++;
				// entry line
			} else {
				br.close();
				return lines;
			}
		}
		br.close();
		return lines;
	}

	public String scanHeader() throws IOException {
		String header = "";
		String line;
		BufferedReader br = new BufferedReader(new FileReader(this.inputFile));
		boolean first = false;
		while ((line = br.readLine()) != null) {
			if (line.startsWith("#")) {
				if (!first) {
					header += line;
					first = true;
				} else {
					header += "\n" + line;
				}				
			} else {
				br.close();
				this.header = header;
				return this.header;
			}
		}
		br.close();
		this.header = header;
		return this.header;
	}

	public HashMap<String, List<AFeatureFormatEntry>> getAttributesKeyMapFilterType(String key, String type) throws IOException {
		BufferedReader bR = new BufferedReader(new FileReader(this.inputFile));
		String line;
		HashMap<String, List<AFeatureFormatEntry>> attrMap = new HashMap<String, List<AFeatureFormatEntry>>();
		while ((line = bR.readLine()) != null) {
			// comment lines are ignored
			if (line.startsWith("#")) {

			} else {
				String[] lineSplit = line.split("\t");
				String feature = lineSplit[2];
				if (feature.equals(type)) {
					String attrKey = "";
					String start = lineSplit[3];
					String end = lineSplit[4];
					String attributes = lineSplit[8];
					String[] attrSplit = attributes.split(";");
					for (String attr : attrSplit) {
						attr = attr.trim();
						String[] splitSpace = attr.split(" ");
						attrKey = splitSpace[0].trim();
						String attrValue = splitSpace[1].trim().replace("\"", "");
						if (attrKey.equals(key.trim())) {
							addToAttrMap(start, end, attrValue, attrMap);
							break;
						}						
					}
				}				
			}
		}
		bR.close();
		return attrMap;
	}

	protected abstract void addToAttrMap(String start, String end, String key, HashMap<String, List<AFeatureFormatEntry>> attrMap) throws IOException;

}
