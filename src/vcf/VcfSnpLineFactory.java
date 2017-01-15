package vcf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import utils.ArrayUtils;

// FIXME this is currently not working!!!

/**
 * A class representing a SNP Line Factory that mainly
 * holds a list of VcfSnpLineHolder and calculates
 * needed statistics for the 'ConsensusSeqFromVCFs' module.
 * @author heumos
 *
 */
public class VcfSnpLineFactory {
	
	// a String array holding all the paths to the VCF input files
	private String[] inputFiles;
	// required number of alternative SNPs
	private int requiredSnps;
	// the VcfSnpLineHolder list
	private List<VcfSnpLineHolder> holderList;
	// the current chromosome
	private String chrom;
	// boolean declaring, if there is at least one VCF entry with the current 
	// chrom in the holderList
	private boolean hasCurChromEntry;
	// the minimum position of all hold VCF entries with current chromosome
	private int minPos;
	// boolean declaring if there are enough VCF entries with the minimum
	// position
	private boolean enoughVcfs;
	// array holding positions of VCF entries with current chromosome 
	// and minimum position
	private List<Integer> minPoss;
	// boolean declaring if there is at least one next SNP line left
	private boolean isNextSnpLine;
	// boolean array telling, if VcfSnpindexLineHolder is null or not
	private boolean[] lineHolderNull;
	
	public VcfSnpLineFactory(String[] inputFiles, int requiredSnps, String chrom) {
		this.inputFiles = inputFiles;
		this.requiredSnps = requiredSnps;
		this.chrom = chrom;
		try {
			this.holderList = createHolderList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.lineHolderNull = new boolean[this.inputFiles.length];
	}
	
	private List<VcfSnpLineHolder> createHolderList() throws IOException {
		List<VcfSnpLineHolder> holderList = new ArrayList<VcfSnpLineHolder>();
		boolean t = true;
		boolean f = false;
		this.hasCurChromEntry = true;
		VcfLineParser vcfLineParser = new VcfLineParser(t, t, f, t, t, f, f, f, f, f);
		TreeMap<Integer, List<Integer>> tM = new TreeMap<Integer, List<Integer>>(); 
		for (int i = 0; i < this.inputFiles.length; i++) {
			VcfSnpLineHolder vcfSnpLineHolder = new VcfSnpLineHolder(this.inputFiles[i], vcfLineParser);
			VcfEntry vE = vcfSnpLineHolder.getVcfEntry();
			int pos = Integer.parseInt(vE.getPos());
			if (tM.containsKey(pos)) {
				List<Integer> intList = tM.get(pos);
				intList.add(i);
			} else {
				List<Integer> intList = new ArrayList<Integer>();
				intList.add(i);
				tM.put(pos, intList);
			}
			this.minPos = tM.firstKey();
			this.minPoss = tM.get(this.minPos);
			this.enoughVcfs = this.minPoss.size() >= this.requiredSnps;
			this.isNextSnpLine = true;
			holderList.add(vcfSnpLineHolder);
		}
		return holderList;
	}
	
	public String getAltFromCurSnp() {
		return this.holderList.get(this.minPoss.get(0)).getVcfEntry().getAlt();
	}
	
	public void updateHolderList() throws IOException {
		TreeMap<Integer, List<Integer>> tM = new TreeMap<Integer, List<Integer>>();
		if (hasCurChromEntry) {
			for (int i = 0; i < this.minPoss.size(); i++) {
				int posInHolderList = this.minPoss.get(i);
				VcfSnpLineHolder vcfSnpLineHolder = this.holderList.get(posInHolderList);
				VcfEntry vE = vcfSnpLineHolder.getNextVcfEntry();
				if (vE != null) {
					int pos = Integer.parseInt(vE.getPos());
					String chromOrig = vE.getChrom();
					if (this.chrom.equals(chromOrig)) {
						if (tM.containsKey(pos)) {
							List<Integer> intList = tM.get(pos);
							intList.add(i);
						} else {
							List<Integer> intList = new ArrayList<Integer>();
							intList.add(i);
							tM.put(pos, intList);
						}
						Integer firstKey = tM.firstKey();
						if (firstKey != null) {
							this.minPos = firstKey;
							this.minPoss = tM.get(firstKey);
							this.enoughVcfs = this.minPoss.size() >= this.requiredSnps;
						} else {
							hasCurChromEntry = false;
						}
					}
				} else {
					this.lineHolderNull[posInHolderList] = true;
				}
			}			
		} else {
			this.chrom = findNextChrom();
			for (int i = 0; i < this.holderList.size(); i++) {
				VcfSnpLineHolder vcfSnpLineHolder = this.holderList.get(i);
				VcfEntry vE = vcfSnpLineHolder.getVcfEntry();
				if (vE != null) {
					int pos = Integer.parseInt(vE.getPos());
					String chromOrig = vE.getChrom();
					if (this.chrom.equals(chromOrig)) {
						if (tM.containsKey(pos)) {
							List<Integer> intList = tM.get(pos);
							intList.add(i);
						} else {
							List<Integer> intList = new ArrayList<Integer>();
							intList.add(i);
							tM.put(pos, intList);
						}
						this.minPos = tM.firstKey();
						this.minPoss = tM.get(this.minPos);
						this.enoughVcfs = this.minPoss.size() >= this.requiredSnps;
					}
				}
			}
		}
		if (ArrayUtils.countTrue(this.lineHolderNull) == this.inputFiles.length) {
			this.isNextSnpLine = false;
		}
	}

	private String findNextChrom() {
		String chrom = "";
		for (int i = 0; i < this.lineHolderNull.length; i++) {
			boolean holder = this.lineHolderNull[i];
			if (holder) {
				chrom = this.holderList.get(i).getVcfEntry().getChrom();
				return chrom;
			}
		}
		return chrom;
	}

	public boolean isHasCurChromEntry() {
		return hasCurChromEntry;
	}
	
	public List<Integer> getMinPoss() {
		return minPoss;
	}
	
	public boolean isNextSnpLine() {
		return isNextSnpLine;
	}
	
	public String[] getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(String[] inputFiles) {
		this.inputFiles = inputFiles;
	}

	public int getRequiredSnps() {
		return requiredSnps;
	}

	public void setRequiredSnps(int requiredSnps) {
		this.requiredSnps = requiredSnps;
	}

	public String getChrom() {
		return this.chrom;
	}

	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	public int getMinPos() {
		return this.minPos;
	}

	public void setMinPos(int minPos) {
		this.minPos = minPos;
	}

	public List<VcfSnpLineHolder> getHolderList() {
		return holderList;
	}

	public boolean isChromEntry() {
		return hasCurChromEntry;
	}

	public boolean isEnoughVcfs() {
		return enoughVcfs;
	}
}
