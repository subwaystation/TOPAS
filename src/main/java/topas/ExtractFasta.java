package topas;

import io.fasta.FastaIndexReader;
import io.fasta.FastaIndexWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import core.fasta_index.FastaIndexSequenceLengthSorter;
import core.fasta_index.FastaIndex;
import core.fasta_index.FastaIndexIdentifierSorter;
import core.fasta_index.FastaIndexPatternMatcher;
import core.fasta_index.GetSequenceFromFastaIndex;
import topas.Topas.TOPASModule;
import topas.parameters.ExtractFastaParameters;

@TOPASModule(
		purpose = "sort a fasta file and return only the fasta sequences which match a given pattern"
		)


public class ExtractFasta {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(ExtractFasta.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		ExtractFastaParameters.createInstance(args);
		
		String inputFile = ExtractFastaParameters.getInstance().getParameter("i").toString();
		String faidxFile = ExtractFastaParameters.getInstance().getParameter("faidx").toString();
		String pattern = ExtractFastaParameters.getInstance().getParameter("pattern").toString();
		String[] sort = ExtractFastaParameters.getInstance().getParameter("sort").getValues();
		if(sort==null) {
			sort = new String[1];
			sort[0] = "--";
		}
		String outputFile = ExtractFastaParameters.getInstance().getParameter("o").toString();
		System.out.println();
		if(pattern.equals("--")&&sort[0].equals("--")) {
			System.err.println("Please set the 'sort' parameter and/or the \'pattern\' parameter to run 'ExtractFasta'.");
			System.exit(1);
		}
		
		// verify sort parameters
		verifySortParameters(sort);
				
		System.out.println("Parameters chosen: ");
		System.out.println("Input file           : "+inputFile);
		System.out.println("Fasta index file     : "+faidxFile);
		System.out.println("Pattern              : "+pattern);
		System.out.println("Sort parameters      : "+sort[0]);
		System.out.println("Output file          : "+outputFile);
		
		System.out.println();
		System.out.println("Reading fasta index from: " + faidxFile);
		FastaIndexReader fIR = new FastaIndexReader(faidxFile);
		List<FastaIndex> faidxList = fIR.readFastaIndex();
		
		if(outputFile.equals("--")) {
			outputFile = inputFile;
		}
		if(outputFile.endsWith(".extract")) {
			outputFile = outputFile.substring(0, outputFile.length()-8);
		}
		if(outputFile.endsWith(".extract.fai")) {
			outputFile = outputFile.substring(0, outputFile.length()-12);
		}
		
		// user only wants to extract sequences by a certain pattern without sorting the resulting sequences
		if(sort[0].equals("--")) {
			FastaIndexPatternMatcher fIPM = new FastaIndexPatternMatcher(faidxList, pattern);
			faidxList = fIPM.getMatchedFaidxList();
			FastaIndexWriter fIW = new FastaIndexWriter(outputFile+".extract.fasta", faidxList);
			fIW.writeFastaIndex();
		}
		
		// the user only wants to sort the fasta file
		if(pattern.equals("--")) {
			sortAndWriteFasta(sort, faidxList, outputFile, inputFile);
		}
		
		// the user wants to match a specific pattern and sort the resulting fastaIndexList
		if(!pattern.equals("--")&&!sort.equals("--")) {
			// match pattern and get new list
			FastaIndexPatternMatcher fIPM = new FastaIndexPatternMatcher(faidxList, pattern);
			faidxList = fIPM.getMatchedFaidxList();
			// sort and write new list
			sortAndWriteFasta(sort, faidxList, outputFile, inputFile);			
		}

		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(ExtractFasta.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
	}

	private static void sortAndWriteFasta(String[] sort, List<FastaIndex> faidxList, String outputFile, String inputFile) throws IOException {
		Integer[] faidxIndices;
		if(sort[0].toLowerCase().equals("identifier")) {
			FastaIndexIdentifierSorter fIIS = new FastaIndexIdentifierSorter(faidxList);
			if(sort[1].toLowerCase().equals("asc")) {
				faidxIndices = fIIS.sortAsc();
			} else {
				faidxIndices = fIIS.sortDesc();
			}
			
		} else {
			FastaIndexSequenceLengthSorter fISLS = new FastaIndexSequenceLengthSorter(faidxList);
			if(sort[1].toLowerCase().equals("asc")) {
				faidxIndices = fISLS.sortAsc();
			} else {
				faidxIndices = fISLS.sortDesc();
			}
		}
		
		// write resulting fasta file
		FileWriter fstream = new FileWriter(outputFile + ".extract.fasta");
		System.out.println("Writing sorted sequences to: " + outputFile + ".extract.fasta");
		BufferedWriter out = new BufferedWriter(fstream);
		for(int i = 0; i < faidxIndices.length; i++) {
			FastaIndex faidx;
			faidx = faidxList.get(faidxIndices[i]);
			GetSequenceFromFastaIndex gSFFI = new GetSequenceFromFastaIndex(inputFile, faidx);
			String sequence = gSFFI.getSequence();
			String identifier = faidx.getSequenceName();
			if(!(i == (faidxIndices.length-1))) {
				out.write(">" + identifier + "\n" + sequence + "\n");
			} else {
				out.write(">" + identifier + "\n" + sequence);
			}
		}
		//Close the output stream
		out.close();
	}

	private static void verifySortParameters(String[] sort) {
		boolean fastaPart = false;
		boolean sortOrder = false;
		if(!sort[0].equals("--")) {
			if(sort[0].toLowerCase().equals("identifier")||sort[0].toLowerCase().equals("sequence")) {
				fastaPart = true;
			}
			if(sort[1].toLowerCase().equals("asc")||sort[1].toLowerCase().equals("desc")) {
				sortOrder = true;
			}
		}
		if(!fastaPart || !sortOrder) {
			System.err.println("Please specify if you want to sort the fasta file by \'identifier\' or by \'sequence\'.\n"
				+ "Also specify if you want to sort \'asc\' or \'desc\'");
			System.exit(1);
		}
	}

}
