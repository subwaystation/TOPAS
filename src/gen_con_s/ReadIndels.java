package gen_con_s;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

import vcf.VcfEntry;
import vcf.VcfLineParser;

public class ReadIndels {
	
	private String indelFile;
	
	private HashMap<String, TreeMap<Integer, VcfEntry>> indels;
	private boolean inDelMode;
	private VcfEntry deletion;
	private ArrayList<String> warningList;
	private ArrayList<VcfEntry> tempE;
	private ArrayList<Integer> tempLines;
	private int delBasePos;
	private int delLine;
	
	public ReadIndels(String indelFile) {
		this.indelFile = indelFile;
		this.indels = new HashMap<String, TreeMap<Integer,VcfEntry>>();
		this.inDelMode = false;
		this.deletion = new VcfEntry();
		this.warningList = new ArrayList<String>();
		this.tempE = new ArrayList<VcfEntry>();
		this.tempLines = new ArrayList<Integer>();
		this.delBasePos = -1;
		this.delLine = -1;
	}
	
	public HashMap<String, TreeMap<Integer, VcfEntry>> readIndels() throws IOException {
		// read GZIPPED file
		BufferedReader bReader;
		InputStream gzipStream;
		if (this.indelFile.endsWith(".gz")) {
			InputStream fileStream = new FileInputStream(this.indelFile);
			gzipStream = new GZIPInputStream(fileStream);
			Reader decoder = new InputStreamReader(gzipStream, StandardCharsets.UTF_8);
			bReader = new BufferedReader(decoder);
		} else {
			bReader = new BufferedReader(new FileReader(this.indelFile));
		}
		String line = "";
		int lines = 0;
		String chromBefore = "";
		boolean passedFirstLine = false;
		VcfLineParser vcfLineParser = new VcfLineParser(true, true, false,
				true, true, true, false, false, true, true);
		VcfEntry vE = null;
		boolean del = false;
		while ((line = bReader.readLine()) != null) {
			lines++;
			if (!line.startsWith("#") && !line.isEmpty()) {
				vE = vcfLineParser.createVcfEntryFromLine(line);
				if (!vE.isMultiDel()) {
					if (validatePos(vE, lines)) {
						String chrom = vE.getChrom();
						if (passedFirstLine) {
							// check for chromosome change and finish if inDelMode
							checkChromChange(chromBefore, chrom, lines);
						} else {
							chromBefore = chrom;
							passedFirstLine = true;
						}
						del = vE.isDel();
						// no deletion
						if (!del) {
							// check if inDelMode
							if (this.inDelMode) {
								// check, if current vcf entry is in range of the deletion
								if (inDelRange(vE)) {
									// add current vcf entry to tempeE list
									this.tempE.add(vE);
									this.tempLines.add(lines);
									// not in range
								} else {
									// finish deletion
									finishDel(new VcfEntry(), lines);
									// add current vcf entry normally
									addVcfEntryToIndels(vE, lines);
								}
							} else {
								// add current vcf entry normally
								addVcfEntryToIndels(vE, lines);
							}
							
							// deletion
						} else {
							// we have already seen a deletion
							if (this.inDelMode) {
								// check if current vcf entry is in range of the deletion
								if (inDelRange(vE)) {
									finishDel(vE, lines);
								} else {
									finishDel(new VcfEntry(), lines);
									this.deletion = vE;
									this.inDelMode = true;
									this.delLine = lines;
									this.delBasePos = calcDelBasePos();
								}
								// resolve/finish current deletion and add del and lists entries accordingly to resolvation
								// we have not already seen a deletion
							} else {
								this.deletion = vE;
								this.inDelMode = true;
								this.delLine = lines;
								this.delBasePos = calcDelBasePos();
							}
						}
					}
				} else {
					this.warningList.add("[WARNING] in line " + lines + ": VCF entry is ignored because position '" + vE.getPos()
							+ "' with chromosome '" + vE.getChrom() + "is a multi deletion. This tool can not handle that (yet).");
				}
			}
		}
		// last read vcf entry might be a deletion
		if (inDelMode) {
			finishDel(new VcfEntry(), lines);
		}
		bReader.close();
		return this.indels;
	}
	
	private void checkChromChange(String chromBefore, String chrom, int lines) {
		// see new chrom
		if (!chromBefore.equals(chrom)) {
			if (this.inDelMode) {
				finishDel(new VcfEntry(), lines);
			}
		}
	}

