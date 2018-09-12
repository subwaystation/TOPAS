package feature_format.gff.filter;

import java.util.List;

import feature_format.gff.GffThreeEntry;

public abstract class AGffThreeFilter {
	
	protected GffThreeEntry gTE;
	
	public AGffThreeFilter(GffThreeEntry gTE) {
		this.gTE = gTE;
	}

	public GffThreeEntry getgTE() {
		return gTE;
	}

	public void setgTE(GffThreeEntry gTE) {
		this.gTE = gTE;
	}

	protected abstract boolean filter(List<String> listFilter);
}
