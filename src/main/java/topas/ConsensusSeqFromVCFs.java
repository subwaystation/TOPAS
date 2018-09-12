package topas;

import io.fasta.FastaIndexReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.fasta_index.FastaIndex;
import core.fasta_index.GetSequenceFromFastaIndex;
import core.sequence.OutputSequenceFormatter;
import topas.Topas.TOPASModule;
import topas.parameters.ConsensusSeqFromVCFsParameters;
import utils.ArrayUtils;
import utils.newline_type.NewLineType;
import vcf.VcfEntry;
import vcf.VcfLineParser;
import vcf.VcfSnpLineHolder;

@TOPASModule(
		purpose = "generate a consensus sequence from the SNPs of several VCF files"
		)

public class ConsensusSeqFromVCFs {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		long before = System.currentTimeMillis();

		ConsensusSeqFromVCFsParameters.createInstance(args);

		String fasta = ConsensusSeqFromVCFsParameters.getInstance().getParameter("fasta").toString();
		boolean faiB = ConsensusSeqFromVCFsParameters.getInstance().getParameter("fai").isPresent();
		String fai = "";
		if (faiB) {
			fai = ConsensusSeqFromVCFsParameters.getInstance().getParameter("fai").toString();
		} else {
			if (!new File(fasta + ".fai").isFile()) {
				System.out.println();
				System.out.println("No fasta index specified or found, starting TOPAS.IndexFasta...");
				String cmdline = "-i " + fasta;
				topas.IndexFasta.main(cmdline.split(" "));
				System.out.println();
				System.out.println(ConsensusSeqFromVCFs.class.getCanonicalName());
			}			
			fai = fasta + ".fai";
			System.out.println();
		}
		String[] inputFiles = ConsensusSeqFromVCFsParameters.getInstance().getParameter("vcfs").getValues();
		String outputFile = ConsensusSeqFromVCFsParameters.getInstance().getParameter("o").toString();
		boolean ratioB = ConsensusSeqFromVCFsParameters.getInstance().getParameter("ratio").isPresent();
		double ratio = 0.8;
		if (ratioB) {
			ratio = Double.parseDouble(ConsensusSeqFromVCFsParameters.getInstance().getParameter("ratio").toString());
		}

		BufferedWriter bW = new BufferedWriter(new FileWriter(outputFile + ".log"));

		bW.write(ConsensusSeqFromVCFs.class.getCanonicalName().toString());
		System.out.println(ConsensusSeqFromVCFs.class.getCanonicalName().toString());
		twoWrite(bW, "Use -? for help");

		twoWrite(bW, "Parameters chosen: ");
		twoWrite(bW, "Fasta file           : " + fasta);
		twoWrite(bW, "Fasta index file     : " + fai);
		twoWrite(bW, "Input VCF files      : " + Arrays.toString(inputFiles));
		twoWrite(bW, "Output file          : " + outputFile);
		twoWrite(bW, "Ratio                : " + ratio);

		twoWrite(bW, "");

		// load fasta index
		FastaIndexReader faiReader = new FastaIndexReader(fai);
		List<FastaIndex> faiList = faiReader.readFastaIndex();

		// calculate required number of alternative SNPs with respect to the given ratio
		// math.ceil, double required!
		int requiredSnps = (int) Math.ceil(ratio * inputFiles.length);

		// read first line of each VCF file, put these into one list
		List<VcfSnpLineHolder> holderList = createHolderList(inputFiles);

		// the chromosome currently working on, first chrom is retrieved from holderList
		// it is assumed, that every VCF file has the same chromosomes in the same order
		// furthermore, every VCF file has at least one SNP on every chromosome
		String chrom = holderList.get(0).getVcfEntry().getChrom();
		twoWrite(bW, "Now working on chromosome " + chrom + ".");

		// load sequence from fasta index and fasta file
		String newLineType = NewLineType.calculateNewlineTypeString(fasta);
		System.out.println("Reading sequence from fasta file...");
		String seq = loadSeqFromFai(faiList, fasta, chrom, newLineType);
		StringBuilder seqBuilder = new StringBuilder(seq);
		System.out.println("Finished reading sequence.");
		System.out.println("Creating consensus sequence...");

		// the base position in the chromosome currently working on, in the beginning its one
		@SuppressWarnings("unused")
		int basePos = 1;

		// the minimum position of all VCF lines currently hold by the holderList
		// in the beginning -1, it will be calculated later
		int minPos = -1;

		// the total number of SNP positions per chromosome
		int totalSnpPosChrom = 0;

		// the total number of base changes in relation to the reference chromosome
		int totalBaseChangesChrom = 0;

