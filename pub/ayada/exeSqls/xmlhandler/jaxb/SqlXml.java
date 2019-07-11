package pub.ayada.exeSqls.xmlhandler.jaxb;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
 <sql>
 <query>..</query>
 <colformat>..</colformat>
 <varlist>..</varlist>	
 </sql>
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = { "title", "query", "colformat", "varlist" })
@XmlRootElement(name = "sql")
public class SqlXml {
	@XmlElement(name = "title")
	String title;
	@XmlElement(name = "query")
	String query;
	@XmlElement(name = "colformat")
	protected ColumnFormat colformat = new ColumnFormat();
	@XmlElement(name = "varlist")
	protected VarList varlist = new VarList();

	public SqlXml() {
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ColumnFormat getColFormat() {
		return colformat;
	}

	public void setColFormat(ColumnFormat ColFormat) {
		this.colformat = ColFormat;
	}

	public VarList getVarList() {
		return varlist;
	}

	public void setVarList(VarList Varlist) {
		this.varlist = Varlist;
	}

	public HashMap<String, String[]> getRptColFormats() {
		return this.colformat.getColFmtMap();
	}

	public String updateVarsNgetQuery() {

		String query = getQuery();

		return query;

	}
}
