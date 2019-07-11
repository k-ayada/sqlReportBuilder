package pub.ayada.exeSqls.jsons;

public class GenericVars {
	String userid;
	String dbRegion;
	String fileType;
	String separator;
	boolean encloseStr=true;
	boolean trimStrings= true;
	boolean wordwrap=false;

	public GenericVars(){}
	
	public GenericVars(String userid, String dbRegion, String fileType, String separator,
			boolean trimStrings, boolean wordwrap) {
		super();
		this.userid = userid;
		this.dbRegion = dbRegion;
		this.fileType = fileType;
		this.separator = separator;
		this.trimStrings = trimStrings;
		this.wordwrap = wordwrap;
	}

	public boolean isEncloseStr() {
		return encloseStr;
	}

	public void setEncloseStr(boolean encloseStr) {
		this.encloseStr = encloseStr;
	}

	public String getUserid() {
		return this.userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getDbRegion() {
		return this.dbRegion;
	}

	public void setDbRegion(String dbRegion) {
		this.dbRegion = dbRegion;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getSeparator() {
		return this.separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public boolean isTrimStrings() {
		return this.trimStrings;
	}

	public void setTrimStrings(boolean trimStrings) {
		this.trimStrings = trimStrings;
	}

	public boolean isWordwrap() {
		return this.wordwrap;
	}

	public void setWordwrap(boolean wordwrap) {
		this.wordwrap = wordwrap;
	}

	
	public String toString() {
		
		
		StringBuilder sb = new StringBuilder(); 
		
		
		sb.append("{ \"UserId\" : \"" + this.userid + "\",");
		
		sb.append(" \"dbRegion\" : \"" + this.dbRegion + "\",");
		sb.append(" \"fileType\" : \"" + this.fileType + "\",");
		
		sb.append(" \"separator\" : \"" + this.separator + "\",");
		
		sb.append(" \"trimStrings\" : \"" + this.trimStrings + ",");
		sb.append(" \"wordwrap\" : \"" + this.wordwrap + "}");

		return sb.toString();
		
	}
}
