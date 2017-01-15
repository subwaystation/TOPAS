package io;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ParseBytes {

	String inputFile;
	long skipBytes;
	int offsetLength;



	/**
	 * @param inputFile
	 * @param skipBytes
	 * @param offsetLength
	 */
	public ParseBytes(String inputFile, long skipBytes,
			int offsetLength) {
		this.inputFile = inputFile;
		this.skipBytes = skipBytes;
		this.offsetLength = offsetLength;
	}

	public long getSkipBytes() {
		return skipBytes;
	}



	public void setSkipBytes(int skipBytes) {
		this.skipBytes = skipBytes;
	}



	public int getOffsetLength() {
		return offsetLength;
	}



	public void setOffsetLength(int offsetLength) {
		this.offsetLength = offsetLength;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public String parseBytes() throws IOException {
//		FileInputStream fileInputStream = new FileInputStream(this.inputFile);
//		byte[] b = new byte[this.offsetLength];
//		int offset = 0;
//		while(offset < this.offsetLength) {
//			fileInputStream.skip(this.skipBytes);
//			offset += fileInputStream.read(b, offset, (this.offsetLength - offset));
//		}
//		String result = new String(b);
//		fileInputStream.close();
//		return result;
		RandomAccessFile rAF = new RandomAccessFile(this.inputFile, "r");
		rAF.seek(this.skipBytes);
		byte[] b = new byte[this.offsetLength];
		rAF.read(b);
		String result = new String(b);
		return result;
	}

}
