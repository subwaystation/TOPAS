package io.fasta;

import java.io.BufferedWriter;

public class FastaWriter {

	private BufferedWriter bW;
	private String line;
	private boolean isHeaderLine;
	private boolean firstHeader;
	private int seqWidth;
	private StringBuilder buffer;
	private String newLineType;

	public FastaWriter (BufferedWriter bW, String line, boolean isHeaderLine, int seqWidth, String newLineType) {
		this.bW = bW;
		this.line = line;
		this.isHeaderLine = isHeaderLine;
		this.firstHeader = true;
		this.seqWidth = seqWidth;
		this.buffer = new StringBuilder();
		this.newLineType = newLineType;
	}

	public BufferedWriter getbW() {
		return bW;
	}

	public void setbW(BufferedWriter bW) {
		this.bW = bW;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public boolean isHeader() {
		return isHeaderLine;
	}

	public void setIsHeaderLine(boolean isHeaderLine) {
		this.isHeaderLine = isHeaderLine;
	}

	public boolean isFirstHeader() {
		return firstHeader;
	}

	public void setFirstHeader(boolean firstHeader) {
		this.firstHeader = firstHeader;
	}

	public int getSeqWidth() {
		return seqWidth;
	}

	public void setSeqWidth(int seqWidth) {
		this.seqWidth = seqWidth;
	}
	
	// FIXME delete all sysouts

	public void writeLine() throws Exception {
		if (this.isHeaderLine) {
			if (!(this.buffer.length() == 0)) {
				if (this.firstHeader) {
					bW.write(this.line);
					writeEOL(bW, this.newLineType);
					bW.write(this.buffer.toString());
					this.firstHeader = false;
				} else {
					writeEOL(bW, this.newLineType);
					bW.write(this.buffer.toString());
					writeEOL(bW, this.newLineType);
					bW.write(this.line);
				}
			} else {
				if (this.firstHeader) {
					bW.write(this.line);
					this.firstHeader = false;
				} else {
					writeEOL(bW, this.newLineType);
					bW.write(this.buffer.toString());
					writeEOL(bW, this.newLineType);
					bW.write(this.line);
				}
			}
		} else {
			this.buffer.append(this.line);
			while (this.buffer.length() > this.seqWidth) {
				String seqLine = this.buffer.substring(0, this.seqWidth);
				writeEOL(bW, this.newLineType);
				bW.write(seqLine);
				this.buffer.delete(0, this.seqWidth);
			}
		}
	}

	private void writeEOL(BufferedWriter bW, String newLineType) throws Exception {
		if (newLineType.equals("10")) {
			bW.write((char) 10);
		} else {
			if (newLineType.equals("13")) {
				bW.write((char) 13);
			} else {
				if (newLineType.equals("1310")) {
					bW.write((char) 13);
					bW.write((char) 10);
				} else {
					throw new Exception("Invalid EOL");
				}
			}
		}
	}

	@Override
	public String toString() {
		return "Line " + this.line + "\n" + "EOL " + this.newLineType;
	}
}
