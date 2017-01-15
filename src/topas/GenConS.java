package topas;

import io.fasta.FastaIndexReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import core.fasta_index.FastaIndex;
import core.fasta_index.GetSequenceFromFastaIndex;
import core.sequence.OutputSequenceFormatter;
import gen_con_s.ConsensusBaseCaller;
import gen_con_s.DeletionCaller;
import gen_con_s.ReadIndels;
import gen_con_s.StatsCounter;
import gen_con_s.VcfSnpHolder;
import gen_con_s.VitalStr;
import topas.Topas.TOPASModule;
import topas.parameters.GenConSParameters;
import utils.newline_type.NewLineType;
import vcf.VcfEntry;
import vcf.VcfLineParser;

@TOPASModule(
		purpose = "generate a consensus sequence from a GATK Unified Genotyper generated VCF file"
		)

public class GenConS {
	
	private static boolean CHROM_CHANGE = false;
	
	private static boolean vcfB = false;
	private static BufferedWriter vcfWriter;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		long before = System.currentTimeMillis();

		GenConSParameters.createInstance(args);

		String snpsFile = GenConSParameters.getInstance().getParameter("snps").toString();
		String indelsFile = "";
		boolean indelsFileB = GenConSParameters.getInstance().getParameter("indels").isPresent();
		if (indelsFileB) {
			indelsFile = GenConSParameters.getInstance().getParameter("indels").toString();
		}
		String outputFile = GenConSParameters.getInstance().getParameter("o").toString();
		String refFile = GenConSParameters.getInstance().getParameter("ref").toString();
		boolean faiB = GenConSParameters.getInstance().getParameter("fai").isPresent();
		String fai = "";
		if (faiB) {
			fai = GenConSParameters.getInstance().getParameter("fai").toString();
		} else {
			if (!new File(refFile + ".fai").isFile()) {
				System.out.println();
				System.out.println("No fasta index specified or found, starting TOPAS.IndexFasta...");
				String cmdline = "-i " + refFile;
				topas.IndexFasta.main(cmdline.split(" "));
				System.out.println();
				System.out.println(GenConSParameters.class.getCanonicalName());
			}			
			fai = refFile + ".fai";
			System.out.println();
		}
		boolean consensusRatioB = GenConSParameters.getInstance().getParameter("consensus_ratio").isPresent();
		double consensusRatio = 0.75;
		if (consensusRatioB) {
			consensusRatio = Double.parseDouble(GenConSParameters.getInstance().getParameter("consensus_ratio").toString());
		}
		int majorAlleleCoverage = 5;
		boolean majorAlleleCoverageB = GenConSParameters.getInstance().getParameter("major_allele_coverage").isPresent();
		if (majorAlleleCoverageB) {
			majorAlleleCoverage = Integer.parseInt(GenConSParameters.getInstance().getParameter("major_allele_coverage").toString());
		}
		int totalCoverageThreshold = 0;
		boolean totalCoverageThresholdB = GenConSParameters.getInstance().getParameter("total_coverage").isPresent();
		if (totalCoverageThresholdB) {
			totalCoverageThreshold = Integer.parseInt(GenConSParameters.getInstance().getParameter("total_coverage").toString());
		}
		double punishmentRatio = 0.8;
		boolean punishmentRatioB = GenConSParameters.getInstance().getParameter("punishment_ratio").isPresent();
		if (punishmentRatioB) {
			punishmentRatio = Double.parseDouble(GenConSParameters.getInstance().getParameter("punishment_ratio").toString());
		}		
		String name = "";
		boolean nameB = GenConSParameters.getInstance().getParameter("name").isPresent();
		if (nameB) {
			name = GenConSParameters.getInstance().getParameter("name").toString();
		}
		boolean suppressWarn = false;
		boolean suppresWarnB = GenConSParameters.getInstance().getParameter("suppress_warn").isPresent();
		if (suppresWarnB) {
			suppressWarn = true;
		}
		vcfB = GenConSParameters.getInstance().getParameter("vcf_out").isPresent();
		String vcf = "";
		if (vcfB) {
			vcf = GenConSParameters.getInstance().getParameter("vcf_out").toString();
			vcfWriter = new BufferedWriter(new FileWriter(vcf));
			writeVcfHeader(refFile);
		}
		boolean minorB = GenConSParameters.getInstance().getParameter("minor").isPresent();

		// initialize BufferedWriter for log file
		BufferedWriter logWriter = new BufferedWriter(new FileWriter(outputFile + ".log"));

