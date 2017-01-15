package topas;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import feature_format.gff.GffThreeEntry;
import feature_format.gff.filter.GffThreeFinder;
import io.feature_format.GffThreeReader;
import topas.Topas.TOPASModule;
import topas.parameters.AnnotateVCFParameters;

@TOPASModule(
		purpose = "annotate a vcf file by reference of a vcf CHROM:POSITION to SEQID:START-END of a gff3 file"
		)

public class AnnotateVCF {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(AnnotateVCF.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		AnnotateVCFParameters.createInstance(args);
		
		String inputFile = AnnotateVCFParameters.getInstance().getParameter("vcf").toString();
		String outputFile = AnnotateVCFParameters.getInstance().getParameter("o").toString();
		String gff = AnnotateVCFParameters.getInstance().getParameter("gff").toString();
		String mapFile = AnnotateVCFParameters.getInstance().getParameter("map").toString();
		String[] types = AnnotateVCFParameters.getInstance().getParameter("type").getValues();
		System.out.println();
		
		boolean m = AnnotateVCFParameters.getInstance().getParameter("map").isPresent();
		boolean type = AnnotateVCFParameters.getInstance().getParameter("type").isPresent();
		
		if (!type) {
			types = new String[1];
			types[0] = "NO FILTERING SPECIFIED";
		}
		
		System.out.println("Parameters chosen: ");
		System.out.println("VCF input            : "+inputFile);
		System.out.println("VCF output           : "+outputFile);
		System.out.println("GFF3 file            : "+gff);
		System.out.println("Mapping file         : "+mapFile);
		System.out.println("Type(s) to Filter    : "+Arrays.toString(types));
		
		System.out.println();
		
		List<String> typesToFilter = new ArrayList<String>();
		if (type) {
			typesToFilter = new ArrayList<String>(Arrays.asList(types));
			
		}
		
		System.out.println("Reading GFF3 file...");
		GffThreeReader gReader = new GffThreeReader(gff);
		HashMap<String, HashMap<String, List<GffThreeEntry>>> gMap = gReader.createSeqIdTypeMap();
		System.out.println();
		
		// read in and process mapping file if necessary
		HashMap<String, String> chromSeqIdMap = new HashMap<String, String>();
		if (m) {
			System.out.println("Reading Mapping file");
			System.out.println();
			chromSeqIdMap = createChromSeqIdMap(mapFile);
		}

		
		System.out.println("Annotating VCF file...");
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line;
		String headerLines = "";
		String header = "";
		String t = "\t";
		int annot = 0;
		
		List<GffThreeEntry> gTEList = new ArrayList<GffThreeEntry>();
		
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.startsWith("##")) {
				headerLines += line + "\n";
			} else {
				if (line.startsWith("#")) {
					bw.write(headerLines);
					bw.write("##ANNOTATION=<ID:Entry_X,Number=.,Type=String,Description=\"VCF Entry Annotation from GFF3\">");
					bw.write("\n");
					header = line;
					String[] lineSplit = line.split("\t");
					String chrom = lineSplit[0];
					String pos = lineSplit[1];
					String id = lineSplit[2];
					String ref = lineSplit[3];
					String alt = lineSplit[4];
					String qual = lineSplit[5];
					String filter = lineSplit[6];
					String info = lineSplit[7];
					String other = createOther(lineSplit);
					header = chrom + t + pos + t + id + t + ref + t + alt + t
							+ qual + t + filter + t + info + t + "ANNOTATION" + t + other;
					bw.write(header);
				} else {
					annot++;
					String[] lineSplit = line.split("\t");
					String chrom = lineSplit[0];
					String pos = lineSplit[1];
//					String chromRange = "";
					// map if map file was specified
					HashMap<String, List<GffThreeEntry>> typeMap;
					if (m) {
						String seqId = chromSeqIdMap.get(chrom);
						typeMap = gMap.get(seqId);
//						chromRange = seqId + ":" + pos;
					} else {
						typeMap = gMap.get(chrom);
//						chromRange = chrom + ":" + pos;
					}		
					List<GffThreeEntry> foundGTEList = new ArrayList<GffThreeEntry>();
//					List<String> chromRangeList = new ArrayList<String>();
//					chromRangeList.add(chromRange);
//					GffThreeFinder gFinder = new GffThreeFinder(gTEList);
//					gFinder.getFoundLines(chromRangeList, typesToFilter);
//					List<GffThreeEntry> foundGTEList = gFinder.getFoundGTEList();
					if (type) {
						for (int i = 0; i < typesToFilter.size(); i++) {
							if (typeMap.containsKey(typesToFilter.get(i))) {
								GffThreeFinder gFinder = new GffThreeFinder(typeMap.get(typesToFilter.get(i)));
								gFinder.getFoundLines(Integer.parseInt(pos));
								foundGTEList.addAll(gFinder.getFoundGTEList());
//								gTEList.addAll(typeMap.get(typesToFilter.get(i)));
							}							
						}
					} else {
						Set<Entry<String, List<GffThreeEntry>>> entries = typeMap.entrySet();
						for (Entry<String, List<GffThreeEntry>> entry : entries) {
							gTEList.addAll(entry.getValue());
						}
						GffThreeFinder gFinder = new GffThreeFinder(gTEList);
						gFinder.getFoundLines(Integer.parseInt(pos), typesToFilter);
						foundGTEList.addAll(gFinder.getFoundGTEList());
					}
					String annotation = createAnnotation(foundGTEList);
					String id = lineSplit[2];
					if (id.equals(".")) {
						id = chrom + ":" + pos;
					}
					String ref = lineSplit[3];
					String alt = lineSplit[4];
					String qual = lineSplit[5];
					String filter = lineSplit[6];
					String info = lineSplit[7];
					String other = createOther(lineSplit);
					StringBuilder annotBuilder = new StringBuilder();
					annotBuilder.append(chrom);
					annotBuilder.append(t);
					annotBuilder.append(pos);
					annotBuilder.append(t);
					annotBuilder.append(id);
					annotBuilder.append(t);
					annotBuilder.append(ref);
					annotBuilder.append(t);
					annotBuilder.append(alt);
					annotBuilder.append(t);
					annotBuilder.append(qual);
					annotBuilder.append(t);
					annotBuilder.append(filter);
					annotBuilder.append(t);
					annotBuilder.append(info);
					annotBuilder.append(t);
					annotBuilder.append(annotation);
					annotBuilder.append(t);
					annotBuilder.append(other);
					String annotated = annotBuilder.toString();
					bw.write("\n");
					bw.write(annotated);
					
					if (annot%1000 == 0) {
						System.out.println();
						System.out.println("Annotated " + annot + " VCF Entries.");
						System.out.println();
					}
					gTEList.clear();
					
				}
			}						
		}
		bw.close();
		br.close();
		
		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(AnnotateVCF.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");

	}

	private static HashMap<String, String> createChromSeqIdMap(String mapFile) throws IOException {
		HashMap<String, String> chromSeqIdMap = new HashMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(mapFile));
		String line;
		while ((line = br.readLine()) != null) {
			String[] splitLine = line.split("\t");
			String chrom = splitLine[0];
			String seqId = splitLine[1];
			chromSeqIdMap.put(chrom, seqId);
		}
		br.close();
		return chromSeqIdMap;
	}

	private static String createAnnotation(List<GffThreeEntry> foundGTEList) {
		StringBuilder sB = new StringBuilder();
		String id = "ID:";
		String name = "Name:";
		String semi = ";";
		String c = ",";
		String locus_tag = "locus_tag:";
		String parent = "Parent:";
		for (int i = 0; i < foundGTEList.size(); i++) {
			int j = i+1;
			sB.append("Entry_" + j + "=");
			GffThreeEntry gTE = foundGTEList.get(i);
			SortedMap<String, String> attributes = gTE.getAttributes();
			if (gTE.hasId()) {
				sB.append(id);
				sB.append(attributes.get("ID"));
				sB.append(c);
			}
			if (gTE.hasName()) {
				sB.append(name);
				sB.append(attributes.get("Name"));
				sB.append(c);
			}
			if (gTE.hasLocusTag()) {
				sB.append(locus_tag);
				sB.append(attributes.get("locus_tag"));
				sB.append(c);
			}
			if (gTE.hasParentValue()) {
				sB.append(parent);
				sB.append(attributes.get("Parent"));
				sB.append(c);
			}
			sB.deleteCharAt(sB.length()-1);
			sB.append(semi);
		}
		if (sB.length() > 0) {
			sB.deleteCharAt(sB.length()-1);
		}
		return sB.toString();
	}

	private static String createOther(String[] lineSplit) {
		StringBuilder other = new StringBuilder();
		String tab = "\t";
		for (int i = 8; i < lineSplit.length; i++) {
			if (i == 8) {
				other.append(lineSplit[i]);
			} else {
				other.append(tab);
				other.append(lineSplit[i]);
			}
		}
		return other.toString();
	}
}