	private int calcDelBasePos() {
		String ref = this.deletion.getRef();
		String[] refSplit = ref.split(",");
		int refSplitLen = refSplit.length;
		return refSplitLen;
//		int delBasePos = -1;
//		int baseLen = Integer.MIN_VALUE;
//		for (int i = 0; i < refSplit.length; i++) {
//			String base = refSplit[i];
//			if (base.length() > baseLen) {
//				delBasePos = i;
//				baseLen = base.length();
//			}
//		}
//		return delBasePos;
	}

	private void finishDel(VcfEntry vE, int lines) {
		// we not have to add the current entry which would be another deletion
		if (vE.isEmpty()) {
			// deletion coverage wins
			if (resolveDel()) {
				addVcfEntryToIndels(this.deletion, this.delLine);
				// tell that the following entries(chrom + positions) were left out,
				// because the del_entry had a higher coverage then these combined
				// total coverage of tempE list wins
				if (!this.tempE.isEmpty()) {
					String warning = "[WARNING] in line " + this.delLine + ": ";
					warning += "VCF entries with chromosome " + this.tempE.get(0) + "and positions " + buildPosString(this.tempE) + "are "
							+ "ignored, because they have a smaller coverage than the deletion at position " + this.deletion.getPos() + "."; 
					this.warningList.add(warning);
				}
			} else {
				for (int i = 0; i < this.tempE.size(); i++) {
					VcfEntry tempVe = this.tempE.get(i);
					int tempLine = this.tempLines.get(i);
					addVcfEntryToIndels(tempVe, tempLine);
				}
				// tell that the deletion (chrom + pos) was not taken into account, 
				// because the insertion following that deletion have a higher coverage than
				// the deletion
				String warning = "[WARNING] in line " + this.delLine + ": ";
				warning += "Deletion with chromosome " + this.deletion.getChrom() + "and position " + this.deletion.getPos() + " is "
						+ "ignored, because it has a smaller coverage than the following entries at positions " + buildPosString(this.tempE) + "."; 
				this.warningList.add(warning);
			}
			this.inDelMode = false;
			this.tempE.clear();
			this.delBasePos = -1;
			this.delLine = -1;
			// we have to add also the current entry vE, being another deletion
		} else {
			this.tempE.add(vE);
			this.tempLines.add(lines);
			// deletion coverage wins
			if (resolveDel()) {
				addVcfEntryToIndels(this.deletion, this.delLine);
				this.inDelMode = false;
				this.tempE.clear();
				this.delBasePos = -1;
				this.delLine = -1;
				// tell that the following entries(chrom + positions) were left out,
				// because the del_entry had a higher coverage then these combined
				// total coverage of tempE list wins, furthermore, tell that the current found
				// entry (deletion) did not win, too
				if (!this.tempE.isEmpty()) {
					String warning = "[WARNING] in line " + this.delLine + ": ";
					warning += "VCF entries with chromosome " + this.tempE.get(0) + "and positions " + buildPosString(this.tempE) + vE.getPos() + " are "
							+ "ignored, because they have a smaller coverage than the deletion at position " + this.deletion.getPos() + "."; 
					this.warningList.add(warning);
				} else {
					// current deletion lost in coverage over previos one 
					String warning = "[WARNING] in line " + this.delLine + ": ";
					warning += "VCF entries with chromosome " + vE.getChrom() + "and position " +  vE.getPos() + " is "
							+ "ignored, because they it has a smaller coverage than the deletion at position " + this.deletion.getPos() + "."; 
					this.warningList.add(warning);
				}
				
				// tempE and newly found deletion coverage wins
			} else {
				for (int i = 0; i < this.tempE.size() - 1; i++) {
					VcfEntry tempVe = this.tempE.get(i);
					int tempLine = this.tempLines.get(i);
					addVcfEntryToIndels(tempVe, tempLine);
				}
				// tell that the deletion (chrom + pos) was not taken into account, 
				// because the insertion(s) following that deletion + current deletion have a higher coverage than
				// the deletion
				String warning = "[WARNING] in line " + this.delLine + ": ";
				warning += "Deletion with chromosome " + this.deletion.getChrom() + "and position " + this.deletion.getPos() + " is "
						+ "ignored, because it has a smaller coverage than the following entries at positions " + buildPosString(this.tempE) + vE.getPos() + "."; 
				this.warningList.add(warning);
				this.deletion = vE;
				this.inDelMode = true;
				this.delLine = lines;
				this.delBasePos = calcDelBasePos();
				this.tempE.clear();
			}
		}
	}
	
	private String buildPosString(ArrayList<VcfEntry> tempE) {
		String poss = "";
		for (VcfEntry vE : tempE) {
			poss += vE.getPos() + ", ";
		}
		return poss;
	}

