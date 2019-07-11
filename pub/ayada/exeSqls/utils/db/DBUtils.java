package pub.ayada.exeSqls.utils.db;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

public class DBUtils {

	/**
	 * Generates the Prepared SQL statement object form the SQL string.
	 * 
	 * @param SQL
	 *            SqlString (String)
	 * @param DBConn
	 *            Database Connection (Connection)
	 * @return prepared statement (PreparedStatement)
	 * 
	 * @throws SQLException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	public static PreparedStatement createStetement(String SQL,
			Connection DBConn) throws SQLException, IOException,
			PropertyVetoException {

		PreparedStatement pStmt = DBConn.prepareStatement(SQL,
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		// pStmt.closeOnCompletion();
		return pStmt;
	}

	/**
	 * Generates the Prepared SQL statement object form the SQL string.
	 * 
	 * @param SQL
	 *            SqlString (String)
	 * @param DBConn
	 *            Database Connection (Connection)
	 * @return prepared statement (PreparedStatement)
	 * 
	 * @throws SQLException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @throws RuntimeException
	 */
	public static PreparedStatement createStetement(String SQL,
			Properties JDBCProps) throws SQLException, IOException,
			PropertyVetoException {

		PreparedStatement pStmt = DBConnPool.getConnFrmPool(JDBCProps)
				.prepareStatement(SQL, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
		// pStmt.closeOnCompletion();
		return pStmt;
	}

	public static HashMap<String, ColumnMeta> getReportHDR(Properties pf,
			ResultSetMetaData rsmd) throws SQLException, IOException,
			PropertyVetoException {

		int columnCount = rsmd.getColumnCount();

		StringBuilder sql = new StringBuilder(
				"select COL_NAME, RPT_HDR, RPT_FMT from conv_master_col_xref where 1 = 1 and (");
		int frmDB = 0;
		for (int i = 1; i <= columnCount; i++) {
			frmDB++;
			sql.append("COL_NAME = '");
			// System.out.println(rsmd.getColumnName(i) + " : " +
			// rsmd.getColumnTypeName(i) + " : " + rsmd.getColumnLabel(i) +
			// " : " + rsmd.getColumnClassName(i)+ " : " );
			sql.append(rsmd.getColumnLabel(i));
			sql.append("' or ");
		}
		sql.setLength(sql.length() - 4);
		sql.append(") ");

		// System.out.println("SQL : \n" + sql.toString());

		HashMap<String, ColumnMeta> cols = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		if (frmDB > 0) {
			conn = DBConnPool.getConnFrmPool(pf);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			cols = new HashMap<String, ColumnMeta>(frmDB);
			while (rs.next()) {
				cols.put(
						rs.getString("COL_NAME"),
						new ColumnMeta(rs.getString("COL_NAME"), (String) null, // Assume
																				// null,
																				// the
																				// ReportMeta
																				// will
																				// populate
																				// it
																				// based
																				// on
																				// the
																				// ResultSet
																				// Meta
								rs.getString("RPT_HDR"), rs
										.getString("RPT_FMT"), 1));
			}
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		return cols;
	}

	public static int getLength(String format, String columnTypeName)
			throws SQLException {
		//format = format.replace("%", "%1$");
		String col = columnTypeName.substring(0, 3);
		try {
			if (col.equals("TIM") || col.equals("DAT")) {
				return String.format(format, new java.sql.Timestamp(0))
						.length();
			} else if (col.equals("NUM") || col.equals("DEC")) {
				return String.format(format, new java.math.BigDecimal(0))
						.length();
			} else if (col.equals("VAR") || col.equals("CHA")
					|| col.equals("STR")) {
				return String.format(format, " ").length();
			} else if (col.equals("INT") || col.equals("SMA")
					|| col.equals("BIN") || col.equals("POS")
					|| col.equals("SIG") || col.equals("NAT")
					|| col.equals("PLS")) {
				return String.format(format, 0).length();
			} else if (col.equals("DOU") || col.equals("FLO")) {
				return String.format(format, 0.0).length();
			} else if (col.equals("REA")) {
				return String.format(format, 0.0f).length();
			} else if (col.equals("BOO") || col.equals("")) {
				return 4;
			} else {
				throw new SQLException("Can't format the SQL datatype: (" + col
						+ ") " + columnTypeName + " format :" + format);
			}

			/*
			 * Oracle data types currently not handled.... case "NCH": //NCHAR
			 * case "NVA": //NVARCHAR return String.format(format, new
			 * oracle.sql.NString )).length(); case "ROW": //ROWID, case "URO":
			 * //UROWID return String.format(format, new
			 * oracle.sql.ROWID()).length(); case "INT": //INTERVAL YEAR TO
			 * MONTH, INTERVAL DAY TO SECOND -- conflict
			 */
		} catch (Exception e) {
			System.err.println("Failed to format the SQL datatype: (" + col
					+ ") " + columnTypeName + " format :" + format);
			throw new SQLException(e);
		}

	}
}
