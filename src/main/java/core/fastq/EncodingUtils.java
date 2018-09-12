package core.fastq;

/**
 * Encoding was taken over from http://en.wikipedia.org/wiki/FASTQ_format on 20.02.2014
 * @author bli.blau.blubb
 *
 */
public class EncodingUtils {

	public static String encode(int lowestBQ, int highestBQ) {
		if (highestBQ <= 74 && lowestBQ >= 35 && highestBQ >= 35 && lowestBQ <= 74) {
			return "Illumina 1.8, Phred+33, raw reads typically (0, 41)";
		} else {
			if (highestBQ <= 104 && highestBQ >= 66 && lowestBQ <= 104 && lowestBQ >=66) {
				return "Illumina 1.5, Phred+64, raw reads typically (3,40)\n" +
						"with 0=unused, 1=unused, 2=Read Segment Quality Control Indicator";
			} else {
				if (highestBQ <= 104 && highestBQ >= 59 && lowestBQ >= 59 && lowestBQ <= 104) {
					return "Solexa, Phred+64, raw reads typically (-5, 40)";
				} else {
					if (highestBQ <= 104 && highestBQ >= 64 && lowestBQ <= 104 && lowestBQ >= 64) {
						return "Illumina 1.3, Phred+64, raw reads typically (0, 40)";
					} else {
						if (highestBQ <= 73 && lowestBQ <=73) {
							return "Sanger, Phred+33, raw reads typically (0, 40)";
						} else {
							return "Could not find out encoding of fastq file.";
						}
					}
				}
			}
		}
	}

	public static boolean encode_(int lowestBQ, int highestBQ) {
		if (highestBQ <= 74 && lowestBQ >= 35 && highestBQ >= 35 && lowestBQ <= 74) {
			return true;
		} else {
			if (highestBQ <= 104 && highestBQ >= 66 && lowestBQ <= 104 && lowestBQ >=66) {
				return true;
			} else {
				if (highestBQ <= 104 && highestBQ >= 59 && lowestBQ >= 59 && lowestBQ <= 104) {
					return true;
				} else {
					if (highestBQ <= 104 && highestBQ >= 64 && lowestBQ <= 104 && lowestBQ >= 64) {
						return true;
					} else {
						if (highestBQ <= 73 && lowestBQ <=73) {
							return true;
						} else {
							return false;
						}
					}
				}
			}
		}
	}

}
