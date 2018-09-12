package topas;

import feature_format.gff.GffThreeEntry;
import feature_format.gff.filter.GffThreeFilter;
import feature_format.gff.gene_loci_mapper.GetSequenceFromGeneLocus;
import io.fasta.FastaIndexReader;
import io.feature_format.GffThreeReader;
import io.feature_format.GffThreeWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;

import core.fasta_index.FastaIndex;
import core.sequence.OutputSequenceFormatter;
import test.MapLoci;
import topas.Topas.TOPASModule;
import topas.parameters.FilterGFF3Parameters;
import utils.newline_type.NewLineType;

@TOPASModule(
		purpose = "a GFF3 file can be filtered by seqid + range, source, type, score, strand, phase, attribute"
		)

public class FilterGFF3 {

	public static void main(String[] args) throws IOException {
		System.out.println(FilterGFF3.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		FilterGFF3Parameters.createInstance(args);

		String inputFile = FilterGFF3Parameters.getInstance().getParameter("i").toString();
		String gff = FilterGFF3Parameters.getInstance().getParameter("gff").toString();
		String outputFile = FilterGFF3Parameters.getInstance().getParameter("o").toString();
		String col = FilterGFF3Parameters.getInstance().getParameter("col").toString();
		String fa = FilterGFF3Parameters.getInstance().getParameter("fa").toString();
		String fai = FilterGFF3Parameters.getInstance().getParameter("fai").toString();
		//		String[] l = MapLociParameters.getInstance().getParameter("l").getValues();
		String[] seqidRange = FilterGFF3Parameters.getInstance().getParameter("seqidRange").getValues();
		String[] sources = FilterGFF3Parameters.getInstance().getParameter("source").getValues();
		String[] types = FilterGFF3Parameters.getInstance().getParameter("type").getValues();
		String[] scores = FilterGFF3Parameters.getInstance().getParameter("score").getValues();
		String[] strands = FilterGFF3Parameters.getInstance().getParameter("strand").getValues();
		String[] phases = FilterGFF3Parameters.getInstance().getParameter("phase").getValues();
		String[] attributes = FilterGFF3Parameters.getInstance().getParameter("attribute").getValues();
		boolean seq = FilterGFF3Parameters.getInstance().getParameter("seq").isPresent();
		System.out.println();

		//		boolean linesMap = MapLociParameters.getInstance().getParameter("l").isPresent();
		boolean input = FilterGFF3Parameters.getInstance().getParameter("i").isPresent();
		boolean iDR = FilterGFF3Parameters.getInstance().getParameter("seqidRange").isPresent();
		boolean soc = FilterGFF3Parameters.getInstance().getParameter("source").isPresent();
		boolean t = FilterGFF3Parameters.getInstance().getParameter("type").isPresent();
		boolean sco = FilterGFF3Parameters.getInstance().getParameter("score").isPresent();
		boolean st = FilterGFF3Parameters.getInstance().getParameter("strand").isPresent();
		boolean p = FilterGFF3Parameters.getInstance().getParameter("phase").isPresent();
		boolean a = FilterGFF3Parameters.getInstance().getParameter("attribute").isPresent();
		boolean c = FilterGFF3Parameters.getInstance().getParameter("col").isPresent();
		boolean fab = FilterGFF3Parameters.getInstance().getParameter("fa").isPresent();
		boolean faib = FilterGFF3Parameters.getInstance().getParameter("fai").isPresent();

		//		// if both lines to map and seqIds to map are null, then no gene loci was specified
		//		if(!linesMap&&!iD) {
		//			System.err.println("Please specify the line(s) to map using '-l int int ...' OR \n       specify the seqIds and the range(s) to map using\n       '-seqId seqId1 seqId2 ...' AND '-range start1_end1 start2_end2 ...'.");
		//			System.exit(1);
		//		}
		//		if (!linesMap) {
		//			l = new String[1];
		//			l[0] = "none";
		//		}
		if (seq) {
			if (!fab) {
				System.err.println("Please specify the location of the fasta file, from where the sequences should be read using '-fa input.fasta'.");
				System.exit(1);
			}
			if (!faib) {
				System.err.println("Please specify the location of the fasta index of the entered fasta file using '-fai input.fai'.");
				System.exit(1);
			}
			
		}
		if (!iDR && !input) {
			System.err.println("Please specify either the SeqIdRanges to filter using '-seqidRange SEQID:START-END' OR a file and the column in this file, where SeqIdRanges can be found for filtering using '-i inputFile' and '-col int'.");
			System.exit(1);
		}
		if (!c) {
			col = "2";
		}
		if (!iDR) {
			seqidRange = new String[1];
			seqidRange[0] = "NO FILTERING SPECIFIED";
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
		if (input) {
			System.out.println("Input file              : "+inputFile);
			System.out.println("Column in input file    : "+col);
		}
		System.out.println("Gff3 file               : "+gff);
		System.out.println("Output file             : "+outputFile);
		if (seq) {
			System.out.println("Fasta file           : "+fa);
			System.out.println("Fasta index file     : "+fai);
		}
		//		System.out.println("Line(s) to map          : "+Arrays.toString(l));
		System.out.println("SeqIdRange(s) to filter : "+Arrays.toString(seqidRange));
		System.out.println("Source(s) to filter     : "+Arrays.toString(sources));
		System.out.println("Type(s) to filter       : "+Arrays.toString(types));
		System.out.println("Score(s) to filter      : "+Arrays.toString(scores));
		System.out.println("Strand(s) to filter     : "+Arrays.toString(strands));
		System.out.println("Phase(s) to filter      : "+Arrays.toString(phases));
		System.out.println("Attribute(s) to filter  : "+Arrays.toString(attributes));
		System.out.println();
		if (iDR && input) {
			System.out.println("You specified a file with SeqIdRanges and a single SeqIdRange,"
					+ "\nthe single SeqIdRange will be ignored.");
		}

		System.out.println();

		//		// add the lines to map to an array list
		//		List<GffThreeEntry> linesMapList = new ArrayList<GffThreeEntry>();
		//		if (linesMap) {
		//			// create a set of the lines to map
		//			HashSet<Integer> linesToFilter = new HashSet<Integer>();
		//			for(int i = 0; i < l.length; i++) {
		//				linesToFilter.add(Integer.parseInt(l[i]));
		//			}
		//			// now fetch the corresponding gff3 entries
		//			GffThreeReader gReader = new GffThreeReader(gff, linesToFilter);
		//			linesMapList = gReader.getMappedLines();
		//		}
		// final list of gff3 entries to map
		//		List<GffThreeEntry> gteList = new ArrayList<GffThreeEntry>();		

		// add the gff3 entries specified by seqidRange, source, type, score, strand, phase, attributes
		List<GffThreeEntry> filteredGffThreeEntryList = new ArrayList<GffThreeEntry>();
		
		List<String> seqIdsToFilter = new ArrayList<String>();
		if (input) {
			seqIdsToFilter = readSeqIdsToFilter(inputFile, Integer.parseInt(col));
		} else {
			seqIdsToFilter = new ArrayList<String>(Arrays.asList(seqidRange));
		}
		

		List<String> scoresToFilter = new ArrayList<String>();
		if (sco) {
			scoresToFilter = new ArrayList<String>(Arrays.asList(scores));
		}
		List<String> sourcesToFilter = new ArrayList<String>();
		if (soc) {
			for (int i = 0; i < sources.length; i++) {
				sourcesToFilter.add(sources[i].trim());
			}
		}
		List<String> typesToFilter = new ArrayList<String>();
		if (t) {
			for (int i = 0; i < types.length; i++) {
				typesToFilter.add(types[i].trim());
			}
		}
		List<String> strandsToFilter = new ArrayList<String>();
		if (st) {
			for (int i = 0; i < strands.length; i++) {
				strandsToFilter.add(strands[i].trim());
			}
		}
		List<String> phasesToFilter = new ArrayList<String>();
		if (p) {
			for (int i = 0; i < phases.length; i++) {
				phasesToFilter.add(phases[i].trim());
			}
		}
		List<String> attributesToFilter = new ArrayList<String>();
		if (a) {
			attributesToFilter = new ArrayList<String>(Arrays.asList(attributes));
		}
		GffThreeFilter gFilter = new GffThreeFilter(gff, seqIdsToFilter, sourcesToFilter, typesToFilter, scoresToFilter, strandsToFilter, phasesToFilter, attributesToFilter);
		filteredGffThreeEntryList = gFilter.getFilteredLines();
		
		String newLineType = NewLineType.calculateNewlineTypeString(fa);
		
		if (!seq) {
			GffThreeReader gTR = new GffThreeReader(gff);
			String header = gTR.scanHeader();
			
			GffThreeWriter gWriter = new GffThreeWriter(outputFile, filteredGffThreeEntryList, header);
			gWriter.writeGffThree();
			
			System.out.println();
			System.out.println("The resulting GFF3 File can be found at " + outputFile);
			System.out.println();
		} else {
			// read in the fasta index
			System.out.println("Reading fasta index from: " + fai);
			System.out.println();
			FastaIndexReader fIR = new FastaIndexReader(fai);
			List<FastaIndex> faidxList = fIR.readFastaIndex();
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
				for (int i = 0; i < filteredGffThreeEntryList.size(); i++) {
					GffThreeEntry gTE = filteredGffThreeEntryList.get(i);
					String seqId = gTE.getSeqId();
					int start = gTE.getStart();
					int end = gTE.getEnd();
					for(int j = 0; j < faidxList.size(); j++) {
						if(faidxList.get(j).getSequenceName().contains(seqId)) {
							faidxIndex = j;
						}
					}
					GetSequenceFromGeneLocus getGeneLocus = new GetSequenceFromGeneLocus(fa, faidxList.get(faidxIndex), start, end);
					String s = getGeneLocus.getSequenceFromGeneLocus();
					s = s.replace(newLineType, "");
					sequence = OutputSequenceFormatter.formatSequence(s, 80);		
					
					
					identifier += gTE.getSeqId() + "|" + gTE.getSource() + "|" + gTE.getType() + "|" + gTE.getStart() + "|" +
							gTE.getEnd() + "|" + gTE.getScore() + "|" + gTE.getStrand() + "|" + gTE.getPhase() + "|";
					SortedMap<String, String> attributesE = gTE.getAttributes();
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
					if (i == filteredGffThreeEntryList.size()-1) {
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
		}

		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(MapLoci.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
	}

	private static List<String> readSeqIdsToFilter(String inputFile, int col) throws IOException {
		List<String> seqIdsToFilter = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		String line;

		while ((line = br.readLine()) != null) {
			String[] lineSplit = line.split("\t");
			if (col > lineSplit.length) {
				System.out.println("The column you specified does not exist in " + inputFile + ". Please specify the right column.");
				System.exit(1);
			} else {
				seqIdsToFilter.add(lineSplit[col-1]);
			}
		}
		br.close();
		return seqIdsToFilter;
	}
}
