package feature_format.gff.validate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.VerifyClassType;

public class GffThreeEntryValidator {

	String seqId;
	String source;
	String type;
	String start;
	String end;
	String score;
	String strand;
	String phase;
	String attributes;
	List<String> entryErrorList;
	long totalLines;

	/**
	 * @param seqId
	 * @param source
	 * @param type
	 * @param start
	 * @param end
	 * @param score
	 * @param strand
	 * @param phase
	 * @param attributes
	 * @param totalLines
	 */
	public GffThreeEntryValidator(String seqId, String source, String type, String start, String end,
			String score, String strand, String phase, String attributes, long totalLines) {
		this.seqId = seqId;
		this.source = source;
		this.type = type;
		this.start = start;
		this.end = end;
		this.score = score;
		this.strand = strand;
		this.phase = phase;
		this.attributes = attributes;
		this.entryErrorList = new ArrayList<String>();
		this.totalLines = totalLines;
	}


	/**
	 * validates a gff3 entry
	 * @return
	 */
	public boolean validate() {
		boolean validateSeqId = validateSeqId();
		boolean validateSource = validateSource();
		boolean validateType = validateType();
		boolean validateStartEnd = validateStartEnd();
		boolean validateScore = validateScore();
		boolean validateStrand = validateStrand();
		boolean validatePhase = validatePhase();
		boolean validateAttributes = validateAttributes();
		return validateSeqId && validateSource && validateType && validateStartEnd &&
				validateScore && validateStrand && validatePhase && validateAttributes;
	}

	public List<String> getErrorList() {
		return entryErrorList;
	}


	public void setErrorList(List<String> errorList) {
		this.entryErrorList = errorList;
	}


	public long getTotalLines() {
		return totalLines;
	}


	public void setTotalLines(long totalLines) {
		this.totalLines = totalLines;
	}


	/**
	 * validates the seqid field of a gff3 entry
	 * @return
	 */
	private boolean validateSeqId() {
		boolean validateSeqId = true;
		String seqId = this.seqId.trim();
		// cannot be empty (dot)
		if (seqId.equals(".")) {
			validateSeqId = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field seqId must not contain a dot.");
			return validateSeqId;
		}
		if (seqId.equals("")) {
			validateSeqId = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field seqId is not allowed to be empty.");
			return validateSeqId;
		}
		// only following characters are allowed [a-zA-Z0-9\.\:\^\*\$\@\!\+\_\?\-\|\%]
		String pattern = "[a-zA-Z0-9\\.\\:\\^\\*\\$\\@\\!\\+\\_\\?\\-\\|\\%]";
		validateSeqId = validateS(seqId, pattern, "seqid");
		return validateSeqId;
	}

	/**
	 * validates the source field of a gff3 entry
	 * @return
	 */
	private boolean validateSource() {
		boolean validateSource = true;
		String source = this.source.trim();
		// can be empty (dot)
		if (source.equals("")) {
			validateSource = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field source is not allowed to be empty.");
			return validateSource;
		}
		// only following characters are allowed [a-zA-Z0-9\.\: \^\*\$\@\!\+\_\?\-\%]
		String pattern = "[a-zA-Z0-9\\.\\: \\^\\*\\$\\@\\!\\+\\_\\?\\-\\%]";
		validateSource = validateS(source, pattern, "source");
		return validateSource;
	}

	/**
	 * validates the type field of a gff3 entry
	 * @return
	 */
	private boolean validateType() {
		boolean validateType = true;
		String type = this.type.trim();
		// cannot be empty (dot)
		if (type.equals(".")) {
			validateType = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field type must not contain a dot.");
			return validateType;
		}
		if (type.equals("")) {
			validateType = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field type is not allowed to be empty.");
			return validateType;
		}
		// only following characters are allowed [a-zA-Z0-9\.\: \^\*\$\@\!\+\_\?\-]
		String pattern = "[a-zA-Z0-9\\.\\: \\^\\*\\$\\@\\!\\+\\_\\?\\-]";
		validateType = validateS(type, pattern, "type");
		if (validateType) {
			List<String> typeList = generateTypeList();
			if (!typeList.contains(type)) {
				this.entryErrorList.add("[line " + this.totalLines + "] Unrecognised type '" + type + "'."
						+ " Types that the GFF3EntryValidator supports are: 'region', 'gene',"
						+ " 'transcript', 'ncRNA', 'mRNA', 'tRNA', 'rRNA', 'exon', 'CDS', 'five_prime_UTR', 'three_prime_UTR'.");
				validateType = false;
			}
		}
		return validateType;
	}

