package lib.normalize;

import java.util.HashMap;
import java.util.List;

import feature_format.AFeatureFormatEntry;

public class RPKM {
	
	private static double normalize(double tagCount, double totalTagCount, double kiloBaseOfTranscript) {
		return (tagCount*1000000.0)/(totalTagCount*kiloBaseOfTranscript);
	}
	
	public static void normList(List<Double> exprValues, HashMap<String, List<AFeatureFormatEntry>> gEMap,
			List<String> geneNames, Double totalTagCount, HashMap<String, Double> transcriptSizes) {
		for (int i = 0; i < geneNames.size(); i++) {
			double tagCount = exprValues.get(i);
			if (tagCount == Double.NaN) {

			} else {
				String geneName = geneNames.get(i);
				if(transcriptSizes.containsKey(geneName)) {
					double transcriptSize = transcriptSizes.get(geneName);
					double kiloBaseOfTranscript = transcriptSize/1000;
					double normExprValue = normalize(tagCount, totalTagCount, kiloBaseOfTranscript);
					exprValues.set(i, normExprValue);
				}//else keep old value -> these are most likely htseq specific rows
			}			
		}
	}
}
