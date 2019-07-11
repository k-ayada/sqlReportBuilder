package pub.ayada.exeSqls.jsons;

import java.util.HashMap;

public class ReportJSON {

	String id;
	HashMap<String, String> vars;

	public ReportJSON(String reportid, HashMap<String, String> vars) {
		super();
		this.id = reportid;
		this.vars = vars;
	}

	public String getId() {
		return id;
	}

	public void setId(String reportid) {
		this.id = reportid;
	}

	public HashMap<String, String> getVars() {
		return vars;
	}

	public void setVars(HashMap<String, String> vars) {
		this.vars = vars;
	}

}
