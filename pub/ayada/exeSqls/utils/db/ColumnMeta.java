package pub.ayada.exeSqls.utils.db;

public class ColumnMeta {
	private String sqlName;
	private String sqlType;
	private String hdrText;
	private String rptFormat;
	private int rptDispLen;
	private String blankCol;

	public ColumnMeta() {
	}

	/*
	 * public ColumnMeta(String SqlName, String HdrText, String RptFormat)
	 * throws SQLException { this(SqlName,(String) null, HdrText,RptFormat,
	 * DBUtils.getLength(RptFormat, "CHAR")); }
	 */
	public ColumnMeta(String SqlName, String SqlType, String HdrText, String RptFormat, int DispLen) {
		this.sqlName = SqlName;
		this.sqlType = SqlType;
		this.hdrText = HdrText;
		this.rptFormat = RptFormat;
		this.rptDispLen = DispLen;
		this.blankCol = String.format("%1$"+this.rptDispLen+"s", " ");
	}	
	
	public String getSqlName() {
		return sqlName;
	}

	public void setSqlName(String SqlName) {
		this.sqlName = SqlName;
	}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public String getRptColHeader() {
		return this.hdrText;
	}

	public void setRptColHeader(String HdrText) {
		this.hdrText = HdrText;
	}

	public String getRptFormat() {
		return this.rptFormat;
	}

	public void setRptFormat(String RptFormat) {
		this.rptFormat = RptFormat;
	}

	public int getRptDispLen() {
		return this.rptDispLen;
	}

	public void setRptDispLen(int RptDispLen) {
		this.rptDispLen = RptDispLen;
		this.blankCol = String.format("%1$"+this.rptDispLen+"s", " ");
	}

	public String getStringRptFmt() {
		return new StringBuilder("%").append(getRptDispLen()).append("%s").toString();
	}

	public String getBlankColumn() {
		return String.format(getStringRptFmt(), " ");
	}

	public String getBlankCol() {
		return blankCol;
	}

}