	private boolean resolveDel() {
		boolean delCovWin = false;
		String[] aDs = getAds(this.deletion);
		int delCov = Integer.parseInt(aDs[this.delBasePos]);
		int totalTempECov = 0;
		for (VcfEntry vE : this.tempE) {
			String[] aDsVe = getAds(vE);
			for (String aD : aDsVe) {
				totalTempECov += Integer.parseInt(aD);
			}
		}
		if (delCov > totalTempECov) {
			delCovWin = true;
			return delCovWin;
		} else {
			return delCovWin;
		}
	}
	
	/**
	 * @param vE
	 * @return a string array with all the AD of the vcf entry
	 */
	private String[] getAds(VcfEntry vE) {
		return vE.getSampleIds().split(":")[1].split(",");
	}

	private boolean inDelRange(VcfEntry vE) {
		boolean inDelRange = false;
		String ref = this.deletion.getRef();
		int pos = Integer.parseInt(this.deletion.getPos());
		String[] refSplit = ref.split(",");
		int delLen = Integer.MIN_VALUE;
		for (String base : refSplit) {
			delLen = Math.max(delLen, base.length());
		}
		int posVe = Integer.parseInt(vE.getPos());
		// not in range
		
		if ((pos + delLen - 1) <= posVe) {
			return inDelRange;
			// in range
		} else {
			inDelRange = true;
			return inDelRange;
		}
	}

	private boolean validatePos(VcfEntry vE, int lines) {
		boolean validated = true;
		String chrom = vE.getChrom();
		int pos = Integer.parseInt(vE.getPos());
		// check if position occured in indels
		TreeMap<Integer, VcfEntry> posMap = this.indels.get(chrom);
		if (posMap != null && posMap.containsKey(pos)) {
			validated = false;
			this.warningList.add("[WARNING] in line " + lines + ": VCF entry is ignored because position '" + pos
					+ "' with chromosome '" + chrom + "' already occured in the VCF file. This tool is currently only able to read VCF files"
					+ " in which each position occures at most one time! Skipping this entry! (Please fix your VCF file).");
			return validated;
			// check if position occured in deletion
		} else {
			if (!this.deletion.isEmpty()) {
				String delChrom = this.deletion.getChrom();
				int delPos = Integer.parseInt(this.deletion.getPos());
				// check if position occured in tempE
				if ((delChrom.equals(chrom) && pos == delPos) || haveSamePos(vE, this.tempE)) {
					validated = false;
					this.warningList.add("[WARNING] in line " + lines + ": VCF entry is ignored because position '" + pos
							+ "' with chromosome '" + chrom + "' already occured in the VCF file. This tool is currently only able to read VCF files"
							+ " in which each position occures at most one time! Skipping this entry! (Please fix your VCF file).");
					return validated;
				}
			}
		} 
		return validated;
		
	}

	private boolean haveSamePos(VcfEntry vE, ArrayList<VcfEntry> tempE) {
		boolean samePos = false;
		for (VcfEntry vEE : tempE) {
			String pos = vE.getPos();
			String posE = vEE.getPos();
			if (pos.equals(posE)) {
				samePos = true;
				return samePos;
			}
		}
		return samePos;
	}

	private void addVcfEntryToIndels(VcfEntry vE, int lines) {
		String chrom = vE.getChrom();
		Integer pos = Integer.parseInt(vE.getPos());

		// check, if current chrom does already exist
		if (this.indels.containsKey(chrom)) {
			TreeMap<Integer, VcfEntry> posMap = this.indels.get(chrom);
			// check if current pos does already exist, this should not be the case!
			if (posMap.containsKey(pos)) {
				this.warningList.add("[WARNING] in line " + lines + ": VCF entry is ignored because position '" + pos
						+ "' with chromosome '" + chrom + "' already occured in the VCF file. This tool is currently only able to read VCF files"
						+ " in which each position occures at most one time! Skipping this entry! (Please fix your VCF file).");
			} else {
					posMap.put(pos, vE);
			}
		} else {
			TreeMap<Integer, VcfEntry> posMap = new TreeMap<Integer, VcfEntry>();
			posMap.put(pos, vE);
			this.indels.put(chrom, posMap);
		}
	}

	public String getIndelFile() {
		return indelFile;
	}

	public void setIndelFile(String indelFile) {
		this.indelFile = indelFile;
	}

	public ArrayList<String> getWarningList() {
		return warningList;
	}

	public void setWarningList(ArrayList<String> warningList) {
		this.warningList = warningList;
	}

}
