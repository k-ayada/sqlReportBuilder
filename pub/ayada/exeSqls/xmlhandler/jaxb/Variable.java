package pub.ayada.exeSqls.xmlhandler.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
<variable>
	<name>name</name>
	<datatype>datatype</datatype>
	<default>defaultVal</default>
</variable>
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = { "name","datatype", "defaultval" })
@XmlRootElement(name = "variable")
public class Variable {

	@XmlAttribute(name = "name")
	protected String name; 

	@XmlElement(name = "datatype")
	protected String datatype;

	@XmlElement(name = "defaultval")
	protected String defaultval;

	public Variable() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getDefaultval() {
		return defaultval;
	}

	public void setDefaultval(String defaultval) {
		this.defaultval = defaultval;
	}

}