		System.out.println(GenConS.class.getCanonicalName().toString());
		logWriter.write(GenConS.class.getCanonicalName().toString());
		writeCurrentDate(logWriter);
		twoWrite(logWriter, "Use -? for help");

		twoWrite(logWriter, "Parameters chosen: ");
		twoWrite(logWriter, "SNPs VCF file        : " + snpsFile);
		if (indelsFileB) {
			twoWrite(logWriter, "INDELs VCF file      : " + indelsFile);
		}
		twoWrite(logWriter, "Output file          : " + outputFile);
		twoWrite(logWriter, "Reference fasta      : " + refFile);
		if (faiB) {
			twoWrite(logWriter, "Fasta index          : " + fai);
		}
		twoWrite(logWriter, "Consensus ratio      : " + consensusRatio);
//		twoWrite(logWriter, "Quality threshold    : " + qualityThreshold);
		twoWrite(logWriter, "Major allele coverage: " + majorAlleleCoverage);
		twoWrite(logWriter, "Total coverage       : " + totalCoverageThreshold);
		twoWrite(logWriter, "Punishment ratio     : " + punishmentRatio);
		if (nameB) {
			twoWrite(logWriter, "Name                 : " + name);
		}
		twoWrite(logWriter, "Suppress warnings    : " + suppressWarn);
		if (vcfB) {
			twoWrite(logWriter, "VCF output           : " + vcf);
		}
		if (minorB) {
			twoWrite(logWriter, "Minor call mode      : " + minorB);
		}

		twoWrite(logWriter, "");

		// calculate the newline type of the given fasta file
		String newLineType = NewLineType.calculateNewlineTypeString(refFile);
		// read in first entry of snp file
		VcfSnpHolder vcfSnpHolder = initializeVcfSnpHolder(snpsFile);
		// set current chromosome
		String curChrom = vcfSnpHolder.getVcfEntry().getChrom();
		// read in all the indels if necessary
		ArrayList<String> indelsWarning = new ArrayList<String>();
		HashMap<String, TreeMap<Integer, VcfEntry>> indels = readIndels(indelsFileB, indelsFile, indelsWarning);
		// check, if any warnings occured when the indels were read in
		if (!(indelsWarning.size() == 0)) {
			twoWrite(logWriter, "ReadIndels warnings:");
			for (String warning : indelsWarning) {
				twoWrite(logWriter, warning);
			}
			twoWrite(logWriter, "");
		}
		
		// read in all the fasta indices
		FastaIndexReader faiReader = new FastaIndexReader(fai);
		List<FastaIndex> faiList = faiReader.readFastaIndex();
		// load the fasta index corresponding to the current chromosome
		FastaIndex faidx = findFai(faiList, curChrom);
		// the current seq len
		int seqLen = faidx.getSequenceLength();
		// read the first sequence from the fasta file
		twoWrite(logWriter, "Reading reference sequence '" + curChrom + "'...");
		String seq = loadSeqFromFai(faiList, refFile, curChrom, newLineType, faidx);
		StringBuilder seqBuilder = new StringBuilder(seq);
		// now beginning with the creating of the consenus sequence
		twoWrite(logWriter, "Creating consensus sequence...");
		// initialize buffered writer with the output file
		BufferedWriter fastaWriter = new BufferedWriter(new FileWriter(outputFile));
		// write first identifier, including parameters!
		writeIdentifier(fastaWriter, curChrom, consensusRatio, majorAlleleCoverage, totalCoverageThreshold, punishmentRatio, name);
		// the line width of the resulting fasta file
		int lineWidth = 70;
		// initialize the buffered writer to where the ccf file is written
		BufferedWriter ccfWriter = new BufferedWriter(new FileWriter(outputFile + ".ccf"));
		// write the header of the ccf file
		writeCcfHeader(ccfWriter, refFile, snpsFile, indelsFile,
				consensusRatio, majorAlleleCoverage, totalCoverageThreshold,
				punishmentRatio, name);

