package io.feature_format;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import feature_format.AFeatureFormatEntry;
import feature_format.gtf.GtfEntry;
import feature_format.gtf.GtfParser;

public class GtfReader extends AFeatureFormatReader{
	
	public GtfReader(String inputFile) {
		super(inputFile);
	}
	
	@Override
	protected List<AFeatureFormatEntry> parseFile(BufferedReader bR) throws IOException {
		String line;
		GtfParser gP = new GtfParser();
		boolean firstLine = false;
		boolean lastHeader = false;
		String header = "";
		while ((line = bR.readLine()) != null) {
			// commentLine
			if(line.startsWith("#")) {
				if (!firstLine) {
					header += line;
					firstLine = true;
				} else {
					header += "\n" + line;
				}
				// featureLine
			} else {
				if (!lastHeader) {
					this.setHeader(header);
				}				
				gP.setLine(line);
				GtfEntry gE = (GtfEntry) gP.scanLine();
				addToGffThreeFeatureList(gE);
			}
		}
		bR.close();
		return this.getFeatureList();
	}
	

	/**
	 * @param gTE
	 */
	private void addToGffThreeFeatureList(GtfEntry gE) {
		this.getFeatureList().add(gE);
	}

	@Override
	protected void addToAttrMap(String start, String end, String key,
			HashMap<String, List<AFeatureFormatEntry>> attrMap)
			throws IOException {
		GtfEntry gE = new GtfEntry(null, null, null,
				Integer.parseInt(start), Integer.parseInt(end), null, null, null, null);
		if (attrMap.containsKey(key)) {
			List<AFeatureFormatEntry> gEList = attrMap.get(key);
			gEList.add(gE);
		} else {
			List<AFeatureFormatEntry> gEList = new ArrayList<AFeatureFormatEntry>();
			gEList.add(gE);
			attrMap.put(key, gEList);
		}
	}	

}
