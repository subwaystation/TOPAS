package topas;

import io.vcf.VcfIndexReader;
import io.vcf.VcfReaderFromIndex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import topas.Topas.TOPASModule;
import topas.parameters.FilterVCFParameters;
import vcf.filter.IdVcfFilter;
import vcf.filter.InDelFilter;
import vcf.filter.RangeVcfFilter;
import vcf.filter.StrictSnpFilter;
import vcf.index.VcfIndex;

@TOPASModule(
		purpose = "a VCF file can be filtered by CHROM:START-END, ID and by INFO (SNP or INDEL)"
		)

public class FilterVCF {
	
	public static void main(String[] args) throws Exception {

		System.out.println(FilterVCF.class.getCanonicalName());
		System.out.println("Use -? for help");

		long before = System.currentTimeMillis();

		FilterVCFParameters.createInstance(args);

		String vcfFile = FilterVCFParameters.getInstance().getParameter("vcf").toString();
		String outputFile = FilterVCFParameters.getInstance().getParameter("o").toString();
		String vaidx = FilterVCFParameters.getInstance().getParameter("vai").toString();
		String chromRanges = FilterVCFParameters.getInstance().getParameter("chromRanges").toString();
		String chromRange = FilterVCFParameters.getInstance().getParameter("chromRange").toString();
		String ids = FilterVCFParameters.getInstance().getParameter("ids").toString();
		String id = FilterVCFParameters.getInstance().getParameter("id").toString();
		boolean snp = FilterVCFParameters.getInstance().getParameter("snp").isPresent();
		boolean indel = FilterVCFParameters.getInstance().getParameter("indel").isPresent();

		System.out.println();
		if (snp && indel) {
			System.out.println("FilterVCF only supports the extraction of either SNPs or INDELs, please only specify one of the"
					+ " following parameters: '-snp', '-indel'.");
			System.exit(1);
		}
		
		if (chromRange.equals("--") && chromRanges.equals("--")) {
			System.out.println("FilterVCF requires a chromosome range specification, please enter one of the following parameters:"
					+ " '-chromRange' or '-chromRanges'.");
			System.exit(1);
		}

		System.out.println("Parameters chosen: ");
		System.out.println("VCF file            : "+vcfFile);
		System.out.println("VCF index file      : "+vaidx);
		System.out.println("Output file         : "+outputFile);
		System.out.println("ChromRanges file    : "+chromRanges);
		System.out.println("ChromRange          : "+chromRange);
		System.out.println("IDs                 : "+ids);
		System.out.println("ID                  : "+id);
		System.out.println("Extract only SNPs   : "+snp);
		System.out.println("Extract only INDELS : "+indel);
		System.out.println();

		if (!chromRange.equals("--") && !chromRanges.equals("--")) {
			System.out.println("You specified a file with ChromRanges and a single ChromRange,"
					+ "\nthe single ChromRange will be ignored.");
		}
		
		if (!ids.equals("--") && !id.equals("--")) {
			System.out.println("You specified a file with IDs and a single ID, the single ID will be ignored.");
		}

		// create List of ChromRanges if necessary
		List<String> chromRangesList = new ArrayList<String>();
		if (!chromRanges.equals("--")) {
			System.out.println("Reading ChromRanges from " + chromRanges);
			System.out.println();
			chromRangesList = createChromRangesList(chromRanges);
		} else {
			if (!chromRange.equals("--")) {
				chromRangesList.add(chromRange);
			}
		}

		// create List of IDs if necessary
		List<String> idsList = new ArrayList<String>();
		if (!ids.equals("--")) {
			System.out.println("Reading Ids from " +ids);
			System.out.println();
			idsList = createIdsList(ids);
		} else {
			if (!id.equals("--")) {
				idsList.add(id);
			}
		}

		System.out.println("Reading VCF Index from " + vaidx);
		VcfIndexReader vaidxR = new VcfIndexReader(vaidx);
		List<VcfIndex> vaidxList = vaidxR.readVcfIndices();
		// create HashMap of VCF_Indices
		HashMap<String, List<VcfIndex>> vaidxMap = new HashMap<String, List<VcfIndex>>();
		createIndicesMap(vaidxList, vaidxMap);

		// create HashMap of chromRangesList
		HashMap<String, List<String>> chromRangesMap = createChromRangesMap(chromRangesList);

		// only keep needed VCF_Indices in hash map
		HashMap<String, List<VcfIndex>> vM = calcNeededVcfIndices(vaidxMap, chromRangesMap);

		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
		String vcfLine;
		RangeVcfFilter rVF = new RangeVcfFilter(null, null);
		IdVcfFilter iVF = new IdVcfFilter(null, null);
		InDelFilter inDelF = new InDelFilter(null);
		StrictSnpFilter sF = new StrictSnpFilter(null);
		String vcfHeader = VcfReaderFromIndex.readHeader(vcfFile);

		boolean firstLine = true;
		boolean match = false;
		List<String> chroms = new ArrayList<String>();
		for (String key : chromRangesMap.keySet() ) {
			chroms.add(key);
		}
		for (String chrom : chroms) {
			List<VcfIndex> vaidxL = vM.get(chrom);
			if (vaidxL != null) {
				for (VcfIndex vaix : vaidxL) {
					String[] vcfLines = VcfReaderFromIndex.readVcfLinesFromIndex(vaix, vcfFile);
					for (int j = 0; j < vcfLines.length; j++) {
						vcfLine = vcfLines[j].trim();
						boolean matchRange = false;
						boolean matchId = false;
						boolean matchVt = false;
						// check VCF Entry for Ranges
						rVF.setVcfLine(vcfLine);
						for (int i = 0; i < chromRangesList.size(); i++) {
							String cR = chromRangesList.get(i);
							rVF.setFilterElem(cR);
							if (rVF.filter()) {
								matchRange = true;
								break;
							}
						}

						// check VCF Entry for IDs if necessary
						if (!ids.equals("--") || !id.equals("--")) {
							iVF.setVcfLine(vcfLine);
							for (int i = 0; i < idsList.size(); i++) {
								String iD = idsList.get(i);
								iVF.setFilterElem(iD);
								if (iVF.filter()) {
									matchId = true;
									break;
								}
							}
						} else {
							matchId = true;
						}

						// check VCF Entry for SNPs or INDEL if necessary
						if (snp || indel) {
							if (snp) {
								sF.setVcfLine(vcfLine);
								if (sF.filter()) {
									matchVt = true;
								}
							} else {
								inDelF.setVcfLine(vcfLine);
								if (inDelF.filter()) {
									matchVt = true;
								}
							}
						} else {
							matchVt = true;
						}

						// write out VCF Entry if all filters matched
						if (matchRange && matchId && matchVt) {
							match = true;				
							if (firstLine) {
								bw.write(vcfHeader);
								bw.write(vcfLine);
								firstLine = false;
							} else {
								bw.newLine();
								bw.write(vcfLine);
							}				
						} 
					}
				}		
			}
		}

		bw.close();
		System.out.println();
		if (!match) {
			System.out.println("There could not be any VCF Entry found, matching your filter options.");
			System.out.println("Therefore FilterVCF is exiting.");
			File deleteFile = new File(outputFile);
			deleteFile.delete();
			System.exit(1);
		}
		System.out.println("The resulting VCF file can be found at: " + outputFile);

		long now = System.currentTimeMillis();
		System.out.println();
		System.out.println(FilterVCF.class.getCanonicalName()+" finished in "+(now-before)/1000+" seconds");
	}