		// the difference from the position in the reference compared to the
		// position in the seqBuilder due to indels
		int positionDifference = 0;
		// the SNP VCF entry related to current position
		VcfEntry curSnpEntry = new VcfEntry();
		// the INDEL VCF entry related to current position
		VcfEntry curIndelEntry = new VcfEntry();
		// initialize StatsCounter
		StatsCounter statsCounter = new StatsCounter(0, 0);
		statsCounter.setRefLen(seqLen);
		// create empty consensus base finder
		ConsensusBaseCaller consensusBaseCaller = new ConsensusBaseCaller(majorAlleleCoverage,
				consensusRatio, punishmentRatio, totalCoverageThreshold, curSnpEntry, statsCounter, minorB);
		// create empty deletion caller
		DeletionCaller deletionCaller = new DeletionCaller(new VcfEntry(), -1, consensusRatio);
		// did a deletion occur?
		boolean inDelMode = false;
		// at which position do we have to finish the current deletion?
		int delAimPos = -1;
		// the deletion is stored extra
		VcfEntry del = new VcfEntry();
		// empty base counter 
		double[] emptyBaseCounter = new double[5];
		
		// iterate over the whole sequence, making calls when possible, else write 'N' in consensus
		for (int faidxPos = 0; faidxPos < seqLen; faidxPos++) {
			int seqBuilderPos = faidxPos + positionDifference;
			int curPos = faidxPos + 1;
			
			// look up, if there exists a SNP VCF entry with current position and chromosome
			// if no, then an empty VCF entry is returnedo
			curSnpEntry = lookUpSnp(vcfSnpHolder, curChrom, curPos, CHROM_CHANGE);
			
			// check if chromosome changed:
			if (CHROM_CHANGE) {
				fastaWriter.newLine();
				// write out last sequence
				twoWrite(logWriter, "Writing consensus sequence...");
				fastaWriter.write(OutputSequenceFormatter.formatSequence(seqBuilder.toString(), lineWidth));
				fastaWriter.newLine();
				// set consensus length of statsCounter
				statsCounter.setConsSeqLen(seqBuilder.length());
				// write statistics out
				twoWrite(logWriter, statsCounter.toString());
				twoWrite(logWriter, "");
				// reset statsCounter
				statsCounter.reset();
				curChrom = vcfSnpHolder.getVcfEntry().getChrom();
				// write out new identifer + parameters used
				writeIdentifier(fastaWriter, curChrom, consensusRatio, majorAlleleCoverage, totalCoverageThreshold, punishmentRatio, name);
				// find new fasta index
				faidx = findFai(faiList, curChrom);
				// read the first sequence from the fasta file
				twoWrite(logWriter, "");
				twoWrite(logWriter, "Reading reference sequence '" + curChrom + "'...");
				seq = loadSeqFromFai(faiList, refFile, curChrom, newLineType, faidx);
				// set reference length of statsCounter
				statsCounter.setRefLen(seq.length());
				seqBuilder = new StringBuilder(seq);
				// now beginning with the creating of the consenus sequence
				twoWrite(logWriter, "Creating consensus sequence...");
				// update indices
				faidxPos = -1;
				seqLen = faidx.getSequenceLength();
				positionDifference = 0;
				CHROM_CHANGE = false;
			}
			// look up, if there exists a INDEL VCF entryi with current position and chromosome
			curIndelEntry = lookUpIndel(indels, curChrom, curPos);
			// inDelMode
			if (inDelMode) {
				// check, if we have to finish current deletion
				if (!curSnpEntry.isEmpty() && curSnpEntry.getChrom().equals(curChrom) && Integer.parseInt(curSnpEntry.getPos()) == curPos) {
					VcfEntry snp = new VcfEntry();
					snp.setEntry(curSnpEntry);
					deletionCaller.addSnp(snp);
				}
				if (delAimPos == curPos) {
					deletionCaller.finishDel();
					String snCallType = deletionCaller.getCallType();
					int delLen = deletionCaller.getDeletionLen();
					// we have no majority of either the deletion or the snps following it
					if (snCallType.equals("no")) {
						for (int i = curPos - delLen + 1; i < curPos; i++) {
							updateSeqCcf(ccfWriter, seqBuilder, i + positionDifference,
									curChrom, VitalStr.N, VitalStr.CONSENSUS_RATIO,
									positionDifference, emptyBaseCounter, deletionCaller.getRatio());
							// add consensus ratio to counter
							statsCounter.getTagCounter().addNumConsensusRatio();
						}
						// we have a call on deletion
					} else if (snCallType.equals("del")) {
						// add del call to counter
						statsCounter.getTagCounter().addNumDeletionCall();
						for (int i = curPos - delLen; i < curPos; i++) {
							// we must not alter the reference but the positions of the snps
							if (!(i == Integer.parseInt(deletionCaller.getDeletion().getPos()) - 1)) {
								String ref = String.valueOf(seqBuilder.charAt(i + positionDifference));
								writeCcfEntry(ccfWriter, curChrom, String.valueOf(i + 1), ref,
										VitalStr.DOT, VitalStr.DEL_CALL, emptyBaseCounter, deletionCaller.getRatio());
								seqBuilder.replace(i + positionDifference, i + 1 + positionDifference, VitalStr.EMPTY);
								positionDifference += -1;
							}
						}
						// we have a call on the snps
					} else {
						TreeMap<Integer, VcfEntry> consensusEntries = deletionCaller.getConsensusEntries();
						for (int i = curPos - delLen + 1; i < curPos; i++) {
							VcfEntry vE = null;
							if ((vE = consensusEntries.get(i + 1)) != null) {
								consensusBaseCaller.setEntryToCall(vE);
								consensusBaseCaller.findConsensusBase();
								snCallType = consensusBaseCaller.getCallType();
								String snBase = consensusBaseCaller.getBase();
								double[] baseCounter = consensusBaseCaller.getBaseCounter();
								double ratio = consensusBaseCaller.getRatio();
								evaluateSnCall(curChrom, seqBuilder, ccfWriter,
										positionDifference, statsCounter,
										i + positionDifference, snBase, snCallType,
										baseCounter, ratio);

							} else {
								updateSeqCcf(ccfWriter, seqBuilder, i + positionDifference, curChrom,
										VitalStr.N, VitalStr.NO_POS, positionDifference, emptyBaseCounter, 0.0);
								statsCounter.getTagCounter().addNumNoPos();
								// print out warning
								int j = i + 1;
								if (!suppressWarn) {
									twoWrite(logWriter, "[WARNING] There is noncallable VCF at " + curChrom + ":" + j + ".");
								}
							}
							consensusBaseCaller.reset();
						}
					}
					inDelMode = false;
					deletionCaller.reset();
					delAimPos = -1;
					consensusBaseCaller.reset();
				}
				// not inDelMode
			} else {
				// there was no call, we have to make the reference a 'N'
				if (curSnpEntry.isEmpty()) {
					updateSeqCcf(ccfWriter, seqBuilder, seqBuilderPos,
							curChrom, VitalStr.N, VitalStr.NO_POS, positionDifference, emptyBaseCounter, 0.0);
					statsCounter.getTagCounter().addNumNoPos();
					// print out warning
					if (!suppressWarn) {
						twoWrite(logWriter, "[WARNING] There is a noncallable VCF entry at " + curChrom + ":" + curPos + ".");
					}
					
					// we have a callable vcf entry
				} else {
					// process SNP entry
					consensusBaseCaller.setEntryToCall(curSnpEntry);
					consensusBaseCaller.findConsensusBase();
					String snBase = consensusBaseCaller.getBase();
					String snCallType = consensusBaseCaller.getCallType();
					double[] baseCounter = consensusBaseCaller.getBaseCounter();
					double ratio = consensusBaseCaller.getRatio();
					// reset caller
					consensusBaseCaller.reset();
					// process INDEL entry if possible
					String indelBase = VitalStr.EMPTY;
					String indelCallType = VitalStr.EMPTY;
					boolean isInsert = false;
					if (!curIndelEntry.isEmpty()) {
						consensusBaseCaller.setEntryToCall(curIndelEntry);
						consensusBaseCaller.findConsensusBase();
						indelBase = consensusBaseCaller.getBase();
						indelCallType = consensusBaseCaller.getCallType();
						isInsert = indelCallType.equals(VitalStr.INSERT_CALL);
					}

					// we have an insertion
					if (isInsert) {
						statsCounter.getTagCounter().addNumInsertCall();
						indelBase = indelBase.substring(1);
						// updateSeqCcf(ccfWriter, seqBuilder, seqBuilderPos + 1, curChrom, indelBase, indelCallType, positionDifference);
						evaluateSnIndelCall(curChrom, seqBuilder, ccfWriter, 
								positionDifference, statsCounter, seqBuilderPos,
								snBase, snCallType, indelBase, indelCallType,
								baseCounter, ratio);
						positionDifference += indelBase.length();		
					} else {
						// we may have a single nucleotide call
						evaluateSnCall(curChrom, seqBuilder, ccfWriter,
						positionDifference, statsCounter,
						seqBuilderPos, snBase, snCallType,
						baseCounter, ratio);
						
						// do we have a deletion?
						if (indelCallType.equals(VitalStr.DEL_CALL)) {
							del.setEntry(curIndelEntry);
							deletionCaller.setDeletion(del);
							inDelMode = true;
							delAimPos = curPos + del.getRef().length() - 1;
							curIndelEntry = new VcfEntry();
						}
					}
					consensusBaseCaller.reset();
				}	
			}
			// check if we have reached the last base of the current load seq
			if (faidxPos == seqLen - 1) {
				// check, if there is a chromosome change
				if (chromChange(curChrom, vcfSnpHolder)) {
					// write out current sequence
					// newline
					fastaWriter.newLine();
					// write out last sequence
					twoWrite(logWriter, "Writing consensus sequence...");
					fastaWriter.write(OutputSequenceFormatter.formatSequence(seqBuilder.toString(), lineWidth));
					fastaWriter.newLine();
					// set consensus length of statsCounter
					statsCounter.setConsSeqLen(seqBuilder.length());
					// write statistics out
					twoWrite(logWriter, statsCounter.toString());
					twoWrite(logWriter, "");
					// reset statsCounter
					statsCounter.reset();
					curChrom = vcfSnpHolder.getVcfEntry().getChrom();
					// write out new identifer + parameters used
					writeIdentifier(fastaWriter, curChrom, consensusRatio, majorAlleleCoverage, totalCoverageThreshold, punishmentRatio, name);
					// find new fasta index
					faidx = findFai(faiList, curChrom);
					// read the first sequence from the fasta file
					twoWrite(logWriter, "");
					twoWrite(logWriter, "Reading reference sequence '" + curChrom + "'...");
					seq = loadSeqFromFai(faiList, refFile, curChrom, newLineType, faidx);
					// set reference length of statsCounter
					statsCounter.setRefLen(seq.length());
					seqBuilder = new StringBuilder(seq);
					// now beginning with the creating of the consenus sequence
					twoWrite(logWriter, "Creating consensus sequence...");
					// update indices
					faidxPos = -1;
					seqLen = faidx.getSequenceLength();
					positionDifference = 0;
					CHROM_CHANGE = false;
				}
			}
		} // for loop ends here
		// newline
		fastaWriter.newLine();
		// write out last sequence
		twoWrite(logWriter, "Writing consensus sequence...");
		fastaWriter.write(OutputSequenceFormatter.formatSequence(seqBuilder.toString(), lineWidth));
		statsCounter.setConsSeqLen(seqBuilder.length());
		// write out statsCounter 
		twoWrite(logWriter, statsCounter.toString());
		fastaWriter.close();
		ccfWriter.close();
		if (vcfB) {
			vcfWriter.close();
		}