	/**
	 * validates the start and end field of a gff3 entry
	 * @return
	 */
	private boolean  validateStartEnd() {
		boolean validateStartEnd = true;
		String start = this.start.trim();
		String end = this.end.trim();
		if (start.equals("")) {
			validateStartEnd = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field start is not allowed to be empty, a dot is also not allowed.");
			return validateStartEnd;
		}
		if (end.equals("")) {
			validateStartEnd = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field end is not allowed to be empty, a dot is also not allowed.");
			return validateStartEnd;
		}
		boolean startIntParsable = VerifyClassType.isInteger(start);
		boolean endIntParsable = VerifyClassType.isInteger(end);
		String errorMessage = "[line " + this.totalLines + "] ";
		String startIntError = "";
		String endIntError = "";
		String endGreaterEqualStartError = "";
		String oneBasedCoordinateError = "";
		// check if start is an integer, cannot be empty (dot)
		if(!startIntParsable) {
			startIntError = "Character(s) in field start do(es) not represent an integer. ";
			validateStartEnd = false;
		}
		// check if end is an integer, cannot be empty (dot)
		if(!endIntParsable) {
			endIntError = "Character(s) in field end do(es) not represent an integer. ";
			validateStartEnd = false;
		}
		// check if start is greater than 0 (1-based coordinate system)
		if(startIntParsable) {
			int startInt = Integer.valueOf(start);
			if(!(startInt>0)) {
				oneBasedCoordinateError = "Field start must contain an integer bigger than zero (1-based coordinate system). ";
				validateStartEnd = false;
			}
		}
		// check if start and end are integers, check if end is greater/equal start
		if(startIntParsable&&endIntParsable) {
			int startInt = Integer.valueOf(start);
			int endInt = Integer.valueOf(end);
			if(!(endInt>=startInt)) {
				endGreaterEqualStartError = "Field end must be greater/equal field start. ";
				validateStartEnd = false;
			}
		}
		if(!validateStartEnd) {
			// add error messages
			if(startIntError.length()>5) {
				errorMessage += startIntError;
			}
			if(endIntError.length()>5) {
				errorMessage += endIntError;
			}
			if(oneBasedCoordinateError.length()>5) {
				errorMessage += oneBasedCoordinateError;
			}
			if(endGreaterEqualStartError.length()>5) {
				errorMessage += endGreaterEqualStartError;
			}
			this.entryErrorList.add(errorMessage);
		}		
		return validateStartEnd;
	}

	/**
	 * validates the score
	 * @return
	 */
	private boolean validateScore() {
		boolean validateScore = true;
		String score = this.score.trim();
		// can be empty (dot)
		if (score.equals(".")) {
			return validateScore;
		}
		if (score.equals("")) {
			validateScore = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field score is not allowed to be empty, a dot is allowed.");
			return validateScore;
		}
		// must be a floating number (e.g. "1.2", "5", "1e-10", "-1e+10") 
		// (for scientific notation: /^[\+\-]{0,1}\d+(e|E)[\+\-]\d+$/ is used)
		String pattern = "/^[\\+\\-]{0,1}\\d+(e|E)[\\+\\-]\\d+$/|\\.|\\d";
		String fieldName = "score (only floating numbers or a dot are allowed)";
		validateScore = validateS(score, pattern, fieldName);
		return validateScore;
	}