		// initialize output writer
		int lineWidth = 70;
		BufferedWriter fastaWriter = new BufferedWriter(new FileWriter(outputFile));
		fastaWriter.write(">" + chrom);

		// iterate over all loaded VCF entries, as long there is at least one
		while (holderList.size() > 0) {
			totalSnpPosChrom++;
			// check, if there is at least one VCF entry with current chrom, if not change chrom and basePos
			// and load the new sequence from corresponding fasta index
			if (!checkHolderListForChrom(holderList, chrom)) {
				writeStatsOut(chrom, totalSnpPosChrom, totalBaseChangesChrom, bW);
				System.out.println("Writing consensus sequence...");
				System.out.println();

				fastaWriter.newLine();
				fastaWriter.write(OutputSequenceFormatter.formatSequence(seqBuilder.toString(), lineWidth));

				totalSnpPosChrom = 0;
				totalBaseChangesChrom = 0;

				chrom = holderList.get(0).getVcfEntry().getChrom();

				fastaWriter.newLine();
				fastaWriter.write(">" + chrom);

				basePos = 1;
				// load new seq
				twoWrite(bW, "Now working on chromosome " + chrom + ".");
				System.out.println("Reading sequence from fasta file...");
				seq = loadSeqFromFai(faiList, fasta, chrom, newLineType);
				seqBuilder = new StringBuilder(seq);
				System.out.println("Finished reading sequence.");
				System.out.println("Creating consensus sequence...");
			}
			// only get relevant VCF entries
			List<VcfEntry> vEList = filterHolderList(holderList, chrom);
			// find minimum pos of all VCF entries (check right chrom!)
			minPos = findMinimumSnpPos(vEList);
			// TODO pack the two upper methoods into one method, to save computation time!
			
			// calculate length of VCF entries having the minimum pos
			// with the same alt base
			// set alt base
			// (replace 'getFirstSingleAlt(vEList, minPos)' with alt base) DONE!
			StringBuilder altBase = new StringBuilder();
			int lenMinPosSameBase = lenMinPosFindBase(vEList, minPos, altBase);
			if (lenMinPosSameBase >= requiredSnps) {
				seqBuilder.replace(minPos -1 , minPos, altBase.toString());
				totalBaseChangesChrom++;				
			} 
			// set the basePos to minPos + 1
			basePos = minPos + 1;

			// for all minimum pos VCF entries getNextVcfEntryFromLine
			holderList = updateHolderList(minPos, holderList);
		}
		totalSnpPosChrom++;
		writeStatsOut(chrom, totalSnpPosChrom, totalBaseChangesChrom, bW);
		System.out.println("Writing consensus sequence...");

		fastaWriter.newLine();
		fastaWriter.write(OutputSequenceFormatter.formatSequence(seqBuilder.toString(), lineWidth));
		fastaWriter.close();

		long now = System.currentTimeMillis();
		twoWrite(bW, "");
		twoWrite(bW, ConsensusSeqFromVCFs.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");

		bW.close();
	}
	

	private static void twoWrite(BufferedWriter bW, String s) throws IOException {
		System.out.println(s);
		bW.newLine();
		bW.write(s);
	}

	private static void writeStatsOut(String chrom, int totalSnpPosChrom,
			int totalBaseChangesChrom, BufferedWriter bW) throws IOException {
		twoWrite(bW, "Statistics:");
		twoWrite(bW, "Total number of SNP positions: " + totalSnpPosChrom);
		twoWrite(bW, "Total number of base changes: " + totalBaseChangesChrom);
		float percentage = (float) totalBaseChangesChrom * 100f / (float) totalSnpPosChrom;
		twoWrite(bW, String.format("Percentage of base changes: %.4f", percentage));
		bW.newLine();
	}

	//	private static String getFirstSingleAlt(List<VcfEntry> vEList, int minPos) {
	//		String base = "";
	//		for (VcfEntry vE : vEList) {
	//			int pos = Integer.parseInt(vE.getPos());
	//			if (pos == minPos) {
	//				String alt = vE.getAlt();
	//				String[] altSplit = alt.split(",");
	//				for (int i = 0; i < altSplit.length; i++) {
	//					String alter = altSplit[i];
	//					if (alter.length() == 1) {
	//						base = alter;
	//						return base;
	//					}
	//				}
	//			}
	//		}
	//		return base;
	//	}