		long now = System.currentTimeMillis();
		twoWrite(logWriter, "");
		twoWrite(logWriter, GenConS.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
		logWriter.close();
	} // closing main part

	private static void writeVcfHeader(String referenceFile) throws IOException {
		String tab = "\t";
		vcfWriter.write("##reference=file: " + referenceFile);
		vcfWriter.newLine();
		vcfWriter.write("##FILTER=<ID=q11111, Description=\"Fake filter value\">");
		vcfWriter.newLine();
		vcfWriter.write("##FORMAT=<ID=GT, Number=1, Type=String, Description=\"Genotype\"");
		vcfWriter.newLine();
		vcfWriter.write("##FORMAT=<ID=AD, Number=1, Type=Integer, Description=\"Allele Depth\"");
		vcfWriter.newLine();
		vcfWriter.write("#CHROM" + tab + "ID" + tab + "REF" + tab + "ALT" + tab + "QUAL" + tab + "FILTER" + tab + "INFO" 
				+ tab + "FORMAT" + tab + "SAMPLE");
	}

	/**
	 * @param curChrom
	 * @param seqBuilder
	 * @param ccfWriter
	 * @param positionDifference
	 * @param statsCounter
	 * @param seqBuilderPos
	 * @param snBase
	 * @param snCallType
	 * @param indelBase
	 * @param indelCallType
	 * @param baseCounter
	 * @param ratio
	 * @throws IOException
	 * 
	 * only invoked when insertion has been found!!!
	 * 
	 */
	private static void evaluateSnIndelCall(String curChrom,
			StringBuilder seqBuilder, BufferedWriter ccfWriter,
			int positionDifference, StatsCounter statsCounter,
			int seqBuilderPos, String snBase, String snCallType,
			String indelBase, String indelCallType,
			double[] baseCounter, double ratio) throws IOException {
		String snIndelBase = snBase + indelBase;
		String snIndelCallType = snCallType + VitalStr.COMMA + indelCallType;
		if (snCallType.equals(VitalStr.SN_CALL)) {
			statsCounter.getTagCounter().addNumSnpCall();
			updateSeqCcf(ccfWriter, seqBuilder, seqBuilderPos, curChrom, snIndelBase, snIndelCallType, positionDifference,
					baseCounter, ratio);
		} else {
			snIndelBase = VitalStr.N + indelBase;
			updateSeqCcf(ccfWriter, seqBuilder, seqBuilderPos, curChrom, snIndelBase, snIndelCallType, positionDifference,
					baseCounter, ratio);
			if (snCallType.equals(VitalStr.CONSENSUS_RATIO)) {
				statsCounter.getTagCounter().addNumConsensusRatio();
			} else if (snCallType.equals(VitalStr.MAJOR_ALLELE_COVERAGE)) {
				statsCounter.getTagCounter().addNumMajorAlleleCoverage();
			} else if (snCallType.equals(VitalStr.TOTAL_COVERAGE)) {
				statsCounter.getTagCounter().addNumTotalCoverage();
			}
		}
	}

