package pretesting.fast_line_test;

import java.io.FileReader;

public class TestFastLine {
	
	private final static String TEST = "CP000239.gff";

	public static void main(String[] args) {

		// This example uses character arrays instead of Strings.
		// It doesn't use BufferedReader or BufferedFileReader, but does
		// the buffering by itself so that it can avoid creating too many
		// String objects.  For simplicity, it assumes that no line will be
		// longer than 128 characters.
		
		args[0] = TEST;

		FileReader fr;
		int nlines = 0;
		char buffer[] = new char[8192 + 1];
		int maxLineLength = 8192;

		//assumes no line is longer than this
		char lineBuf[] = new char[maxLineLength];
		for (int i=0; i < args.length; i++) {
			try {
				fr = new FileReader(args[i]);

				int nChars = 0;
				int nextChar = 0;
				int startChar = 0;
				boolean eol = false;
				int lineLength = 0;
				char c = 0;
				int n;
				int j;

				while (true) {
					if (nextChar >= nChars) {
						n = fr.read(buffer, 0, 8192);
						if (n == -1) {  // EOF
							break;
						}
						nChars = n;
						startChar = 0;
						nextChar = 0;
					}

					for (j=nextChar; j < nChars; j++) {
						c = buffer[j];
						if ((c == '\n') || (c == '\r')) {
							eol = true;
							break;
						}
					}
					nextChar = j;

					int len = nextChar - startChar;
					if (eol) {
						nextChar++;
						if ((lineLength + len) > maxLineLength) {
							// error
						} else {
							System.arraycopy(buffer, startChar, lineBuf, lineLength, len);
						}
						lineLength += len;

						//
						// Process line here
						//
						nlines++;
						System.out.println(new String(lineBuf));
						

						if (c == '\r') {
							if (nextChar >= nChars) {
								n = fr.read(buffer, 0, 8192);
								if (n != -1) {
									nextChar = 0;
									nChars = n;
								}
							}

							if ((nextChar < nChars) && (buffer[nextChar] == '\n'))
								nextChar++;
						}
						startChar = nextChar;
						lineLength = 0;
						continue;
					}

					if ((lineLength + len) > maxLineLength) {
						// error
					} else {
						System.arraycopy(buffer, startChar, lineBuf, lineLength, len);
					}
					lineLength += len;
				}
				fr.close();
			} catch (Exception e) {
				System.out.println("exception: " + e);
			}
		}

	}

}
