package pub.ayada.exeSqls.jsons;

import java.util.ArrayList;

public class RequestJSON {

	GenericVars genVars;
	ArrayList<ReportJSON> reportList;

	// ArrayList<String> reportids;

	public RequestJSON(String userid, String dbRegion, String conv_id,
			int wave, String phase, String fileType, String separator,
			boolean trimStrings, boolean wordWrap, ArrayList<ReportJSON> reports) {
		super();
		this.genVars = new GenericVars(userid, dbRegion, fileType, separator,
				trimStrings, wordWrap);
		this.reportList = reports;
	}

	public GenericVars getGenVars() {
		return genVars;
	}

	public void setGenVars(GenericVars genVars) {
		this.genVars = genVars;
	}

	public ArrayList<ReportJSON> getReportList() {
		return reportList;
	}

	public void setReportList(ArrayList<ReportJSON> reportList) {
		this.reportList = reportList;
	}

	public ArrayList<String> getReportIds() {

		ArrayList<String> ids = new ArrayList<String>(reportList.size());
		for (ReportJSON rpt : this.reportList)
			ids.add(rpt.getId());
		return ids;
	}

	public ReportJSON getReport(String id) {
		for (ReportJSON rpt : this.reportList)
			if (id.equals(rpt.getId()))
				return rpt;
		return (ReportJSON) null;
	}

}