	private static void evaluateSnCall(String curChrom,
			StringBuilder seqBuilder, BufferedWriter ccfWriter,
			int positionDifference, StatsCounter statsCounter,
			int seqBuilderPos, String snBase, String snCallType,
			double[] baseCounter, double ratio)
			throws IOException {

		if (snCallType.equals(VitalStr.SN_CALL)) {
			statsCounter.getTagCounter().addNumSnpCall();
			updateSeqCcf(ccfWriter, seqBuilder, seqBuilderPos, curChrom, snBase, snCallType, positionDifference,
					baseCounter, ratio);
		} else {
			updateSeqCcf(ccfWriter, seqBuilder, seqBuilderPos, curChrom, VitalStr.N, snCallType, positionDifference,
					baseCounter, ratio);
			if (snCallType.equals(VitalStr.CONSENSUS_RATIO)) {
				statsCounter.getTagCounter().addNumConsensusRatio();
			} else if (snCallType.equals(VitalStr.MAJOR_ALLELE_COVERAGE)) {
				statsCounter.getTagCounter().addNumMajorAlleleCoverage();
			} else if (snCallType.equals(VitalStr.TOTAL_COVERAGE)) {
				statsCounter.getTagCounter().addNumTotalCoverage();
				// 	James
			} else if (snCallType.equals(VitalStr.PROBLEMATIC)) {
				statsCounter.getTagCounter().addNumProblematicPos();
			}
		}
	}
	
