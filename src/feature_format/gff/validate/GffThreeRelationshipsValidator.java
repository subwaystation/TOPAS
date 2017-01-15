package feature_format.gff.validate;

import feature_format.gff.GffThreeEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class GffThreeRelationshipsValidator {

	private HashMap<String, GffThreeEntry> uniqueIdsMap;
	private List<String> errorList;

	public GffThreeRelationshipsValidator(HashMap<String, GffThreeEntry> uniqueIdsMap) {
		this.uniqueIdsMap = uniqueIdsMap;
		this.errorList = new ArrayList<String>();
	}

	public HashMap<String, GffThreeEntry> getUniqueIdsMap() {
		return uniqueIdsMap;
	}

	public void setUniqueIdsMap(HashMap<String, GffThreeEntry> uniqueIdsMap) {
		this.uniqueIdsMap = uniqueIdsMap;
	}

	public List<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}

	public boolean validateRelationships() {
		boolean validateRelationships = true;
		for (Iterator<Entry<String, GffThreeEntry>> iterator = this.uniqueIdsMap.entrySet()
				.iterator(); iterator.hasNext();) {
			Entry<String, GffThreeEntry> entry = iterator.next();
			GffThreeEntry entryToValidate = entry.getValue();
			// current GFF3Entry has attribute Parent
			if (entryToValidate.hasParentValue()) {
				// check the type of the GFF3Entry
				String type = entryToValidate.getType();
//				this code was rewritten, because TOPAS should also be executable in a Java 1.6 environment
//				switch (type) {
//				case "region": break;
//				case "gene": break;
//				case "transcript": if (!validateTranscriptRelationship(entryToValidate)) {
//					validateRelationships = false;
//				}; break;
//				case "mRNA":
//				case "ncRNA":
//				case "rRNA":
//				case "tRNA":	if (!validateRnaRelationship(entryToValidate)) {
//					validateRelationships = false;
//				}; break;
//				case "exon": if (!validateExonRelationship(entryToValidate)) {
//					validateRelationships = false;
//				}; break;
//				case "CDS": if (!validateCdsRelationship(entryToValidate)) {
//					validateRelationships = false;
//				}; break;
//				case "five_prime_UTR": 
//				case "three_prime_UTR": if (!validateUtrRelationship(entryToValidate)) {
//					validateRelationships = false;
//				}; break;
//				default: validateRelationships = false; 
//				this.errorList.add("[line " + entryToValidate.getLine() + "] Unrecognised type. Supported types are: 'region', 'gene',"
//						+ " 'transcript', 'ncRNA', 'mRNA', 'tRNA', 'rRNA', 'exon', 'CDS', 'five_prime_UTR', 'three_prime_UTR'."); break;
//				}
				if (type.equals("region")) {
					
				} else {
					if (type.equals("gene")) {
					
					} else {
						if (type.equals("transcript")) {
							if (!validateTranscriptRelationship(entryToValidate)) {
								validateRelationships = false;
							}
						} else {
							if (type.equals("mRNA") || type.equals("ncRNA") || type.equals("rRNA") || type.equals("tRNA")) {
								if (!validateRnaRelationship(entryToValidate)) {
									validateRelationships = false;
								}
							} else {
								if (type.equals("exon")) {
									if (!validateExonRelationship(entryToValidate)) {
										validateRelationships = false;
									}
								} else {
									if (type.equals("CDS")) {
										if (!validateCdsRelationship(entryToValidate)) {
											validateRelationships = false;
										}
									} else {
										if (type.equals("five_prime_UTR") || type.equals("three_prime_UTR")) {
											if (!validateUtrRelationship(entryToValidate)) {
												validateRelationships = false;
											}
										} else {
											validateRelationships = false;
											this.errorList.add("[line " + entryToValidate.getLine() + "] Unrecognised type. Supported types are: 'region', 'gene',"
													+ " 'transcript', 'ncRNA', 'mRNA', 'tRNA', 'rRNA', 'exon', 'CDS', 'five_prime_UTR', 'three_prime_UTR'.");
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return validateRelationships;
	}

	private boolean validateUtrRelationship(GffThreeEntry entryToValidate) {
		String parentValue = entryToValidate.getParentValue();
		long line = entryToValidate.getLine();
		if (parentExists(parentValue)) {
			GffThreeEntry parent = getParent(parentValue);
			long parentLine = parent.getLine();
			String parentType = parent.getType();
			if (parentType.equals("mRNA") || parentType.equals("tRNA") || parentType.equals("rRNA") || parentType.equals("ncRNA")) {
				return true;
			} else {
				if (parentType.equals("transcript")) {
					return true;
				} else {
					this.errorList.add("[line " + line + "] parent violates relationship rules: parent "
							+ "(line " + parentLine + ") is of type '"
							+ parentType + "'. Only allowed parent types for UTRs are 'transcript' or 'mRNA' or 'tRNA'.");
					return false;
				}
			}
		} else {
			this.errorList.add("[line " + line + "] non-unique or non-existing parent (Parent=" + parentValue + ").");
			return false;
		}
	}

	private boolean validateCdsRelationship(GffThreeEntry entryToValidate) {
		String parentValue = entryToValidate.getParentValue();
		long line = entryToValidate.getLine();
		if (parentExists(parentValue)) {
			GffThreeEntry parent = getParent(parentValue);
			long parentLine = parent.getLine();
			String parentType = parent.getType();
			if (parentType.equals("gene")) {
				return true;
			} else {
				if (parentType.equals("mRNA") || parentType.equals("tRNA") || parentType.equals("rRNA") || parentType.equals("ncRNA")) {
					return true;
				} else {
					this.errorList.add("[line " + line + "] parent violates relationship rules: parent "
							+ "(line " + parentLine + ") is of type '"
							+ parentType + "'. Only allowed parent types for CDSs are 'gene' or 'mRNA'.");
					return false;
				}
			}
		} else {
			this.errorList.add("[line " + line + "] non-unique or non-existing parent (Parent=" + parentValue + ").");
			return false;
		}
	}

	private boolean validateExonRelationship(GffThreeEntry entryToValidate) {
		boolean validateExonRelationship = true;
		String parentValue = entryToValidate.getParentValue();
		long line = entryToValidate.getLine();
		// an exon can have several parents ('transcript' or 'mRNA')
		String[] parentValues = parentValue.split(",");
		for (int i = 0; i < parentValues.length; i++) {
			if (parentExists(parentValue)) {
				GffThreeEntry parent = getParent(parentValue);
				long parentLine = parent.getLine();
				String parentType = parent.getType();
				if (parentType.equals("mRNA") || parentType.equals("tRNA") || parentType.equals("rRNA") || parentType.equals("ncRNA")) {

				} else {
					if (parentType.equals("transcript")) {

					} else {
						this.errorList.add("[line " + line + "] parent violates relationship rules: parent "
								+ "(line " + parentLine + ") is of type '"
								+ parentType + "'. Only allowed parent types for exons are 'transcript' or 'mRNA' or 'tRNA'.");
						validateExonRelationship = false;
					}
				}
			} else {
				this.errorList.add("[line " + line + "] non-unique or non-existing parent (Parent=" + parentValue + ").");
				validateExonRelationship = false;
			}
		}
		return validateExonRelationship;
	}

	/**
	 * validates the parent(s) of a gff3entry with type 'mRNA' or 'tRNA
	 * @param entryToValidate
	 * @return
	 */
	private boolean validateRnaRelationship(GffThreeEntry entryToValidate) {
		String parentValue = entryToValidate.getParentValue();
		long line = entryToValidate.getLine();
		if (parentExists(parentValue)) {
			GffThreeEntry parent = getParent(parentValue);
			long parentLine = parent.getLine();
			String parentType = parent.getType();
			if (parentType.equals("transcript") || parentType.equals("gene")) {
				return true;
			} else {
				this.errorList.add("[line " + line + "] parent violates relationship rules: parent "
						+ "(line " + parentLine + ") is of type '"
						+ parentType + "'. Only allowed parent types for mRNAs are 'transcript' or 'gene'.");
				return false;
			}
		} else {
			this.errorList.add("[line " + line + "] non-unique or non-existing parent (Parent=" + parentValue + ").");
			return false;
		}
	}

	/**
	 * validates the parent of a gff3entry with type 'transcript'
	 * @return
	 */
	private boolean validateTranscriptRelationship(GffThreeEntry entryToValidate) {
		String parentValue = entryToValidate.getParentValue();
		long line = entryToValidate.getLine();
		if (parentExists(parentValue)) {
			GffThreeEntry parent = getParent(parentValue);
			long parentLine = parent.getLine();
			String parentType = parent.getType();
			if (parentType.equals("gene")) {
				return true;
			} else {
				this.errorList.add("[line " + line + "] parent violates relationship rules: parent "
						+ "(line " + parentLine + ") is of type '"
						+ parentType + "'. Only allowed parent type for transcript is 'gene'.");
				return false;
			}
		} else {
			this.errorList.add("[line " + line + "] non-unique or non-existing parent (Parent=" + parentValue + ").");
			return false;
		}
	}

	/**
	 * checks, if there is a GFF3Entry with the 'ID=parent'
	 * @param parent
	 * @return
	 */
	private boolean parentExists(String parent) {
		if (this.uniqueIdsMap.containsKey(parent)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * gets a GFF3Entry with 'ID=parent'
	 * @param parent
	 * @param type
	 * @return
	 */
	private GffThreeEntry getParent(String parent) {
		return this.uniqueIdsMap.get(parent);
	}
}
