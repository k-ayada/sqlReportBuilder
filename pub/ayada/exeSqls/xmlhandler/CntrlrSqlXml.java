package pub.ayada.exeSqls.xmlhandler;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import pub.ayada.exeSqls.xmlhandler.jaxb.ColumnFormat;
import pub.ayada.exeSqls.xmlhandler.jaxb.SqlXml;
import pub.ayada.exeSqls.xmlhandler.jaxb.VarList;

public class CntrlrSqlXml {

	SqlXml sqlXml;

	File xmlFile;

	public CntrlrSqlXml(String path) throws JAXBException {

		this.xmlFile = new File(path);

		JAXBContext jc = JAXBContext.newInstance(SqlXml.class);

		Unmarshaller um = jc.createUnmarshaller();

		um.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

		this.sqlXml = (SqlXml) um.unmarshal(this.xmlFile);
	}

	public SqlXml getSqlXml() {
		return sqlXml;
	}

	public ColumnFormat getColFormat() {
		return sqlXml.getColFormat();
	}

	public VarList getRpt() {
		return sqlXml.getVarList();
	}
}