	private static VcfEntry lookUpIndel(
			HashMap<String, TreeMap<Integer, VcfEntry>> indels, String curChrom, int curPos) {
		VcfEntry indel = new VcfEntry();
		TreeMap<Integer, VcfEntry> indelsMap = null;
		if ((indelsMap = indels.get(curChrom)) != null) {
			if ((indel = indelsMap.get(curPos)) != null) {
				indel.setFormatAd();
				return indel;
			} else {
				return new VcfEntry();
			}
		} else {
			return new VcfEntry();
		}
	}

	private static VcfEntry lookUpSnp(VcfSnpHolder vcfSnpHolder,
			String curChrom, int curPos, boolean chromChange) throws IOException {
		VcfEntry snp = vcfSnpHolder.getVcfEntry();
		if (!vcfSnpHolder.isClosed()) {
			if (!snp.getChrom().equals(curChrom)) {
				chromChange = true;
				return new VcfEntry();
			}
		}
		double snpQual = snp.getQual();
		// can't do something with empty snp
		while (snpQual == -1.0) {
			if (vcfSnpHolder.isClosed()) {
				snp = new VcfEntry();
				break;
			} else {
				snp = vcfSnpHolder.getNextVcfEntry();
				snpQual = snp.getQual();
				if (!snp.isEmpty()) {
					if (!snp.getChrom().equals(curChrom)) {
						chromChange = true;
						break;
					}
				}
			}
		}
		if (!snp.isEmpty()) {
			String chrom = snp.getChrom();
			int pos = Integer.parseInt(snp.getPos());
			if (chrom.equals(curChrom) && pos == curPos) {
				if (!snp.setFormatAd()) {
					snp = new VcfEntry();
				}
				vcfSnpHolder.getNextVcfEntry();
				return snp;
			} else {
				if (!chrom.equals(curChrom)) {
					chromChange = true;
					return new VcfEntry();
				}
			}
		} else {
			chromChange = true;
			return new VcfEntry();
		}

		return new VcfEntry();
	}

