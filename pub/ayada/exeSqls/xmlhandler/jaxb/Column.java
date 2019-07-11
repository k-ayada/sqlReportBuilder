package pub.ayada.exeSqls.xmlhandler.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/*
<column>
	<namecolName</name>
	<format><![CDATA[ format ]]></format>
</column>
 */

@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "", propOrder = { "name", "reptcolhdr", "format" })
@XmlRootElement(name = "column")
public class Column {

	@XmlAttribute(name = "name")
	protected String name;
	
	@XmlElement(name = "reptcolhdr")
	protected String reptcolhdr;
	
	@XmlElement(name = "format")
	protected String format;

	public Column() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReptcolhdr() {
		return reptcolhdr;
	}

	public void setReptcolhdr(String reptcolhdr) {
		this.reptcolhdr = reptcolhdr;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
