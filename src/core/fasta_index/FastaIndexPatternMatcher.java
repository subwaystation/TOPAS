package core.fasta_index;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FastaIndexPatternMatcher {
	
	private List<FastaIndex> faidxList;
	private String pattern;
	/**
	 * @param faidxList
	 * @param pattern
	 */
	public FastaIndexPatternMatcher(List<FastaIndex> faidxList, String pattern) {
		this.faidxList = faidxList;
		this.pattern = pattern;
	}
	public List<FastaIndex> getFaidxList() {
		return faidxList;
	}
	public void setFaidxList(List<FastaIndex> faidxList) {
		this.faidxList = faidxList;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * checks, if the given pattern matches a fastaIndex in a fastaIndexList
	 * if the pattern does not match, the fastaIndex is removed
	 * @return the resulting fastaIndexList
	 * @throws UnsupportedEncodingException 
	 */
	public List<FastaIndex> getMatchedFaidxList() throws UnsupportedEncodingException {
		List<FastaIndex> resultFaidxList = new ArrayList<FastaIndex>();
		// System.out.println(this.pattern);
		Pattern pat = Pattern.compile(this.pattern);
		for(int i = 0; i < this.faidxList.size(); i++) {
			
			String identifier = this.faidxList.get(i).getSequenceName();
			Matcher m = pat.matcher(identifier);
			boolean foundPattern = m.find();
			// System.out.println(foundPattern);
			if(foundPattern) {
				resultFaidxList.add(this.faidxList.get(i));
			}
		}
		Charset charset = Charset.defaultCharset();
		System.out.println("Default encoding: " + charset + " (Aliases: "
				+ charset.aliases() + ")");
		return resultFaidxList;
	}

}
