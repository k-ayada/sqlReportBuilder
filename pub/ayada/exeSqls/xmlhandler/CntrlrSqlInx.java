package pub.ayada.exeSqls.xmlhandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import pub.ayada.exeSqls.xmlhandler.jaxb.Report;
import pub.ayada.exeSqls.xmlhandler.jaxb.SQLIndex;

public class CntrlrSqlInx {

	SQLIndex sqlInx;

	File xmlFile;

	public CntrlrSqlInx(String path) throws JAXBException, IOException {

		this.xmlFile = new File(path);

		JAXBContext jc = JAXBContext.newInstance(SQLIndex.class);

		Unmarshaller um = jc.createUnmarshaller();

		um.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

		this.sqlInx = (SQLIndex) um.unmarshal(this.xmlFile);

		System.out.println("XML Path        : " + this.xmlFile.getAbsolutePath()+ "\n");
		int i = 0;
		for (Report rpt : this.sqlInx.getRpt()) {
			System.out.print("\t"+i + ". Report ID : " + rpt.getid());
			System.out.print(". Report Name : " + rpt.getname());
			System.out.println(". Report SQL Path : " + rpt.getsqlpath());
            i++;
		}
	}

	
	public SQLIndex getsqlInx() {
		return sqlInx;
	}

	public ArrayList<Report> getRpt() {
		return sqlInx.getRpt();
	}

	public static void main(String[] args) {

		try {
			new CntrlrSqlInx("sqlInx.xml");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
