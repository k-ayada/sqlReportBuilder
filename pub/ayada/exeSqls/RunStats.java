package pub.ayada.exeSqls;

import pub.ayada.exeSqls.utils.Timer;

public class RunStats {

	private String rptID;
	private String rptName;
	private String rptPath;
	private String rptSQlPath;
	private long rptRecCount = 0;
	private Timer timer;
	private String logMsg;

	public RunStats(String ReportID) {
		this.rptID = ReportID;
		this.timer = new Timer();
		this.logMsg = "Report: %1$s " 
		            + "\n\t Name       : %2$s"
				    + "\n\t Row Count  : %3$09d" 
		            + "\n\t Elapse Time: %4$s"
				    + "\n\t SQL Path   : %5$s"
			     	+ "\n\t Location   : %6$s";
	}

	public String getRptID() {
		return rptID;
	}

	public void setRptID(String rptID) {
		this.rptID = rptID;
	}

	public String getRptPath() {
		return rptPath;
	}

	public void setRptPath(String rptPath) {
		this.rptPath = rptPath;
	}

	public long getRptRecCount() {
		return rptRecCount;
	}

	public void incrRptRecCount() {
		this.rptRecCount++;
	}

	public void startTimer() {
		this.timer.start();
	}

	public void endTimer() {
		this.timer.end();
	}

	public String getElapsedTime() {
		return this.timer.getELapsedTimeHHmmss();
	}

	public String getLogMsg() {
		return String.format(logMsg, this.rptID, this.rptName,
				this.rptRecCount, getElapsedTime(),this.rptSQlPath, this.rptPath);
	}

	public String getRptName() {
		return rptName;
	}

	public String getRptSQlPath() {
		return rptSQlPath;
	}

	public void setRptSQlPath(String rptSQlPath) {
		this.rptSQlPath = rptSQlPath;
	}

	public void setRptName(String rptName) {
		this.rptName = rptName;
	}

}
