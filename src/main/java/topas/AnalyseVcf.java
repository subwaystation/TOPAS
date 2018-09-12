package topas;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import comparison.RegionSeqIdStartEndComparatorAsc;
import io.vcf.VcfReader;
import topas.Topas.TOPASModule;
import topas.parameters.AnalyseVcfParameters;
import vcf.CoverageFraction;
import vcf.VcfEntry;
import vcf.analyse.Region;
import vcf.analyse.VcfAnalyser;
import vcf.analyse.VcfEntryPreAnalyser;
import vcf.analyse.window.AFilterWindow;
import vcf.analyse.window.WindowMerger;

@TOPASModule(
		purpose = "analyse a given vcf file by given windows"
		)

public class AnalyseVcf {
	
	/**
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {

		long before = System.currentTimeMillis();

		AnalyseVcfParameters.createInstance(args);

		String inputFile = AnalyseVcfParameters.getInstance().getParameter("vcf").toString();
		String outputFile = AnalyseVcfParameters.getInstance().getParameter("o").toString();
		

		// initialize BufferedWriter for log file
		BufferedWriter logWriter = new BufferedWriter(new FileWriter(outputFile + ".log"));

		System.out.println(AnalyseVcf.class.getCanonicalName().toString());
		logWriter.write(AnalyseVcf.class.getCanonicalName().toString());
		writeCurrentDate(logWriter);
		twoWrite(logWriter, "Use -? for help");

		twoWrite(logWriter, "Parameters chosen: ");
		twoWrite(logWriter, "Input VCF file        : " + inputFile);
		twoWrite(logWriter, "Output TSV file       : " + outputFile);
		boolean coverageWindowParametersB = AnalyseVcfParameters.getInstance().getParameter("coverage_window").isPresent();
		String[] coverageWindowParameters = new String[] {"3.0", "50", "5"};
		if (coverageWindowParametersB) {
			coverageWindowParameters = AnalyseVcfParameters.getInstance().getParameter("coverage_window").getValues();
		}
		twoWrite(logWriter, "Coverage window       : " + Arrays.toString(coverageWindowParameters));
		boolean snpAbsWindowParametersB = AnalyseVcfParameters.getInstance().getParameter("snp_abs_window").isPresent();
		String[] snpAbsWindowParameters = new String[] {"2", "5", "1"};
		if (snpAbsWindowParametersB) {
			snpAbsWindowParameters = AnalyseVcfParameters.getInstance().getParameter("snp_abs_window").getValues();
		}
		twoWrite(logWriter, "SNP absolute window   : " + Arrays.toString(snpAbsWindowParameters));
		boolean snpFacWindowParametersB = AnalyseVcfParameters.getInstance().getParameter("snp_fac_window").isPresent();
		String[] snpFacWindowParameters = new String[] {"5.0", "100", "10"};
		if (snpFacWindowParametersB) {
			snpFacWindowParameters = AnalyseVcfParameters.getInstance().getParameter("snp_fac_window").getValues();
		}
		twoWrite(logWriter, "SNP factor window     : " + Arrays.toString(snpFacWindowParameters));
		boolean heterozygousWindowParametersB = AnalyseVcfParameters.getInstance().getParameter("heterozygous_window").isPresent();
		String[] heterozygousWindowParameters = new String[] {"3", "100", "10", "0.9", "0.8"};
		if (heterozygousWindowParametersB) {
			heterozygousWindowParameters = AnalyseVcfParameters.getInstance().getParameter("heterozygous_window").getValues();
		}
		twoWrite(logWriter, "Heterozygous window   : " + Arrays.toString(heterozygousWindowParameters));
		boolean uncoveredPosWindowParametersB = AnalyseVcfParameters.getInstance().getParameter("uncovered_pos_window").isPresent();
		String[] uncoveredPosWindowParameters = new String[] {"25.0", "100", "10"};
		if (uncoveredPosWindowParametersB) {
			uncoveredPosWindowParameters = AnalyseVcfParameters.getInstance().getParameter("uncovered_sspos_window").getValues();
		}
		twoWrite(logWriter, "Uncovered pos window  : " + Arrays.toString(uncoveredPosWindowParameters));
		boolean mergeRegionsParameterB = AnalyseVcfParameters.getInstance().getParameter("merge_regions").isPresent();
		twoWrite(logWriter, "Merge regions         : " + mergeRegionsParameterB);
		boolean contigNamesParameterB = AnalyseVcfParameters.getInstance().getParameter("contig_names").isPresent();
		String[] contigNamesParameters = new String[0];
		if (contigNamesParameterB) {
			contigNamesParameters = AnalyseVcfParameters.getInstance().getParameter("contig_names").getValues();
			twoWrite(logWriter, "Contigs               : " + Arrays.toString(contigNamesParameters));
		} else {
			twoWrite(logWriter, "Contigs               : " + "ALL");
		}
		twoWrite(logWriter, "");
		
		// read in all vcf entries with respective contig identifiers and contig lengths
		VcfReader vcfReader = new VcfReader(inputFile);
		// VcfEntry[] vcfEntries = vcfReader.getVcfEntriesArray();
		HashMap<String, List<VcfEntry>> vcfEntriesMap = vcfReader.parseVcfFile(contigNamesParameters);
		List<String> contigs = vcfReader.getIdentifiers();
		List<Integer> lengths = vcfReader.getContigLengths();
		BufferedWriter gff3Buffer = new BufferedWriter(new FileWriter(outputFile));
		gff3Buffer.write("##gff-version 3");
		gff3Buffer.newLine();
		for (int i = 0; i < contigs.size(); i++) {
			writeGff3Header(contigs.get(i), lengths.get(i), gff3Buffer);
		}
		
		BufferedWriter fractionWriter = new BufferedWriter(new FileWriter(outputFile+".cov"));
		System.out.println("Writing coverage fractions to " + outputFile + ".cov");
		String tab = "\t";
		fractionWriter.write("Chromosome");
		fractionWriter.write(tab);
		fractionWriter.write("Position");
		fractionWriter.write(tab);
		fractionWriter.write("Ref");
		fractionWriter.write(tab);
		fractionWriter.write("fd");
		
		// optimize for several contigs
		int j = 0;
		for (String contig : contigs) {
			System.out.println();
			System.out.println("Working on '" + contig + "' with length '" + lengths.get(j) + "'...");
			List<VcfEntry> vcfEntries = vcfEntriesMap.get(contig);
			// iterate once over the list to get statistical relevant data
			VcfEntryPreAnalyser vcfEntryPreAnalyser = 
					new VcfEntryPreAnalyser(vcfEntries, snpFacWindowParameters, coverageWindowParameters, heterozygousWindowParameters);
			vcfEntryPreAnalyser.preAnalyseVcfEntries();
			twoWrite(logWriter, "Mean coverage per Window: " + vcfEntryPreAnalyser.getMeanCoveragePerWindow());
			twoWrite(logWriter, "Mean number of SNPs per Window: " + vcfEntryPreAnalyser.getMeanNumSnpsPerWindow());
			// write coverage fractions
			writeCoverageFractions(vcfEntryPreAnalyser.getCoverageFractions(), fractionWriter);
			
			
			// analyse vcf with given statistics, give back list of windows
			VcfAnalyser vcfAnalyser = new VcfAnalyser(vcfEntries);
			vcfAnalyser.setCoverageWindowParameters(coverageWindowParameters);
			vcfAnalyser.setSnpAbsWindowParameters(snpAbsWindowParameters);
			vcfAnalyser.setSnpFacWindowParameters(snpFacWindowParameters);
			vcfAnalyser.setHeterozygousWindowParameters(heterozygousWindowParameters);
			vcfAnalyser.setUncoveredPosParameters(uncoveredPosWindowParameters);
			vcfAnalyser.setMeanCoveragePerWindow(vcfEntryPreAnalyser.getMeanCoveragePerWindow());
			vcfAnalyser.setMeanNumSnpsPerWindow(vcfEntryPreAnalyser.getMeanNumSnpsPerWindow());
			vcfAnalyser.analyseVcf();
			
			// some statistics
			writeStatistics(logWriter, vcfAnalyser);
			
			
			// merge lists of windows to regions
			String seqId = contigs.get(0);
			System.out.println("Merging windows to regions...");
			WindowMerger windowMerger = new WindowMerger();
			// merge coverage windows
			windowMerger.setWindows((List<AFilterWindow>) (List<?>) vcfAnalyser.getCoverageWindows());
			windowMerger.mergeWindows(seqId);
			List<Region> coverageRegions = windowMerger.getMergedWindows();
			twoWrite(logWriter, "#coverage regions: " + coverageRegions.size());
			twoWrite(logWriter, "%reference position coverage: " + String.format("%.3f", (100.0 * (double) regionsSize(coverageRegions) / (double) vcfEntries.size())));
			// merge absolute windows
			windowMerger.setWindows((List<AFilterWindow>) (List<?>) vcfAnalyser.getAbsWindows());
			windowMerger.mergeWindows(seqId);
			List<Region> snpAbsoluteRegions = windowMerger.getMergedWindows();
			twoWrite(logWriter, "#SNP absolute regions: " + snpAbsoluteRegions.size());
			twoWrite(logWriter, "%reference position coverage: " + String.format("%.3f", (100.0 * (double) regionsSize(snpAbsoluteRegions) / (double) vcfEntries.size())));
			// merge factor windows
			windowMerger.setWindows((List<AFilterWindow>) (List<?>) vcfAnalyser.getFacWindows());
			windowMerger.mergeWindows(seqId);
			List<Region> snpFactorRegions = windowMerger.getMergedWindows();
			twoWrite(logWriter, "#SNP factor regions: " + snpFactorRegions.size());
			twoWrite(logWriter, "%reference position coverage: " + String.format("%.3f", (100.0 * (double) regionsSize(snpFactorRegions) / (double) vcfEntries.size())));
			// merge heterozygous windows
			windowMerger.setWindows((List<AFilterWindow>) (List<?>) vcfAnalyser.getHeterozygousWindows());
			windowMerger.mergeWindows(seqId);
			List<Region> heterozygousRegions = windowMerger.getMergedWindows();
			twoWrite(logWriter, "#heterozygous regions: " + heterozygousRegions.size());
			twoWrite(logWriter, "%reference position coverage: " + String.format("%.3f", (100.0 * (double) regionsSize(heterozygousRegions) / (double) vcfEntries.size())));
			// merge uncovered windows
			windowMerger.setWindows((List<AFilterWindow>) (List<?>) vcfAnalyser.getUncoveredWindows());
			windowMerger.mergeWindows(seqId);
			List<Region> uncoveredRegions = windowMerger.getMergedWindows();
			twoWrite(logWriter, "#uncovered position regions: " + uncoveredRegions.size());
			twoWrite(logWriter, "%reference position coverage: " + String.format("%.3f", (100.0 * (double) regionsSize(uncoveredRegions) / (double) vcfEntries.size())));
			twoWrite(logWriter, "");
			
			// merge regions regardless of categories
			List<Region> mergedRegions = new ArrayList<>();
			List<Region> allRegions = new ArrayList<>();
			if (mergeRegionsParameterB) {
				allRegions.addAll(coverageRegions);
				allRegions.addAll(snpAbsoluteRegions);
				allRegions.addAll(snpFactorRegions);
				allRegions.addAll(heterozygousRegions);
				allRegions.addAll(uncoveredRegions);
				Collections.sort(allRegions, new RegionSeqIdStartEndComparatorAsc());
				mergeRegions(allRegions, mergedRegions);
			}
			
			// write list of regions to gff3 file
			
			if (mergeRegionsParameterB) {
				writeGff3Entries(mergedRegions, gff3Buffer);
			} else {
				writeGff3Entries(coverageRegions, gff3Buffer);
				writeGff3Entries(snpAbsoluteRegions, gff3Buffer);
				writeGff3Entries(snpFactorRegions, gff3Buffer);
				writeGff3Entries(heterozygousRegions, gff3Buffer);
				writeGff3Entries(uncoveredRegions, gff3Buffer);
			}
			j++;
		}
		
		
		gff3Buffer.close();
		long now = System.currentTimeMillis();
		twoWrite(logWriter, "");
		twoWrite(logWriter, AnalyseVcf.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
		logWriter.close();
	} // closing main part

	private static void writeCoverageFractions(List<CoverageFraction> coverageFractions, BufferedWriter fractionWriter) throws IOException {
		for (CoverageFraction cFraction : coverageFractions) {
			fractionWriter.newLine();
			fractionWriter.write(cFraction.toString());
		}
		fractionWriter.close();
	}

	private static int regionsSize(List<Region> coverageRegions) {
		int regionsSize = 0;
		for (Region region : coverageRegions) {
			regionsSize += region.getSize();
		}
		return regionsSize;
	}

	private static void mergeRegions(List<Region> allRegions, List<Region> mergedRegions) {
		int i = 0;
		int startRegion = -1;
		int endRegion = -1;
		Region lastSeenRegion = null;
		HashSet<String> reasons = new HashSet<>();
		for (Region region : allRegions) {
			lastSeenRegion = region;
			if (i == 0) {
				endRegion = region.getEnd();
				startRegion = region.getStart();
				i++;
				reasons.add(region.getReasons().get(0));
			} else {
				int start = region.getStart();
				// we have an overlap of the regions
				if ((start <= (endRegion + 1)) && (lastSeenRegion.getSeqId().equals(region.getSeqId()))) {
					endRegion = Math.max(endRegion, region.getEnd());
				} else {
					// finish current region
					Region newRegion = new Region(startRegion, endRegion, lastSeenRegion.getSeqId());
					for (String reason : reasons) {
						newRegion.addReason(reason);
					}
					mergedRegions.add(newRegion);
					
					// update start and end
					startRegion = region.getStart();
					endRegion = region.getEnd();
					reasons.clear();
				}
				reasons.add(region.getReasons().get(0));
			}
		}
		// finish last region
		Region newRegion = new Region(startRegion, endRegion, lastSeenRegion.getSeqId());
		for (String reason : reasons) {
			newRegion.addReason(reason);
		}
		mergedRegions.add(newRegion);
		
	}

	private static void writeGff3Entries(List<Region> regions, BufferedWriter gff3Buffer) throws IOException {
		for (Region region : regions) {
			gff3Buffer.newLine();
			gff3Buffer.write(region.toGff3Entry().toString());
		}
	}

	private static void writeGff3Header(String seqId, int length, BufferedWriter gff3Buffer) throws IOException {
		gff3Buffer.write("##sequence-region " + seqId + " 1 " + length);
	}

	/**
	 * @param logWriter
	 * @param vcfAnalyser
	 * @throws IOException
	 */
	private static void writeStatistics(BufferedWriter logWriter, VcfAnalyser vcfAnalyser) throws IOException {
		twoWrite(logWriter, "");
		int[] totalNumWindows = vcfAnalyser.getTotalNumWindows();
		twoWrite(logWriter, "#coverage windows: " + totalNumWindows[0]);
		double probCovWinds = (double) vcfAnalyser.getCoverageWindows().size();
		twoWrite(logWriter, "#problematic coverage windows: " + probCovWinds);
		twoWrite(logWriter, "%problematic coverage windows: " + String.format("%.3f", 100* (probCovWinds / (double) totalNumWindows[0])));
		
		twoWrite(logWriter, "#SNP absolute windows: " + totalNumWindows[1]);
		double probSnpAbsWinds = (double) vcfAnalyser.getAbsWindows().size();
		twoWrite(logWriter, "#problematic SNP absolute windows: " + probSnpAbsWinds);
		twoWrite(logWriter, "%problematic SNP absolute windows: " + String.format("%.3f", 100* (probSnpAbsWinds / (double) totalNumWindows[1])));
		
		twoWrite(logWriter, "#SNP factor windows: " + totalNumWindows[2]);
		double probSnpFacWinds = vcfAnalyser.getFacWindows().size();
		twoWrite(logWriter, "#problematic SNP factor windows: " + probSnpFacWinds);
		twoWrite(logWriter, "%problematic SNP factor windows: " + String.format("%.3f", 100* (probSnpFacWinds / (double) totalNumWindows[2])));
		
		twoWrite(logWriter, "#heterozygous windows: " + totalNumWindows[3]);
		double probHetWinds = vcfAnalyser.getHeterozygousWindows().size();
		twoWrite(logWriter, "#problematic heterozygous windows: " + probHetWinds);
		twoWrite(logWriter, "%problematic heterozygous windows: " + String.format("%.3f", 100* (probHetWinds / (double) totalNumWindows[3])));
		
		twoWrite(logWriter, "#uncovered position windows: " + totalNumWindows[4]);
		double probUncovWinds = vcfAnalyser.getUncoveredWindows().size();
		twoWrite(logWriter, "#problematic uncovered position windows: " + probUncovWinds);
		twoWrite(logWriter, "%problematic uncovered position windows: " + String.format("%.3f", 100* (probUncovWinds / (double) totalNumWindows[4])));
		twoWrite(logWriter, "");
	}
	
	private static void twoWrite(BufferedWriter logWriter, String string) throws IOException {
		System.out.println(string);
		logWriter.newLine();
		logWriter.write(string);
	}
	
	private static void writeCurrentDate(BufferedWriter logWriter) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//get current date time with Date()
		Date date = new Date();
		twoWrite(logWriter, dateFormat.format(date));
	}

}
