package pub.ayada.exeSqls;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import pub.ayada.exeSqls.jsons.RequestJSON;
import pub.ayada.exeSqls.utils.file.PropertyFileHandler;
import pub.ayada.exeSqls.xmlhandler.ReportFinder;
import pub.ayada.exeSqls.xmlhandler.jaxb.Report;

public class ExeSqls {
	private Properties jdbcProps;
	private ThreadGroup processThreadGrp;
	private RequestJSON reqJSON;
	private ArrayList<RunStats> runStatList;
	static Logger L = LoggerFactory.getLogger(ExeSqls.class);

	/**
	 * Constructor for creating the ExeSqls object
	 * 
	 * @param RequestJSON
	 * 
	 *            Structure : </p> &nbs {"genVars": </p>{"userid": "krnydc",
	 *            </p> "dbRegion": "xe", </p> "fileType": "delimited", </p>
	 *            "separator": " ", </p> "trimStrings": true, </p> "wordwrap":
	 *            false}, </p>
	 *            <p>
	 *            "reports":[{"id": "indic_99",
	 *            </p>
	 *            "vars":[{"schema": "xe"}, </p> {"convid": "JPMRPS"}, </p>
	 *            {"wave": 2}, </p> {"phase": "T"}]}]} </p>
	 * @throws RuntimeException
	 * @throws IOException
	 */
	public ExeSqls(String RequestJSON) throws RuntimeException, IOException {
		super();

		// Load the JDBC properties.
		this.jdbcProps = new PropertyFileHandler("./Run.properties")
				.getProperties();

		// convert the JSON string to RequestJSON Object.
		this.reqJSON = new Gson().fromJson(RequestJSON, RequestJSON.class);

		// Log the Request string.
		L.info(new Gson().toJson(reqJSON));

		// Create a new thread group. THis will be used for controlling the
		// number of threads.
		this.processThreadGrp = new ThreadGroup("tg");
		// Set the priority to 5 - average.
		this.processThreadGrp.setMaxPriority(5);
	}

	/**
	 * Executes the Reports requested in the JSON string.
	 * 
	 * @throws XMLStreamException
	 * @throws JAXBException
	 * @throws IOException
	 * @throws SQLException
	 * @throws PropertyVetoException
	 * @throws InterruptedException
	 */
	public void execute() throws XMLStreamException, JAXBException,
			IOException, SQLException, PropertyVetoException,
			InterruptedException {

		// For each request ID, build the Report Object containing the name
		// and the sql path.
		HashMap<String, JAXBElement<Report>> sqlXmlJaxbEleMap = ReportFinder
				.getSqlXmlDtls(this.jdbcProps.getProperty("RPT_SqlIndexXml"),
						this.reqJSON.getReportIds());

		this.runStatList = new ArrayList<RunStats>(sqlXmlJaxbEleMap.size());
		// Decide and update the report extension.
		String RptExt = this.reqJSON.getGenVars().getFileType().toLowerCase()
				.substring(0, 3);
		if (RptExt.equals("del")) {
			if (this.reqJSON.getGenVars().getSeparator().equals("\t"))
				this.reqJSON.getGenVars().setFileType(".txt");
			else
				this.reqJSON.getGenVars().setFileType(".csv");
		} else if (RptExt.equals("fix") || RptExt.equals("tex")) {
			this.reqJSON.getGenVars().setFileType(".txt");
			this.reqJSON.getGenVars().setEncloseStr(false);
		} else if (RptExt.equals("exc")) {
			this.reqJSON.getGenVars().setFileType(".csv");
		} else {
			throw new RuntimeException("Unknown Report file format '"
					+ this.reqJSON.getGenVars().getFileType()
					+ "'. Valid [del|delimited|fix|fixed|exc|excel]");
		}

		// iterate thru the hashmap and submit a child thread to produce the
		// report.
		for (Iterator<String> it = sqlXmlJaxbEleMap.keySet().iterator(); it
				.hasNext();) {
			// get the report id.
			RunStats runStats = new RunStats(it.next());
			runStatList.add(runStats);
			Report rpt =  sqlXmlJaxbEleMap.get(runStats.getRptID()).getValue();
			
			// Build the Report path			
			File dir = new File(this.jdbcProps.getProperty("RPT_RootDir")
					+ reqJSON.getReport(runStats.getRptID()).getVars().get("convcd") + "/" + "W" 
					+ reqJSON.getReport(runStats.getRptID()).getVars().get("wave")
					+ reqJSON.getReport(runStats.getRptID()).getVars().get("phase") + "/" 
					+ rpt.getcategory() + "/"+ runStats.getRptID());					
			
			if (!dir.exists())
				if (!dir.mkdirs())
					throw new RuntimeException("Failed to create the DIR :-"
						                      + dir.getAbsolutePath());		
			runStats.setRptPath(dir.getAbsolutePath() + "/"
					+ reqJSON.getGenVars().getUserid() + "_"  
					+ String.format("%1$tY-%1$tm-%1$td_%1$tH%1$tM%1$tS",
							        (Object) new Timestamp(new java.util.Date().getTime()))
					+ reqJSON.getGenVars().getFileType());
			// Create the SQLReporter object
			SQLReporter srThread = new SQLReporter(this.jdbcProps,
					this.reqJSON, rpt, runStats);
			// Create a new child thread in the thread group
			Thread t = new Thread(this.processThreadGrp, srThread);

			// Set the name of the child thread.
			t.setName(this.reqJSON.getGenVars().getUserid() + "_rpt:"
					+ runStats.getRptID());
			// start the thread.
			t.start();
			// if the thread count has reached the threshold, add the join to
			// complete all these threads.
			handleThreadCount(Integer.parseInt(this.jdbcProps
					.getProperty("RPT_MaxThreadCount")));
		}
		// Make sure that all the child thread's execution is completed.
		handleThreadCount(1);
				
		for(RunStats rs : this.runStatList) {
			L.info("\n"+rs.getLogMsg());
		}
	}

