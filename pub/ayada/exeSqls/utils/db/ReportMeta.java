package pub.ayada.exeSqls.utils.db;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

public class ReportMeta {

	private final ColumnMeta[] cols;
	private final String colHdrLine;
	private final String rptTemplate;
	private final String rptSeparator;
	private final static String oneCharStr = "\"%-1s\"";

	private ReportMeta(ColumnMeta[] ColMetaArr, String colSeparator, boolean trimString) {
		this.cols = ColMetaArr;
		this.colHdrLine = buildColHdrLine(colSeparator, trimString);
		this.rptSeparator = colSeparator;
		this.rptTemplate = buildRptTemplate();
	}

	private String buildRptTemplate() {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.cols.length; i++) {
			sb.append(new StringBuilder(cols[i].getRptFormat()).append(this.rptSeparator).toString());
		}
		sb.setLength(sb.length() - this.rptSeparator.length());
		return sb.toString();
	}

	public String getRptTemplate() {
		return this.rptTemplate;
	}

	public ColumnMeta[] getColMetaArr() {
		return this.cols;
	}

	public ColumnMeta getColMeta(int i) {
		return this.cols[i];
	}

	public String getColHdrLine() {
		return colHdrLine;
	}

	public String getRptSeparator() {
		return rptSeparator;
	}

	private String buildColHdrLine(String colSeparator, boolean trimString) {

		StringBuilder sb = new StringBuilder();
		String[] colhdr = new String[cols.length];

		if (trimString) {
			for (int i = 0; i < this.cols.length; i++) {
				sb.append(new StringBuilder("%").append(i + 1) // String.format()'s
																// parm inx
																// starts for 1
						.append("$1s").append(colSeparator).toString());
				colhdr[i] = cols[i].getRptColHeader();
			}
		} else {
			for (int i = 0; i < this.cols.length; i++) {
				sb.append(new StringBuilder("%").append(i + 1) // String.format()'s
																// parm inx
																// starts for 1
						.append("$-").append(cols[i].getRptDispLen()).append("s").append(colSeparator).toString());
				colhdr[i] = cols[i].getRptColHeader();
			}
		}
		sb.setLength(sb.length() - colSeparator.length());
		return String.format(sb.toString(), (Object[]) colhdr);

	}
	
	public static ReportMeta buildAndGet(ResultSetMetaData rsmd, HashMap<String, ColumnMeta> overrides,
			String colSeparator, boolean encloseStr, boolean trimStrings, Properties JDBCProperties)
					throws SQLException, IOException, PropertyVetoException {

		int colCnt = rsmd.getColumnCount();

		HashMap<String, ColumnMeta> colMetaMap = DBUtils.getReportHDR(JDBCProperties, rsmd);
		ColumnMeta[] colMetaArr = new ColumnMeta[colCnt];

		for (int i = 1, j = 0; i <= colCnt; i++, j++) {
			String SqlName = rsmd.getColumnLabel(i);
			if (overrides != null &&
				overrides.containsKey(SqlName)) {
				colMetaArr[j] = updColumnMeta(overrides.get(SqlName), rsmd.getColumnTypeName(i), encloseStr,
						trimStrings);
			} else if (colMetaMap.containsKey((rsmd.getColumnLabel(i)))) {
				colMetaArr[j] = updColumnMeta(colMetaMap.get(SqlName), rsmd.getColumnTypeName(i), encloseStr,
						trimStrings);
			} else {
				StringBuilder colFmt = new StringBuilder();
				String colHeader = new StringBuilder(rsmd.getColumnLabel(i)).toString();
				int dispLen = 10;
			
				if (rsmd.getColumnTypeName(i).equals("TIMESTAMP")) {
					colFmt.append("%tm/%td/%tY %tr %tZ");
					dispLen = 26;					
				}else if (rsmd.getColumnTypeName(i).equals("TIME")) {
					colFmt.append("%tr %tZ");
					dispLen = 13;
				}else if (rsmd.getColumnTypeName(i).equals("DATE")) {
					colFmt.append("%tm/%td/%tY");
					dispLen = 10;
				}else if (rsmd.getColumnTypeName(i).equals("CHAR") || rsmd.getColumnTypeName(i).equals("VARCHAR2")) {
					if (trimStrings) {
						colFmt.append(oneCharStr);
						dispLen = 1;
					} else {
						dispLen = rsmd.getColumnLabel(i).length() > rsmd.getColumnDisplaySize(i)
								? rsmd.getColumnLabel(i).length() : rsmd.getColumnDisplaySize(i);
						if (encloseStr)
							colFmt.append("\"%-" + dispLen + "s\"");
						else
							colFmt.append("%-" + dispLen + "s");
					}
				}else  {
					if (trimStrings) {
						if (encloseStr) {
							colFmt.append("\"%1s\"");
							dispLen = 3;
						} else {
							colFmt.append("%1s");
							dispLen = 1;
						} 
					} else {
						dispLen = rsmd.getColumnLabel(i).length() > rsmd.getColumnDisplaySize(i)
								? rsmd.getColumnLabel(i).length() : rsmd.getColumnDisplaySize(i);
						if (encloseStr)
							colFmt.append("\"%-" + dispLen + "s\"");
						else
							colFmt.append("%-" + dispLen + "s");
					}
				}

				colFmt.trimToSize();
//				String fmtPos = new StringBuilder("%").append(i).append("$").toString();
				String fmtPos = new StringBuilder("%1$").toString();
				ColumnMeta cm = new ColumnMeta(SqlName, rsmd.getColumnTypeName(i), colHeader,
						colFmt.toString().replace("%", fmtPos), dispLen);
				
 				cm.setRptDispLen(dispLen);
				cm.setSqlType(rsmd.getColumnTypeName(i));
				colMetaArr[j] = cm;
			}
		}

		final ReportMeta rm = new ReportMeta(colMetaArr, colSeparator, trimStrings);
		return rm;
	}

	
	
	
	
	private static ColumnMeta updColumnMeta(ColumnMeta cm, String sqlType, boolean encloseStr, boolean trimStrings) throws SQLException {

		cm.setRptColHeader(new StringBuilder(cm.getRptColHeader().trim()).toString());
		cm.setSqlType(sqlType);
		String fmtPos = new StringBuilder("%1$").toString();
		if (encloseStr && cm.getRptFormat().charAt(cm.getRptFormat().length() - 1) == 's') {
			if (trimStrings) {
				cm.setRptFormat(oneCharStr.replace("%", fmtPos));
				cm.setRptDispLen(1);
			} else {
				StringBuilder colFmt = new StringBuilder("\"");
				colFmt.append(cm.getRptFormat());
				colFmt.append('"');
				cm.setRptFormat(colFmt.toString().replace("%", fmtPos));
				cm.setRptDispLen(DBUtils.getLength(colFmt.toString(), sqlType));
			}
		} else {
		    cm.setRptFormat(cm.getRptFormat().replace("%", fmtPos));	
			cm.setRptDispLen(DBUtils.getLength(cm.getRptFormat(), sqlType));
		}

		return cm;
	}

	public int getColCount() {
		return this.cols.length;
	}
}