	private static void writeIdentifier(BufferedWriter fastaWriter,
			String curChrom, double consensusRatio,
			int majorAlleleCoverage, int totalCoverageThreshold,
			double punishmentRatio, String name) throws IOException {
		StringBuilder sB = new StringBuilder();
		String dot = "."; 
		sB.append(">");	
		sB.append(curChrom);
		if (!name.equals("")) {
			sB.append(dot);
			sB.append(name);
		}
		sB.append(dot);
		sB.append("ratio_"); sB.append(consensusRatio);	sB.append(dot);
		sB.append("allele_cov_"); sB.append(majorAlleleCoverage); sB.append(dot);
		sB.append("total_cov_"); sB.append(totalCoverageThreshold); sB.append(dot);
		sB.append("punishment_"); sB.append(punishmentRatio);		
		fastaWriter.write(sB.toString());
	}

	private static void updateSeqCcf(BufferedWriter ccfWriter, StringBuilder seqBuilder,
			int seqBuilderPos, String chrom, String call, String info, int positionDifference,
			double[] baseCounter, double ratio) throws IOException {
		String ref = String.valueOf(seqBuilder.charAt(seqBuilderPos));
		if (call.equals(VitalStr.EMPTY)) {
			writeCcfEntry(ccfWriter, chrom, String.valueOf(seqBuilderPos + 1 - positionDifference), ref, VitalStr.DOT, info, baseCounter, ratio);
		} else {
			writeCcfEntry(ccfWriter, chrom, String.valueOf(seqBuilderPos + 1 - positionDifference), ref, call, info, baseCounter, ratio);
		}
		seqBuilder.replace(seqBuilderPos, seqBuilderPos + 1, call);
	}
	
	private static void writeCcfEntry(BufferedWriter ccfWriter, String chrom, String pos,
			String ref, String call, String info, double[] baseCounter, double ratio) throws IOException {
		StringBuilder ccfEntryBuilder = new StringBuilder();
		ccfEntryBuilder.append(chrom);
		ccfEntryBuilder.append(VitalStr.TAB);
		ccfEntryBuilder.append(pos);
		ccfEntryBuilder.append(VitalStr.TAB);
		ccfEntryBuilder.append(ref);
		ccfEntryBuilder.append(VitalStr.TAB);
		ccfEntryBuilder.append(call);
		ccfEntryBuilder.append(VitalStr.TAB);
		ccfEntryBuilder.append(info);
		ccfEntryBuilder.append(VitalStr.TAB);
		formatBaseCounter(ccfEntryBuilder, baseCounter); ccfEntryBuilder.append(VitalStr.TAB);
		// number formatter
//		ccfEntryBuilder.append(DOUBLE_FORMATTER.format(ratio));
		ccfEntryBuilder.append(ratio);
		writeNewLine(ccfWriter, ccfEntryBuilder.toString());
		
		// if vcf was selected
		if (vcfB) {
			StringBuilder vcfEntryBuilder = new StringBuilder();
			vcfEntryBuilder.append(chrom).append(VitalStr.TAB);
			vcfEntryBuilder.append(pos).append(VitalStr.TAB);
			// empty id
			vcfEntryBuilder.append(VitalStr.DOT).append(VitalStr.TAB);
			vcfEntryBuilder.append(ref).append(VitalStr.TAB);
			vcfEntryBuilder.append(call).append(VitalStr.TAB);
			vcfEntryBuilder.append("11111").append(VitalStr.TAB);
			vcfEntryBuilder.append("q11111").append(VitalStr.TAB);
			// empty info
			vcfEntryBuilder.append(VitalStr.DOT).append(VitalStr.TAB);
			vcfEntryBuilder.append("GT:AD").append(VitalStr.TAB);
			if (call.equals(VitalStr.N)) {
				vcfEntryBuilder.append("./.").append(VitalStr.COLON).append("0,0");
			} else if (call.equals(ref)) {
				vcfEntryBuilder.append("0/0").append(VitalStr.COLON).append("11111");
			} else {
				vcfEntryBuilder.append("1/1").append(VitalStr.COLON).append("0,11111");
			}
			writeNewLine(vcfWriter, vcfEntryBuilder.toString());
		}
	}

