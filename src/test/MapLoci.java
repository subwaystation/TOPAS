package test;

import feature_format.gff.GffThreeEntry;
import feature_format.gff.filter.GffThreeFilter;
import feature_format.gff.gene_loci_mapper.GetSequenceFromGeneLocus;
import io.fasta.FastaIndexReader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;

import core.fasta_index.FastaIndex;
import core.sequence.OutputSequenceFormatter;
import topas.Topas.TOPASModule;
import utils.ArrayListUtils;

@TOPASModule(
		purpose = "specify several gene locis of a gff3 file and write the resulting sequences into a fasta file"
		)

public class MapLoci {
	
	// TODO enable filtering of attributes

	public static void main(String[] args) throws IOException {
		System.out.println(MapLoci.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		MapLociParameters.createInstance(args);
		
		String fa = MapLociParameters.getInstance().getParameter("fa").toString();
		String fai = MapLociParameters.getInstance().getParameter("fai").toString();
		String gff = MapLociParameters.getInstance().getParameter("gff").toString();
		String outputFile = MapLociParameters.getInstance().getParameter("o").toString();
		String[] l = MapLociParameters.getInstance().getParameter("l").getValues();
		String[] seqIds = MapLociParameters.getInstance().getParameter("seqId").getValues();
		String[] ranges = MapLociParameters.getInstance().getParameter("range").getValues();
		String[] sources = MapLociParameters.getInstance().getParameter("source").getValues();
		String[] types = MapLociParameters.getInstance().getParameter("type").getValues();
		String[] scores = MapLociParameters.getInstance().getParameter("score").getValues();
		String[] strands = MapLociParameters.getInstance().getParameter("strand").getValues();
		String[] phases = MapLociParameters.getInstance().getParameter("phase").getValues();
		String[] attributes = MapLociParameters.getInstance().getParameter("attributes").getValues();
		System.out.println();
		
		boolean linesMap = MapLociParameters.getInstance().getParameter("l").isPresent();
		boolean iD = MapLociParameters.getInstance().getParameter("seqId").isPresent();
		boolean r = MapLociParameters.getInstance().getParameter("range").isPresent();
		boolean soc = MapLociParameters.getInstance().getParameter("source").isPresent();
		boolean t = MapLociParameters.getInstance().getParameter("type").isPresent();
		boolean sco = MapLociParameters.getInstance().getParameter("score").isPresent();
		boolean st = MapLociParameters.getInstance().getParameter("strand").isPresent();
		boolean p = MapLociParameters.getInstance().getParameter("phase").isPresent();
		boolean a = MapLociParameters.getInstance().getParameter("attributes").isPresent();
		
		// if both lines to map and seqIds to map are null, then no gene loci was specified
		if(!linesMap&&!iD) {
			System.err.println("Please specify the line(s) to map using '-l int int ...' OR \n       specify the seqIds and the range(s) to filter using\n       '-seqId seqId1 seqId2 ...' AND '-range start1_end1 start2_end2 ...'.");
			System.exit(1);
		}
		if (!linesMap) {
			l = new String[1];
			l[0] = "none";
		}
		if (!iD) {
			seqIds = new String[1];
			seqIds[0] = "none";
			if (r) {
				ranges = new String[1];
				ranges[0] = "NO SEQIDS SPECIFIED, RANGES ARE IGNORED!";
			}
		}
		if (!r) {
			ranges = new String[1];
			ranges[0] = "NO FILTERING SPECIFIED";
		}
		if (!soc) {
			sources = new String[1];
			sources[0] = "NO FILTERING SPECIFIED";
		}
		if (!t) {
			types = new String[1];
			types[0] = "NO FILTERING SPECIFIED";
		}
		if (!sco) {
			scores = new String[1];
			scores[0] = "NO FILTERING SPECIFIED";
		}
		if (!st) {
			strands = new String[1];
			strands[0] = "NO FILTERING SPECIFIED";
		}
		if (!p) {
			phases = new String[1];
			phases[0] = "NO FILTERING SPECIFIED";
		}
		if (!a) {
			attributes = new String[1];
			attributes[0] = "NO FILTERING SPECIFIED";
		}
		
		System.out.println("Parameters chosen: ");
		System.out.println("Fasta file           : "+fa);
		System.out.println("Fasta index file     : "+fai);
		System.out.println("Gff3 file            : "+gff);
		System.out.println("Output file          : "+outputFile);
		System.out.println("Line(s) to map       : "+Arrays.toString(l));
		System.out.println("SeqId(s) to map      : "+Arrays.toString(seqIds));
		System.out.println("Range(s) to map      : "+Arrays.toString(ranges));
		System.out.println("Source(s) to map     : "+Arrays.toString(sources));
		System.out.println("Type(s) to map       : "+Arrays.toString(types));
		System.out.println("Score(s) to map      : "+Arrays.toString(scores));
		System.out.println("Strand(s) to map     : "+Arrays.toString(strands));
		System.out.println("Phase(s) to map      : "+Arrays.toString(phases));
		
		System.out.println();
		
		// read in the fasta index
		System.out.println("Reading fasta index from: " + fai);
		System.out.println();
		FastaIndexReader fIR = new FastaIndexReader(fai);
		List<FastaIndex> faidxList = fIR.readFastaIndex();
		
		
		// add the lines to map to an array list
		List<GffThreeEntry> linesMapList = new ArrayList<GffThreeEntry>();
		if (linesMap) {
			// create a set of the lines to map
			List<Integer> linesToMap = new ArrayList<Integer>();
			for(int i = 0; i < l.length; i++) {
				linesToMap.add(Integer.parseInt(l[i]));
			}
			// now fetch the corresponding gff3 entries
			GffThreeFilter gFilter = new GffThreeFilter(gff, linesToMap);
			linesMapList = gFilter.findLines();
		}
		// final list of gff3 entries to map
		List<GffThreeEntry> gteList = new ArrayList<GffThreeEntry>();		
		
		// add the gff3 entries specified by seqid, range, source, type, score, strand, phase, attributes
		List<GffThreeEntry> filteredMapList = new ArrayList<GffThreeEntry>();
		if (iD) {
			HashSet<String> seqIdsToMap = new HashSet<String>();
			for(int i = 0; i < seqIds.length; i++) {
				seqIdsToMap.add(seqIds[i].trim());
			}
			List<String> rangesToMap = new ArrayList<String>();
			if (r) {
				for(int i = 0; i < ranges.length; i++) {
					rangesToMap.add(ranges[i].trim());
				}
			}
			HashSet<String> scoresToMap = new HashSet<String>();
			if (sco) {
				for (int i = 0; i < scores.length; i++) {
					scoresToMap.add(scores[i].trim());
				}
			}
			HashSet<String> sourcesToMap = new HashSet<String>();
			if (soc) {
				for (int i = 0; i < sources.length; i++) {
					sourcesToMap.add(sources[i].trim());
				}
			}
			HashSet<String> typesToMap = new HashSet<String>();
			if (t) {
				for (int i = 0; i < types.length; i++) {
					typesToMap.add(types[i].trim());
				}
			}
			HashSet<String> strandsToMap = new HashSet<String>();
			if (st) {
				for (int i = 0; i < strands.length; i++) {
					strandsToMap.add(strands[i].trim());
				}
			}
			HashSet<String> phasesToMap = new HashSet<String>();
			if (p) {
				for (int i = 0; i < phases.length; i++) {
					phasesToMap.add(phases[i].trim());
				}
			}
			//GffThreeReader gReader = new GffThreeReader(gff, seqIdsToMap, rangesToMap, sourcesToMap, typesToMap, scoresToMap, strandsToMap, phasesToMap);
			//filteredMapList = gReader.getFilteredLines();
		}
		
		gteList = ArrayListUtils.unionGffThreeEntryList(linesMapList, filteredMapList);
				
		// get the sequences from the gene loci and write them directly to the output file
		try{
			// create file 
			FileWriter fstream = new FileWriter(outputFile);
			System.out.println();
			System.out.println("The gene loci can be found at: " + outputFile);
			BufferedWriter out = new BufferedWriter(fstream);
			int faidxIndex = 0;
			String sequence = "";
			String identifier = "";
			for (int i = 0; i < gteList.size(); i++) {
				GffThreeEntry gE = gteList.get(i);
				String seqId = gE.getSeqId();
				int start = gE.getStart();
				int end = gE.getEnd();
				for(int j = 0; j < faidxList.size(); j++) {
					if(faidxList.get(j).getSequenceName().contains(seqId)) {
						faidxIndex = j;
					}
				}
				GetSequenceFromGeneLocus getGeneLocus = new GetSequenceFromGeneLocus(fa, faidxList.get(faidxIndex), start, end);
				String s = getGeneLocus.getSequenceFromGeneLocus();
				s = s.replace("\n", "");
				sequence = OutputSequenceFormatter.formatSequence(s, 80);		
				
				
				identifier += gE.getSeqId() + "|" + gE.getSource() + "|" + gE.getType() + "|" + gE.getStart() + "|" +
						gE.getEnd() + "|" + gE.getScore() + "|" + gE.getStrand() + "|" + gE.getPhase() + "|";
				SortedMap<String, String> attributesE = gE.getAttributes();
				ArrayList<String> attributesList = new ArrayList<String>();
				if(attributesE.containsKey("ID")) {
					String attribute = "ID=";
					attribute += attributesE.get("ID");
					attributesList.add(attribute);
				}
				if(attributesE.containsKey("Parent")) {
					String attribute = "Parent=";
					attribute += attributesE.get("Parent");
					attributesList.add(attribute);
				}
				if(attributesE.containsKey("locus_tag")) {
					String attribute = "locus_tag=";
					attribute += attributesE.get("locus_tag");
					attributesList.add(attribute);
				}
				for (int k = 0; k < attributesList.size(); k++) {
					if (k == attributesList.size()-1) {
						identifier += attributesList.get(k);
					} else {
						identifier += attributesList.get(k) + ";";
					}
				}
				identifier += "|";
				out.write(identifier);
				identifier = "";
				out.write("\n");
				if (i == gteList.size()-1) {
					out.write(sequence);
				} else {
					out.write(sequence);
					out.write("\n");
				}
				
			}
			// close the output stream
			out.close();
		}catch (Exception e){
			// catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(MapLoci.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");

	}

}
