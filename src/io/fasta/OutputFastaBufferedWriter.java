package io.fasta;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// FIXME Currently not working!!!

public class OutputFastaBufferedWriter {

	private BufferedWriter bW;
	private int lineWidth;

	private String basesBuffer;
	private boolean firstLine;

	public OutputFastaBufferedWriter(String outputFile, int lineWidth) {
		try {
			this.bW = new BufferedWriter(new FileWriter(outputFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.lineWidth = lineWidth;
		this.firstLine = true;
		this.basesBuffer = "";
	}

	public void writeIdentifier(String identifier) {
		identifier = ">" + identifier;
		if (firstLine) {
			try {
				this.bW.write(identifier);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			firstLine = false;
		} else {
			try {
				this.bW.newLine();
				if (!this.basesBuffer.equals("")) {
					this.bW.write(this.basesBuffer);
					this.basesBuffer = "";
					this.bW.newLine();
				}				
				this.bW.write(identifier);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addBases(String bases) {
		StringBuilder sB = new StringBuilder();
		sB.append(this.basesBuffer);
		sB.append(bases);
		this.basesBuffer = sB.toString();
		if (this.basesBuffer.length() >= this.lineWidth) {
			writeBases();
		}
	}

	private void writeBases() {
		while (this.basesBuffer.length() >= this.lineWidth) {
			String basesToWrite = this.basesBuffer.substring(0, this.lineWidth);
			this.basesBuffer = this.basesBuffer.substring(this.lineWidth);
			try {
				this.bW.newLine();
				this.bW.write(basesToWrite);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void finalizeWrite() {
		if (!this.basesBuffer.equals("")) {
			try {
				this.bW.newLine();
				this.bW.write(this.basesBuffer);
				this.bW.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				this.bW.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
