package utils.identifiers;

public class IdentifierLine {
	
	private String indentifier;
	private int line;
	
	public IdentifierLine(String identifier, int line) {
		this.indentifier = identifier;
		this.line = line;
	}

	public String getIndentifier() {
		return indentifier;
	}

	public void setIndentifier(String indentifier) {
		this.indentifier = indentifier;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

}
