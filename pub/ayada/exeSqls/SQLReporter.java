package pub.ayada.exeSqls;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pub.ayada.exeSqls.jsons.RequestJSON;
import pub.ayada.exeSqls.utils.db.ColumnMeta;
import pub.ayada.exeSqls.utils.db.DBConnPool;
import pub.ayada.exeSqls.utils.db.DBUtils;
import pub.ayada.exeSqls.utils.db.ReportMeta;
import pub.ayada.exeSqls.utils.file.FileUtils;
import pub.ayada.exeSqls.utils.string.StringUtils;
import pub.ayada.exeSqls.xmlhandler.jaxb.Report;
import pub.ayada.exeSqls.xmlhandler.jaxb.SqlXml;

public class SQLReporter implements Runnable {

	private PreparedStatement pStmt;
	private Writer writer;
	// private int recCnt = 0;
	private HashMap<String, ColumnMeta> colHdrXref;
	private Properties jdbcProps;
	private String separator = "\t";
	private Connection DBConn;
	private final ReportMeta rptMeta;
	private boolean wordwrap = false;
	private boolean encloseStr = true;
	private boolean trimStrings = true;
	private RunStats runStats;

	static Logger L = LoggerFactory.getLogger(SQLReporter.class);

	/**
	 * Constructor for the class SQLReporter
	 * 
	 * @param (java.util.Properties)RunProp
	 * @param (RequestJSON)reqJSON
	 * @param (pub.ayada.exeSqls.xmlhandler.jaxb.Report) rpt - Report Object
	 * @param (RunStats) RunStats
	 * @throws IOException
	 * @throws SQLException
	 * @throws PropertyVetoException
	 * @throws JAXBException
	 * @throws InterruptedException
	 */
	public SQLReporter(Properties RunProp, RequestJSON reqJSON, Report rpt,
			RunStats RunStats) throws IOException, SQLException,
			PropertyVetoException, JAXBException, InterruptedException {

		this.jdbcProps = RunProp;
		this.runStats = RunStats;
		this.wordwrap = reqJSON.getGenVars().isWordwrap();
		this.encloseStr = reqJSON.getGenVars().isEncloseStr();
		this.trimStrings = reqJSON.getGenVars().isTrimStrings();

		this.runStats.setRptSQlPath(new File(rpt.getsqlpath())
				.getAbsolutePath());
		this.runStats.setRptName(rpt.getname());
		// Load the SQL xml file
		SqlXml sqlXml = JAXBContext
				.newInstance(SqlXml.class)
				.createUnmarshaller()
				.unmarshal(new StreamSource(this.runStats.getRptSQlPath()),
						SqlXml.class).getValue();

		HashMap<String, String> vars = reqJSON.getReport(runStats.getRptID())
				.getVars();

		// Create the buffered writer handler
		this.writer = FileUtils.getBufferedWriter(this.runStats.getRptPath());

		// Build the SQL. Replace the variable $$schema with actual environment
		// value.
		String SQL = sqlXml.getQuery().trim()
				.replace("$$schema", this.jdbcProps.getProperty("JDBC_SCHEMA"));

		// Get the title form the SQL xml.
		String rptTile = sqlXml.getTitle().trim();

		// Loop thru the report variables and replace the variables in the SQL
		// and the title with the values.
		for (Iterator<String> it = vars.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			String val = vars.get(key);
			rptTile = rptTile.replace("$$" + key, val);
			SQL = SQL.replace("$$" + key, val);
		}
		// Write the title to the file.
		this.writer.write(rptTile + "\n");

		// Load the report column format options in the SQL xml
		HashMap<String, String[]> formatS = sqlXml.getColFormat()
				.getColFmtMap();
		this.colHdrXref = new HashMap<String, ColumnMeta>(formatS.size());

		for (Entry<String, String[]> it : formatS.entrySet()) {
			String key = it.getKey();
			String[] vals = it.getValue();
			ColumnMeta cm = new ColumnMeta();
			cm.setSqlName(key);
			cm.setRptColHeader(vals[0]);
			cm.setRptFormat(vals[1]);
			this.colHdrXref.put(key, cm);
		}
		// Get a connection form pool.
		this.DBConn = DBConnPool.getConnFrmPool(this.jdbcProps);

		// Create the prepared statement from the SQL.
		this.pStmt = DBUtils.createStetement(SQL, this.DBConn);

		// Update the report column format for the missing columns by using the
		// formats available in the query meta data.
		this.rptMeta = ReportMeta.buildAndGet(this.pStmt.getMetaData(),
				this.colHdrXref, this.separator, this.encloseStr,
				this.trimStrings, this.jdbcProps);

	}

