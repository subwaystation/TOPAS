package vcf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import vcf.filter.UnstrictSnpFilter;

/**
 * VCF SNP Line Holder Object.
 * 
 * @author heumos
 *
 */
public class VcfSnpLineHolder {

	// the buffered reader with which the input file is read
	private BufferedReader buffReader;
	// the VCF SNP filter
	private UnstrictSnpFilter unstrictSnpFilter;
	// the VCF entry currently hold by the VCF Line Holder
	private VcfEntry vcfEntry;
	// the VCF line parser configured to parse the VCF line properly
	private VcfLineParser vcfLineParser;

	/**
	 * @param inputFile (required) input file in VCF format, from which 
	 * the VCF entries should be read
	 * @throws IOException
	 */
	public VcfSnpLineHolder(String inputFile, VcfLineParser vcfLineParser) throws IOException {
		this.buffReader = new BufferedReader(new FileReader(inputFile));
		this.unstrictSnpFilter = new UnstrictSnpFilter(null);
		this.vcfLineParser = vcfLineParser;
		this.vcfEntry = readFirstVcfEntry();
	}

	/**
	 * @return the first <tt>VcfEntry</tt> of the given VCF file
	 * @throws IOException
	 */
	private VcfEntry readFirstVcfEntry() throws IOException {
		String line;
		boolean isUnstrictSnp;
		while ((line = this.buffReader.readLine()) != null) {
			if (!line.startsWith("#")) {
				this.unstrictSnpFilter.setVcfLine(line);
				isUnstrictSnp = this.unstrictSnpFilter.filter();
				if (isUnstrictSnp) {
					VcfEntry vE = this.vcfLineParser.createVcfEntryFromLine(line);
					return vE;
				} 
			}
		}
		return null;
	}

	/**
	 * Reads the next VCF line of the VCF file
	 * and sets the VcfEntry field.
	 * If there is no more line in the file, the
	 * field VcfEntry will be set to null and the
	 * BufferedReader will be closed.
	 * 
	 * @throws IOException
	 */
	public void readNextVcfLine() throws IOException {
		String line;
		boolean isUnstrictSnp = false;
		boolean foundLine = false;
		while ((line = this.buffReader.readLine()) != null) {
			this.unstrictSnpFilter.setVcfLine(line);
			isUnstrictSnp = this.unstrictSnpFilter.filter();
			if (isUnstrictSnp) {
				VcfEntry vE = this.vcfLineParser.createVcfEntryFromLine(line);
				foundLine = true;
				this.vcfEntry = vE;
				break;
			} 
		}
		if (!foundLine) {
			this.vcfEntry = null;
			this.buffReader.close();
		}
	}
	
	/**
	 * Reads the next VCF line of the VCF file
	 * and sets the VcfEntry field.
	 * If there is no more line in the file, the
	 * field VcfEntry will be set to null and the
	 * BufferedReader will be closed.
	 * 
	 * @return the parsed VCF line
	 * @throws IOException
	 */
	public VcfEntry getNextVcfEntry() throws IOException {
		this.readNextVcfLine();
		return this.vcfEntry;
	}
	
	/**
	 * @return vcf entry from this object
	 */
	public VcfEntry getVcfEntry() {
		return this.vcfEntry;
	}

}
