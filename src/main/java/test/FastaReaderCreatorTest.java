package test;


import io.fasta.FastaIndexCreator;
import io.fasta.FastaReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import core.fasta_index.FastaIndex;
import core.fasta_index.GetSequenceFromFastaIndex;
import core.fasta_index.GetSequencesFromFastaIndices;
import core.sequence.ISequence;


public class FastaReaderCreatorTest {

	private static final String STAPHYLOCUCCUS_AUREUS = "StaphylococcusAureus.fasta";
	private static final String Cyanobacteria_bacterium_Yellowstone_A_Prime_uid16251 = "CP000239.faa";
	private static final String Test = "test.fasta";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {


		// test the FastaReader
		//		 System.out.println("Reading " + FastaReaderCreatorTest.STAPHYLOCUCCUS_AUREUS);
		//         FastaReader reader = new FastaReader(
		//                         FastaReader.FastaFileSequenceType.DNA_SEQUENCE,
		//                         FastaReaderCreatorTest.STAPHYLOCUCCUS_AUREUS);
		//         List<ISequence> sA = reader.scanFile();
		//         for (ISequence s : sA) {
		//                 System.out.println(s);
		//         }
		//         for (ISequence s : sA) {
		//             System.out.println(s.toTab());
		//         }
		//         System.out.println("Loaded " + sA.size() + " sequences.");


		// test the FastaIndexCreator
		long start = System.currentTimeMillis();
		FastaIndexCreator indexCreator = new FastaIndexCreator(Cyanobacteria_bacterium_Yellowstone_A_Prime_uid16251);
		List<FastaIndex> lFi = indexCreator.createFastaIndex();
		for (FastaIndex fI : lFi) {
			System.out.println(fI);
		}
		System.out.println("Loaded " + lFi.size() + " indices.");
		long stop = System.currentTimeMillis();
		System.out.println("Loading in Seconds = "+ ((stop-start)/1000));
		System.out.println("Writing file to disk.");
		try{
			// Create file 
			FileWriter fstream = new FileWriter(Cyanobacteria_bacterium_Yellowstone_A_Prime_uid16251 + ".fai");
			BufferedWriter out = new BufferedWriter(fstream);
			for (int i = 0; i < lFi.size(); i++) {
				if(!(i == (lFi.size()-1))) {
					out.write(lFi.get(i).toString() + "\n");
				} else {
					out.write(lFi.get(i).toString());
				}

			}
			//Close the output stream
			out.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
//		String[] sequences = new String[lFi.size()];
//		for(int i = 0; i < lFi.size(); i++) {
//			GetSequenceFromFastaIndex getSeq = new GetSequenceFromFastaIndex(STAPHYLOCUCCUS_AUREUS, lFi.get(i));
//			sequences[i] = getSeq.getSequenceSingle();
//		}
//		GetSequenceFromFastaIndex sFI = new GetSequenceFromFastaIndex(STAPHYLOCUCCUS_AUREUS, lFi.get(0));
//		String sequence = sFI.getSequenceSingle();
//		System.out.println(sequence);
		GetSequencesFromFastaIndices sI = new GetSequencesFromFastaIndices(Cyanobacteria_bacterium_Yellowstone_A_Prime_uid16251, lFi);
		String[] sequences = sI.getSequences();
		for(int i = 0; i < sequences.length; i++) {
			System.out.println(sequences[i] + "\n");
		}
	}

}