	public void run() {
		this.runStats.startTimer();
			
		ResultSet rs = null;
		try {
			Thread.sleep(10000);
			writeHeader();
			// Fetch the data from DB
			this.pStmt.clearWarnings();
			this.pStmt.clearParameters();
			rs = this.pStmt.executeQuery();

			// Report the fetched data
			if (rs != null) {
				while (rs.next())
					writeRecord(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		// Close Result set, connection, statement and fileWriter
		finally {
			try {
				this.writer.write("\nRecord count : "
						+ String.format("%09d", this.runStats.getRptRecCount())
						+ "\n");
				if (rs != null)
					rs.close();
				if (this.pStmt != null)
					this.pStmt.close();
				if (this.DBConn != null)
					this.DBConn.close();
				if (this.writer != null)
					this.writer.close();
				this.runStats.endTimer();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

	}

	private void writeHeader() throws IOException, SQLException,
			PropertyVetoException {

		// L.info("Report template: " + this.rptMeta.getRptTemplate());
		// L.info("Report Header  : " + this.rptMeta.getColHdrLine());

		// System.out.print("\n\n"+this.rptMeta.getColHdrLine() + "\n");
		this.writer.write(this.rptMeta.getColHdrLine() + "\n");
	}

	private void writeRecord(ResultSet rs) throws SQLException, IOException {

		int colCount = this.rptMeta.getColCount();
		Object[] cols = new Object[colCount];
		String[] colsStr = new String[colCount];
		for (int i = 0; i < colCount; i++) {
			cols[i] = rs.getObject(i + 1);
			if (cols[i] instanceof oracle.sql.TIMESTAMP
					|| cols[i] instanceof oracle.sql.TIMESTAMPLTZ
					|| cols[i] instanceof oracle.sql.TIMESTAMPTZ
					|| cols[i] instanceof oracle.sql.TIMEZONETAB) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(((oracle.sql.TIMESTAMP) cols[i]).dateValue());
				cols[i] = cal;
			} else if (cols[i] instanceof oracle.sql.DATE) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(((oracle.sql.DATE) cols[i]).dateValue());
				cols[i] = cal;
			}
			if (cols[i] != null)
				colsStr[i] = String.format(this.rptMeta.getColMeta(i)
						.getRptFormat(), cols[i]);
			else
				colsStr[i] = this.rptMeta.getColMeta(i).getBlankCol();
		}
		if (!this.wordwrap) {
			String fmtdStr = StringUtils.joinArr(colsStr,
					this.rptMeta.getRptSeparator());
			// System.out.print(fmtdStr + "\n");
			this.writer.write(fmtdStr + "\n");
		} else {
			int maxdepth = 1;
			ArrayList<ArrayList<String>> lines = new ArrayList<ArrayList<String>>(
					cols.length);
			for (int i = 0; i < cols.length; i++) {
				ColumnMeta cm = this.rptMeta.getColMeta(i);
				ArrayList<String> resLines = StringUtils.splitByLength(
						colsStr[i], cm.getRptDispLen(), true, 'l');
				if (maxdepth < resLines.size())
					maxdepth = resLines.size();
				lines.add(resLines);
			}
			for (int i = 0; i < maxdepth; i++) {
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < lines.size(); j++) {
					ArrayList<String> col = lines.get(j);
					if (col.size() <= i) {
						sb.append(this.rptMeta.getColMeta(j).getBlankCol());
					} else {
						sb.append(col.get(i));
					}
					sb.append(this.rptMeta.getRptSeparator());
				}
				sb.setLength(sb.length()
						- this.rptMeta.getRptSeparator().length());
				System.out.print(sb.toString() + "\n");
				this.writer.write(sb.toString() + "\n");
			}
		}

		this.runStats.incrRptRecCount();
	}
}
/*
 * drop table conv_master_col_xref; CREATE TABLE conv_master_col_xref (COL_NAME
 * VARCHAR2(100 BYTE) NOT NULL, RPT_HDR VARCHAR2(20 BYTE) NOT NULL, RPT_FMT
 * VARCHAR2(30 BYTE), COL_DESC VARCHAR2(4000 BYTE) NOT NULL, AUD_USR VARCHAR2(50
 * BYTE)DEFAULT USER, AUD_TS TIMESTAMP DEFAULT SYSTIMESTAMP --CURRENT_TIMESTAMP
 * --> for client's time );
 * 
 * --insert into conv_master_col_xref(COL_NAME,RPT_HDR,RPT_FMT,COL_DESC)
 * --values('CLIENT_PLAN_NBR' , 'CLIENT' || chr(10)||'PLAN NUM' , '%06d',
 * 'Client''s plan number');
 * 
 * select * from conv_master_col_xref;
 */
