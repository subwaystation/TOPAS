package io.feature_format;

import feature_format.AFeatureFormatEntry;
import feature_format.gff.GffThreeEntry;
import feature_format.gff.GffThreeParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GffThreeReader extends AFeatureFormatReader {

	/**
	 * @param inputFile
	 */
	public GffThreeReader(String inputFile) {
		super(inputFile);
	}

	@Override
	protected List<AFeatureFormatEntry> parseFile(BufferedReader bR) throws IOException {
		String line;
		GffThreeParser gTP = new GffThreeParser();
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
				gTP.setLine(line);
				GffThreeEntry gTE = (GffThreeEntry) gTP.scanLine();
				addToGffThreeFeatureList(gTE);
			}
		}
		bR.close();
		return this.getFeatureList();
	}
	

	/**
	 * @param gTE
	 */
	private void addToGffThreeFeatureList(GffThreeEntry gTE) {
		this.getFeatureList().add(gTE);
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, HashMap<String, List<GffThreeEntry>>> createSeqIdTypeMap() throws IOException {
		HashMap<String, HashMap<String, List<GffThreeEntry>>> gMap = new HashMap<String, HashMap<String, List<GffThreeEntry>>>();
		BufferedReader br = new BufferedReader(new FileReader(this.getInputFile()));
		String line;
		GffThreeParser gTP = new GffThreeParser();
		while ((line = br.readLine()) != null) {

			// commentLine
			if(line.startsWith("#")) {
				// featureLine
			} else {				
				gTP.setLine(line);								
				GffThreeEntry gTE = (GffThreeEntry) gTP.scanLine();
				
				String seqId = gTE.getSeqId();
				String type = gTE.getType();
								
				if (gMap.containsKey(seqId)) {
					HashMap<String, List<GffThreeEntry>> typeMap = gMap.get(seqId);
					if (typeMap.containsKey(type)) {
						List<GffThreeEntry> gTEList = typeMap.get(type);
						gTEList.add(gTE);
					} else {
						List<GffThreeEntry> gTEList = new ArrayList<GffThreeEntry>();
						gTEList.add(gTE);
						typeMap.put(type, gTEList);
					}
				} else {
					HashMap<String, List<GffThreeEntry>> typeMap = new HashMap<String, List<GffThreeEntry>>();
					List<GffThreeEntry> gTEList = new ArrayList<GffThreeEntry>();
					gTEList.add(gTE);
					typeMap.put(type, gTEList);
					gMap.put(seqId, typeMap);
				}
			}


		}
		br.close();
		return gMap;
	}
	
	@Override
	protected void addToAttrMap(String start, String end, String key,
			HashMap<String, List<AFeatureFormatEntry>> attrMap)
			throws IOException {
		GffThreeEntry gTE = new GffThreeEntry(null, null, null,
				Integer.parseInt(start), Integer.parseInt(end), null, null, null, null);
		if (attrMap.containsKey(key)) {
			List<AFeatureFormatEntry> gEList = attrMap.get(key);
			gEList.add(gTE);
		} else {
			List<AFeatureFormatEntry> gTEList = new ArrayList<AFeatureFormatEntry>();
			gTEList.add(gTE);
			attrMap.put(key, gTEList);
		}
	}
	
}