	/**
	 * Limits the thread count to a desired limit.
	 * 
	 * @param maxThreads
	 * @throws InterruptedException
	 */
	private void handleThreadCount(int maxThreads) throws InterruptedException {

		// get the count of active threads.
		int nAlloc = this.processThreadGrp.activeCount();
		// if the active count is same or more than max. Run the join on each
		// thread.
		if (nAlloc >= maxThreads) {
			Thread[] threads;
			int n = 0;
			do {
				nAlloc += 1;
				threads = new Thread[nAlloc];
				n = this.processThreadGrp.enumerate(threads);
			} while (n == nAlloc && n > 0);
			int i = -1;
			while (threads[++i] != null)
				if (threads[i].isAlive()) {
					threads[i].join();
				}
		}
	}

	public static void main(String[] args) throws XMLStreamException,
			JAXBException, IOException, SQLException, PropertyVetoException,
			InterruptedException, RuntimeException {

		String reqJSONStr = "{" + "\"genVars\": {" + "\"userid\": \"krnydc\","
				+ "\"dbRegion\": \"xe\"," + "\"fileType\": \"delimited\","
				+ "\"separator\": \"\t\"," + "\"trimStrings\": true,"
				+ "\"wordwrap\": false" + "}," + "\"reportList\": [" + "{"
				+ "\"id\": \"indic_02\"," + "\"vars\": {"
				+ "\"schema\": \"xe\"," + "\"convcd\": \"JPMRPS\","
				+ "\"wave\": 2," + "\"phase\": \"T\"" + "}" + "}," + "{"
				+ "\"id\": \"indic_099\"," + "\"vars\": {"
				+ "\"schema\": \"xe\"," + "\"convcd\": \"JPMRPS\","
				+ "\"wave\": 2," + "\"phase\": \"T\"" + "}" + "}" + "]" + "}";
		new ExeSqls(reqJSONStr).execute();
	}
}

/*
 * 
 * { "genVars": { "userid": "krnydc", "dbRegion": "xe", "fileType": "delimited",
 * "separator": " ", "trimStrings": true, "wordwrap": false }, "reports": [ {
 * "id": "indic_099", "vars": { "schema": "xe", "convid": "JPMRPS", "wave": 2,
 * "phase": "T" } }, { "id": "indic_099B", "vars": { "schema": "xe", "convid":
 * "JPMRPS", "wave": 2, "phase": "T" } } ] }
 */
