package lib.normalize;

import java.util.List;

public class CPM {
	
	private static double normalize(double tagCount, double totalTagCount) {
		return (tagCount*1000000.0)/(totalTagCount);
	}
	
	public static void normList(List<Double> exprValues, Double totalTagCount) {
		for (int i = 0; i < exprValues.size(); i++) {
			double tagCount = exprValues.get(i);
			if (tagCount == Double.NaN) {
				
			} else {
				double normExprValue = normalize(tagCount, totalTagCount);
				exprValues.set(i, normExprValue);
			}			
		}
	}
	
}
