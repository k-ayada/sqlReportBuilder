package pub.ayada.exeSqls.xmlhandler.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
 <report>
 <id>id</id>
 <name>name</name>
 <sqlpath>sqlpath</sqlpath>
 </report>
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = { "id", "name", "category", "sqlpath" })
@XmlRootElement(name = "report")
public class Report {

	@XmlElement(name = "id")
	protected String id;
	
	@XmlElement(name = "name")
	protected String name;

	@XmlElement(name = "category")
	protected String category;

	@XmlElement(name = "sqlpath")
	protected String sqlpath;

	public Report() {
	}

	public String getid() {
		return id;
	}

	public void setid(String id) {
		this.id = id;
	}

	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	public String getcategory() {
		return category;
	}

	public void setcategory(String category) {
		this.category = category;
	}

	public String getsqlpath() {
		return sqlpath;
	}

	public void setsqlpath(String sqlpath) {
		this.sqlpath = sqlpath;
	}

}