	/**
	 * validates the strand
	 * @return
	 */
	private boolean validateStrand() {
		boolean validateStrand = true;
		String strand = this.strand.trim();
		// must be + or -
		// can be empty (dot)
		if (strand.equals("")) {
			validateStrand = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field strand is not allowed to be empty, a dot is allowed.");
			return validateStrand;
		}
		String pattern = "\\+|\\-|\\.";
		String fieldName = "strand (only '+' or '-' or '.' are allowed).";
		validateStrand = validateS(strand, pattern, fieldName);
		return validateStrand;
	}

	/**
	 * validates the phase
	 * @return
	 */
	private boolean validatePhase() {
		boolean validatePhase = true;
		String phase = this.phase.trim();
		// must be 0 or 1 or 2
		// can be empty (dot)
		if (phase.equals("")) {
			validatePhase = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field phase is not allowed to be empty, a dot is allowed.");
			return validatePhase;
		}
		if(!(phase.equals("0")||phase.equals("1")||phase.equals("2")||phase.equals("."))) {
			this.entryErrorList.add("[line " + this.totalLines + "] Anallowed character(s) in field phase (only '0' or '1' or '2' or '.' are allowed.)");
			validatePhase = false;
		}
		// must be 0, 1 or 2 for CDS features
		if(!(phase.equals("0")||phase.equals("1")||phase.equals("2"))&&this.type.trim().equals("CDS")) {
			this.entryErrorList.add("[line " + this.totalLines + "] Phase must be 0, 1 or 2 for CDS features.");
			validatePhase = false;
		}
		return validatePhase;
	}

	/**
	 * @return
	 */
	private boolean validateAttributes() {
		boolean validateAttributes = true;
		boolean uniqueTag = true;
		// hasTagID has only to be true, if the GFF3Entry is of type region, gene, mRNA, transcript or exon
		boolean hasTagId = false;
		boolean needsTagId = false;
		// can be empty (dot)
		if (this.attributes.equals(".")) {
			return validateAttributes;
		}
		if (phase.equals("")) {
			validateAttributes = false;
			this.entryErrorList.add("[line " + this.totalLines + "] Field attributes is not allowed to be empty, a dot is allowed.");
			return validateAttributes;
		}
		String[] atts = this.attributes.split(";");
		Set<String> uniqueAttributes = new HashSet<String>();
		for (int i = 0; i < atts.length; i++) {
			String[] attribute = atts[i].split("=");
			String tag;
			String value;
			// attributes are in tag=value format, separated by semicolon
			if (attribute.length > 1) {
				tag = attribute[0];
				value = attribute[1];
			} else {
				validateAttributes = false;
				this.entryErrorList.add("[line " + this.totalLines + "] Attributes must be in tag=value format.");
				return validateAttributes;
			}	
			// tag=value pairs are parsed based on first equals sign
			// value cannot contain an unescaped equals sign
			if (attribute.length > 2) {
				validateAttributes = false;
				this.entryErrorList.add("[line " + this.totalLines + "] Only one '=' in each attribute is allowed."
						+ " To still provide the 'tag=value' format please escape all unnecessary '='.");
				return validateAttributes;
			}
			// value cannot be empty
			if (value.length() == 0) {
				validateAttributes = false;
				this.entryErrorList.add("[line " + this.totalLines + "] An empty value in an attribute is not allowed.");
			}
			// tag cannot have a preceding or leading space and cannot be empty
			if (tag.length() == 0) {
				validateAttributes = false;
				this.entryErrorList.add("[line " + this.totalLines + "] An empty tag in an attribute is not allowed.");
				return validateAttributes;
			}
			if (tag.startsWith(" ")||tag.endsWith(" ")) {
				validateAttributes = false;
				this.entryErrorList.add("[line " + this.totalLines + "] A tag cannot have a preceding or leading space. Please correct tag '" + tag + "'.");
				return validateAttributes;
			}
			// each tag should must appear only once, multiple values can be provided to a tag separated by comma
			if (uniqueAttributes.contains(tag)) {
				uniqueTag = false;
				validateAttributes = false;
			} else {
				uniqueAttributes.add(tag);
			}
			// a tag can only have an upper case beginning char, if it is one of the following:
			// ID, Name, Alias, Parent, Target, Gap, Derives_from, Note, Dbxref, Ontology_term, Is_circular
			if (Pattern.matches("[A-Z]", tag.substring(0, 1))) {
				ArrayList<String> tagList = generateReservedUpperCaseTagsList();
				if (!tagList.contains(tag)) {
					this.entryErrorList.add("[line " + this.totalLines + "] A tag can only have an upper case beginning char, if it is one of the following: "
							+ "ID, Name, Alias, Parent, Target, Gap, Derives_from, Note, Dbxref, Ontology_term, Is_circular."
							+ "Please rename tag '" + tag + "'.");
					validateAttributes = false;
				}
			}
		}		
		if (!uniqueTag) {
			this.entryErrorList.add("[line " + this.totalLines + "] Each tag must appear only once, please make your tags unique.");
		}
		// check, if an ID is necessary (for later parentage checking)
		if (validateAttributes) {
			// check if the GFF3Entry is of type region, gene, mRNA, tRNA, rRNA, transcript
			ArrayList<String> typeList = generateIdTypeList();
			if (typeList.contains(this.type.toLowerCase())) {
				needsTagId = true;
				String[] attributes = this.attributes.split(";");
				for (int i = 0; i < attributes.length; i++) {
					String[] attribute = attributes[i].split("=");
					String tag = attribute[0];
					if (tag.equals("ID")) {
						hasTagId = true;
					}
				}	
			}					
		}
		// what if we did not find a tagID although it was necessary
		if (needsTagId && !hasTagId) {
			validateAttributes = false;
			this.entryErrorList.add("[line " + this.totalLines + "] A GFF3Enty of type region, gene, mRNA, tRNA, rRNA, ncRNA, transcript or exon must have a unique ID.");
		}
		return validateAttributes;
	}