	private static List<VcfSnpLineHolder> updateHolderList(long minPos, List<VcfSnpLineHolder> holderList) throws IOException {
		List<VcfSnpLineHolder> updatedHolderList = new ArrayList<VcfSnpLineHolder>();
		for (VcfSnpLineHolder vcfSnpLineHolder : holderList) {
			long pos = Long.parseLong(vcfSnpLineHolder.getVcfEntry().getPos());
			if (pos == minPos) {
				VcfEntry vE = vcfSnpLineHolder.getNextVcfEntry();
				if (!(vE == null)) {
					updatedHolderList.add(vcfSnpLineHolder);
				}
			} else {
				updatedHolderList.add(vcfSnpLineHolder);
			}
		}
		return updatedHolderList;
	}

	private static int lenMinPosFindBase(List<VcfEntry> vEList, long minPos, StringBuilder altBase) {
		int len = 0;
		String comma = ",";
		String a = "A";
		String c = "C";
		String t = "T";
		String g = "G";
		// pos 0 = A; pos 1 = C, pos 2 = T, pos 3 = G
		int[] baseCount = new int[4];
		for (VcfEntry vE : vEList) {
			long pos = Long.parseLong(vE.getPos());
			if (pos == minPos) {
				String alt = vE.getAlt();
				String[] altSplit = alt.split(comma);

				// fill base counter
				for (String base : altSplit) {
					if (base.equals(a)) {
						baseCount[0] = baseCount[0] + 1;
					} else {
						if (base.equals(c)) {
							baseCount[1] = baseCount[1] + 1;
						} else {
							if (base.equals(t)) {
								baseCount[2] = baseCount[2] + 1;
							} else {
								if (base.equals(g)) {
									baseCount[3] = baseCount[3] + 1;
								}
							}
						}
					}
				}
			}
		}

		// resolve from base counter, which base to choose 
		// also calculate the maximum length of each possible base
		int[] maxPosMaxValue = ArrayUtils.findMaxPosMaxValue(baseCount);
		int maxPos = maxPosMaxValue[0];
		int maxValue = maxPosMaxValue[1];
		len = maxValue;
		if (maxPos == 0) {
			altBase.append(a);
		} else {
			if (maxPos == 1) {
				altBase.append(c);
			} else {
				if (maxPos == 2) {
					altBase.append(t);
				} else {
					if (maxPos == 3) {
						altBase.append(g);
					}
				}
			}
		}
		return len;
	}

	private static List<VcfEntry> filterHolderList(List<VcfSnpLineHolder> holderList, String chrom) {
		List<VcfEntry> vEList = new ArrayList<VcfEntry>();
		for (VcfSnpLineHolder vcfSnpLineHolder : holderList) {
			VcfEntry vE = vcfSnpLineHolder.getVcfEntry();
			String chromPresent = vE.getChrom();
			if (chromPresent.equals(chrom)) {
				vEList.add(vE);
			}
		}
		return vEList;		
	}

	private static int findMinimumSnpPos(List<VcfEntry> vEList) {
		int minPos = Integer.MAX_VALUE;
		for (VcfEntry vE : vEList) {
			int pos = Integer.parseInt(vE.getPos());
			minPos = Math.min(pos, minPos);
		}
		return minPos;
	}

	private static String loadSeqFromFai(List<FastaIndex> faiList,
			String fasta, String chrom, String newLineType) throws IOException {
		FastaIndex fai = findFai(faiList, chrom);
		GetSequenceFromFastaIndex gSFFI = new GetSequenceFromFastaIndex(fasta, fai);
		String seq = gSFFI.getSequence();
		seq = seq.replace(newLineType, "");
		return seq;
	}

	private static FastaIndex findFai(List<FastaIndex> faiList, String chrom) {
		for (FastaIndex fai : faiList) {
			String seqName = fai.getSequenceName();
			if (seqName.equals(chrom)) {
				return fai;
			} 
		}
		return new FastaIndex(null, -1, 0, 0, 0);
	}

	private static boolean checkHolderListForChrom(
			List<VcfSnpLineHolder> holderList, String chrom) {
		boolean chromPresent = false;
		for (VcfSnpLineHolder vcfSnpLineHolder : holderList) {
			String chromToCompare = vcfSnpLineHolder.getVcfEntry().getChrom();
			if (chromToCompare.equals(chrom)) {
				chromPresent = true;
				return chromPresent;
			}
		}
		return chromPresent;
	}

	private static List<VcfSnpLineHolder> createHolderList(String[] inputFiles) throws IOException {
		List<VcfSnpLineHolder> holderList = new ArrayList<VcfSnpLineHolder>();
		boolean t = true;
		boolean f = false;
		VcfLineParser vcfLineParser = new VcfLineParser(t, t, f, t, t, f, f, f, f, f);
		for (String inputFile : inputFiles) {
			VcfSnpLineHolder vcfSnpLineHolder = new VcfSnpLineHolder(inputFile, vcfLineParser);
			holderList.add(vcfSnpLineHolder);
		}
		return holderList;
	}
}
