package vcf.analyse.window;

import java.util.ArrayList;
import java.util.List;

import vcf.analyse.Region;

public class WindowMerger {
	
	// the windows to merge
	private List<AFilterWindow> windows;
	
	// the resulting list of regions
	private List<Region> mergedWindows;
	
	public WindowMerger() {
		
	} 
	
	public void mergeWindows(String seqId) {
		this.mergedWindows = new ArrayList<>();
		int i = 0;
		int startRegion = -1;
		int endRegion = -1;
		AFilterWindow lastSeenWindow = null;
		for (AFilterWindow aFilterWindow : this.windows) {
			lastSeenWindow = aFilterWindow;
			if (i == 0) {
				endRegion = aFilterWindow.getEnd();
				startRegion = aFilterWindow.getStart();
				i++;
			} else {
				int start = aFilterWindow.getStart();
				// we have an overlap of the windows
				if ((start - 1) <= endRegion) {
					endRegion = aFilterWindow.getEnd();
				} else {
					// finish current region
					Region region = new Region(startRegion, endRegion, seqId);
					region.addReason(aFilterWindow.getReason());
					this.mergedWindows.add(region);
					
					// update start and end
					startRegion = aFilterWindow.getStart();
					endRegion = aFilterWindow.getEnd();
				}
			}
		}
		// finish last region if possible
		if (this.windows.size() > 0) {
			Region region = new Region(startRegion, endRegion, seqId);
			region.addReason(lastSeenWindow.getReason());
			this.mergedWindows.add(region);
		}
	}

	public List<AFilterWindow> getWindows() {
		return windows;
	}

	public void setWindows(List<AFilterWindow> windows) {
		this.windows = windows;
	}

	public List<Region> getMergedWindows() {
		return mergedWindows;
	}

	public void setMergedWindows(List<Region> mergedWindows) {
		this.mergedWindows = mergedWindows;
	}
	
}
