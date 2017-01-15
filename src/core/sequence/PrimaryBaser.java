package core.sequence;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author heumos
 * The PrimaryBaser alters a given DNA sequence as follows:
 * A,C,T,G are not altered.
 * In the following, another bases than A would often be possible, but
 * A is preferred.
 * R -> A
 * Y -> C
 * K -> G
 * M -> A
 * S -> C
 * W -> A
 * B -> C
 * D -> A
 * H -> A
 * V -> A
 * N -> A
 */
public class PrimaryBaser {
	
    public static final Map<String, String> SEC_DNA_BASES_MAP;
    static {
        HashMap<String, String> secBasesMap = new HashMap<String, String>();
        secBasesMap.put("R", "A");
        secBasesMap.put("Y", "C");
        secBasesMap.put("K", "G");
        secBasesMap.put("M", "A");
        secBasesMap.put("S", "C");
        secBasesMap.put("W", "A");
        secBasesMap.put("B", "C");
        secBasesMap.put("D", "A");
        secBasesMap.put("H", "A");
        secBasesMap.put("V", "A");
        secBasesMap.put("N", "A");
        SEC_DNA_BASES_MAP = Collections.unmodifiableMap(secBasesMap);
    }
	
	// the sequence one wants to alter
	private String sequence;
	// the current line in the FASTA file
	private int lines;
	
	// the altered sequence as a StringBuilder Object
	private StringBuilder  primaryBasedSeq;
	// how many Ns were replaced?
	private int numNs;
	// how many other bases were replaced?
	private int numOther;
	
	public PrimaryBaser(String seq, int lines) {
		this.sequence = seq;
		this.lines = lines;
		this.numNs = 0;
		this.numOther = 0;
	}
	
	/**
	 * @return
	 * StringBuilder of altered sequence
	 * alters the sequence object of this class, making it a primary base sequence
	 * prints out warning if a base is not in IUPAC format
	 */
	public StringBuilder primaryBaseSeq() {
		StringBuilder seqBuilder = new StringBuilder();
		for (int i = 0; i < this.sequence.length(); i++) {
			char charBase = this.sequence.charAt(i);
			String base = String.valueOf(charBase);
			// the current base is a primary base, we can continue to the next iteration
			if (DnaSequence.DNA_BASES_SET.contains(base)) {
				seqBuilder.append(base);
				continue;
				// the current base is not a primary base, it has to be altered
			} else {
				// is the current base a secondary base?
				if (SEC_DNA_BASES_MAP.containsKey(base)) {
					if (base.equals("N")) {
						this.numNs++;
					}
					String secBase = SEC_DNA_BASES_MAP.get(base);
					seqBuilder.append(secBase);
				} else {
					System.out.println("[WARNING] in Line " + this.lines +
							": Found base '" + base + "' at position '" + (i+1) +
							"' that is not in IUPAC format! Changing Base to 'A'.");
					seqBuilder.append("A");
				}
			}
		}
		this.primaryBasedSeq = seqBuilder;
		return seqBuilder;
	}
	
	public int getNumNs() {
		return numNs;
	}

	public void setNumNs(int numNs) {
		this.numNs = numNs;
	}

	public int getNumOther() {
		return numOther;
	}

	public void setNumOther(int numOther) {
		this.numOther = numOther;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}

	public String primaryBaseSeqString() {
		return primaryBaseSeq().toString();
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public StringBuilder getPrimaryBasedSeq() {
		return primaryBasedSeq;
	}

}
