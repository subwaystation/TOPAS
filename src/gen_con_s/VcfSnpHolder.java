package gen_con_s;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

import vcf.VcfEntry;
import vcf.VcfLineParser;

public class VcfSnpHolder {

	// the input stream from which the gzipped input file is read
	private InputStream gzipStream;
	// the buffered reader with which the input file is read
	private BufferedReader buffReader;
	// the VCF entry currently hold by the VCF Line Holder
	private VcfEntry vcfEntry;
	// the VCF line parser configured to parse the VCF line properly
	private VcfLineParser vcfLineParser;
	// boolean declaring if the last line was read
	private boolean closed;

	/**
	 * @param inputFile (required) input file in VCF format, from which 
	 * the VCF entries should be read
	 * @throws IOException
	 */
	public VcfSnpHolder(String inputFile, VcfLineParser vcfLineParser) throws IOException {
		if (inputFile.endsWith(".gz")) {
			InputStream fileStream = new FileInputStream(inputFile);
			this.gzipStream = new GZIPInputStream(fileStream);
			Reader decoder = new InputStreamReader(this.gzipStream, StandardCharsets.UTF_8);
			this.buffReader = new BufferedReader(decoder);
		} else {
			this.buffReader = new BufferedReader(new FileReader(inputFile));
		}
		this.vcfLineParser = vcfLineParser;
		this.vcfEntry = readFirstVcfEntry();
		this.closed = false;
	}

	/**
	 * @return the first <tt>VcfEntry</tt> of the given VCF file
	 * @throws IOException
	 */
	private VcfEntry readFirstVcfEntry() throws IOException {
		String line;
		while ((line = this.buffReader.readLine()) != null) {
			if (!line.startsWith("#")) {
				VcfEntry vE = this.vcfLineParser.createVcfEntryFromLine(line);
				this.vcfEntry = vE;
				return vE;
			}
		}
		return new VcfEntry();
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
		boolean foundLine = false;
		if ((line = this.buffReader.readLine()) != null) {
			VcfEntry vE = this.vcfLineParser.createVcfEntryFromLine(line);
			foundLine = true;
			this.vcfEntry = vE;
		}
		if (!foundLine) {
			this.vcfEntry = new VcfEntry();
			this.buffReader.close();
			this.closed = true;
		}
	}

	/**
	 * Reads the next VCF line of the VCF file
	 * and sets the VcfEntry field.
	 * If there is no more line in the file, the
	 * field VcfEntry will be set to an empty entry and the
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

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
}