	private static void formatBaseCounter(StringBuilder ccfEntryBuilder, double[] baseCounter) {
		for (int i = 0; i < baseCounter.length; i++) {
			// number formatter
			// ccfEntryBuilder.append(DOUBLE_FORMATTER.format(baseCounter[i]));
			ccfEntryBuilder.append(baseCounter[i]);
			if (i != baseCounter.length-1) {
				ccfEntryBuilder.append(VitalStr.COMMA);
			}
		}
	}

	private static void writeCcfHeader(BufferedWriter ccfWriter,
			String refFile, String snpsFile, String indelsFile,
			double consensusRatio, int majorAlleleCoverage,
			int totalCoverageThreshold, double punishmentRatio, String name) throws IOException {
		if (!name.equals("")) {
			ccfWriter.write("##name: " + name);
			writeNewLine(ccfWriter, "##consensus sequence of reference: " + refFile);
		} else {
			ccfWriter.write("##consensus sequence of reference: " + refFile);
		}
		writeNewLine(ccfWriter, "##SNPs VCF file: " + snpsFile);
		writeNewLine(ccfWriter, "##INDELs VCF file: " + indelsFile);
		writeNewLine(ccfWriter, "##consensus ratio: " + consensusRatio);
		writeNewLine(ccfWriter, "##major allele coverage: " + majorAlleleCoverage);
		writeNewLine(ccfWriter, "##total coverage threshold: " + totalCoverageThreshold);
		writeNewLine(ccfWriter, "##punishment ratio: " + punishmentRatio);
		writeNewLine(ccfWriter, "#RefChrom" + VitalStr.TAB + "RefPos" + VitalStr.TAB + "RefBase" + VitalStr.TAB +
				"CallBase" + VitalStr.TAB + "Description" + VitalStr.TAB + "A,C,T,G,OTHER" + VitalStr.TAB + "Ratio");
	}

	private static void writeNewLine(BufferedWriter bW, String s) throws IOException {
		bW.newLine();
		bW.write(s);
	}

	private static boolean chromChange(String curChrom,
			VcfSnpHolder vcfSnpHolder) {
		boolean chromChange = false;
		if (!curChrom.equals(vcfSnpHolder.getVcfEntry().getChrom()) && !vcfSnpHolder.getVcfEntry().isEmpty()) {
			chromChange = true;
			return chromChange;
		} else {
			return chromChange;
		}
	}

	private static FastaIndex findFai(List<FastaIndex> faiList, String chrom) {
		for (FastaIndex fai : faiList) {
			String seqName = fai.getSequenceName();
			if (seqName.trim().equals(chrom.trim())) {
				return fai;
			} 
		}
		return new FastaIndex(null, -1, 0, 0, 0);
	}

	private static String loadSeqFromFai(List<FastaIndex> faiList,
			String fasta, String chrom, String newLineType, FastaIndex faidx) throws IOException {
		GetSequenceFromFastaIndex gSFFI = new GetSequenceFromFastaIndex(fasta, faidx);
		String seq = gSFFI.getSequence();
		seq = seq.replace(newLineType, "");
		return seq;
	}

	private static HashMap<String, TreeMap<Integer, VcfEntry>> readIndels(
			boolean indelsFileB, String indelsFile, ArrayList<String> indelsWarning) throws IOException {
		HashMap<String, TreeMap<Integer, VcfEntry>> indels = new HashMap<String, TreeMap<Integer,VcfEntry>>();
		if (indelsFileB) {
			ReadIndels rI = new ReadIndels(indelsFile);
			indels = rI.readIndels();
			// also get the warnings
			indelsWarning = rI.getWarningList();
		}
		return indels;
	}

	private static VcfSnpHolder initializeVcfSnpHolder(String inputFile) throws IOException {
		// initialize necessary VcfLineParser
		boolean t = true;
		boolean f = false;
		VcfLineParser vcfLineParser = new VcfLineParser(t, t, f, t, t, t, f, f, t, t);
		return new VcfSnpHolder(inputFile, vcfLineParser);
	}

	private static void writeCurrentDate(BufferedWriter logWriter) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//get current date time with Date()
		Date date = new Date();
		twoWrite(logWriter, dateFormat.format(date));
	}

	private static void twoWrite(BufferedWriter logWriter, String string) throws IOException {
		System.out.println(string);
		logWriter.newLine();
		logWriter.write(string);
	}

}