	private ArrayList<String> generateIdTypeList() {
		ArrayList<String> typeIdList = new ArrayList<String>();
		typeIdList.add("region");
		typeIdList.add("gene");
		typeIdList.add("ncrna");
		typeIdList.add("mrna");
		typeIdList.add("trna");
		typeIdList.add("rrna");
		typeIdList.add("transcript");
		return typeIdList;
	}
	
	private ArrayList<String> generateTypeList() {
		ArrayList<String> typeList = new ArrayList<String>();
		typeList.add("region");
		typeList.add("gene");
		typeList.add("mRNA");
		typeList.add("tRNA");
		typeList.add("rRNA");
		typeList.add("ncRNA");
		typeList.add("transcript");
		typeList.add("exon");
		typeList.add("CDS");
		typeList.add("five_prime_UTR");
		typeList.add("three_prime_UTR");
		return typeList;
	}

	private ArrayList<String> generateReservedUpperCaseTagsList() {
		ArrayList<String> tagList = new ArrayList<String>();
		tagList.add("ID");
		tagList.add("Name");
		tagList.add("Alias");
		tagList.add("Parent");
		tagList.add("Target");
		tagList.add("Gap");
		tagList.add("Derives_from");
		tagList.add("Note");
		tagList.add("Dbxref");
		tagList.add("Ontology_term");
		tagList.add("Is_circular");
		return tagList;
	}


	/**
	 * verifies one of the following gff3 fields:
	 * seqid, source, type, score
	 * @param fieldValue
	 * @param fieldPattern
	 * @param fieldName
	 * @return
	 * true, if the given gff3 field could be verified,
	 * else false: alters the field errorList in class GffThreeEntryValidator and
	 * adds an error message including the line, where the error occurred
	 */
	private boolean validateS(String fieldValue, String fieldPattern, String fieldName) {
		boolean validateS = true;
		Pattern r = Pattern.compile(fieldPattern);
		for (int i = 0; i < fieldValue.length(); i++) {
			// Now create matcher object.
			Matcher m = r.matcher(String.valueOf(fieldValue.charAt(i)));
			// char is allowed, do nothing (verified)
			if(m.find()) {

				// found char, which is not allowed
			} else {
				validateS = false;
			}
		}	
		if(!validateS) {
			this.entryErrorList.add("[line " + this.totalLines + "] Anallowed character(s) in field " + fieldName + ".");
		}
		return validateS;
	}

}