	private static HashMap<String, List<String>> createChromRangesMap(
			List<String> chromRangesList) {
		HashMap<String, List<String>> chromRangesMap = new HashMap<String, List<String>>();
		for (String chromRange : chromRangesList) {
			addChromRangeMap(chromRange, chromRangesMap);
		}
		return chromRangesMap;
	}

	private static void addChromRangeMap(String chromRange,
			HashMap<String, List<String>> chromRangesMap) {
		String[] chromRangeSplit = chromRange.split(":");
		String chrom = chromRangeSplit[0];
		if (chromRangesMap.containsKey(chrom)) {
			List<String> lS = chromRangesMap.get(chrom);
			lS.add(chromRange);
		} else {
			List<String> lS = new ArrayList<String>();
			lS.add(chromRange);
			chromRangesMap.put(chrom, lS);
		}
	}

	private static void createIndicesMap(List<VcfIndex> vaidxList,
			HashMap<String, List<VcfIndex>> vaidxMap) {
		for (VcfIndex vaidx : vaidxList) {
			addIndexMap(vaidx, vaidxMap);
		}
	}

	private static void addIndexMap(VcfIndex vaidx, HashMap<String, List<VcfIndex>> vaidxMap) {
		String chrom = vaidx.getChrom();
		if (vaidxMap.containsKey(chrom)) {
			List<VcfIndex> vL = vaidxMap.get(chrom);
			vL.add(vaidx);
		} else {
			List<VcfIndex> vL = new ArrayList<VcfIndex>();
			vL.add(vaidx);
			vaidxMap.put(chrom, vL);
		}
	}

	private static HashMap<String, List<VcfIndex>> calcNeededVcfIndices(
			HashMap<String, List<VcfIndex>> vaidxMap, HashMap<String, List<String>> chromRangesMap) {
		HashMap<String, List<VcfIndex>> vM = new HashMap<String, List<VcfIndex>>();
		List<String> chroms = new ArrayList<String>();
		for (String key : chromRangesMap.keySet() ) {
			chroms.add(key);
		}
		for (String chrom : chroms) {
			List<VcfIndex> vL = vaidxMap.get(chrom);
			List<String> cRL = chromRangesMap.get(chrom);
			for (String chromRange : cRL) {
				String[] chromRangeSplit = chromRange.split(":");
				String ranges = chromRangeSplit[1];
				if (ranges.toLowerCase().equals("all") && vaidxMap.containsKey(chrom)) {
					createIndicesMap(vaidxMap.get(chrom), vM);
				} else {
					String[] rangesSplit = ranges.split("-");
					int start = Integer.parseInt(rangesSplit[0]);
					int end = Integer.parseInt(rangesSplit[1]);
					for (VcfIndex vaidx : vL) {
						int pos = Integer.parseInt(vaidx.getPos());
						if (pos >= start && pos <= end) {
							addIndexMap(vaidx, vM);
						}
					}
				}
			}
		}
		return vM;
	}

	private static List<String> createIdsList(String ids) throws IOException {
		List<String> idsList = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(ids));
		String id;
		while ((id = br.readLine()) != null) {
			idsList.add(id.trim());
		}
		br.close();
		return idsList;
	}

	private static List<String> createChromRangesList(String chromRanges) throws IOException {
		List<String> chromRangesList = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(chromRanges));
		String chromRange;
		while ((chromRange = br.readLine()) != null) {
			chromRangesList.add(chromRange.trim());
		}
		br.close();
		return chromRangesList;
	}
	
}
