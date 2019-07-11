package pub.ayada.exeSqls.xmlhandler.jaxb;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
<reports>
	<report>..</report>
	<report>..</report>	
</reports>
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = { "report" })
@XmlRootElement(name = "reports")
public class SQLIndex {
	@XmlElement(name = "report")
	protected ArrayList<Report> report = new ArrayList<Report>();

	public SQLIndex() {
	}

	public ArrayList<Report> getRpt() {
		return report;
	}

	public void setRpt(ArrayList<Report> rpt) {
		this.report = rpt;
	}
}
