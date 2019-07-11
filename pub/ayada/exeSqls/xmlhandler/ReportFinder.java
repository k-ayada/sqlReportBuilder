package pub.ayada.exeSqls.xmlhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.bind.*;
import javax.xml.stream.*;
import javax.xml.transform.stream.StreamSource;

import pub.ayada.exeSqls.xmlhandler.jaxb.Report;
import pub.ayada.exeSqls.xmlhandler.jaxb.SqlXml;

public class ReportFinder {

	public static HashMap<String, JAXBElement<SqlXml>> getSqlXmlJaxbEleMap(
			String inxXmlPath, ArrayList<String> rptIds)
			throws XMLStreamException, JAXBException {

		HashMap<String, JAXBElement<SqlXml>> res = new HashMap<String, JAXBElement<SqlXml>>(
				rptIds.size());

		XMLInputFactory xif = XMLInputFactory.newFactory();
		StreamSource xml = new StreamSource(inxXmlPath);
		for (String rptId : rptIds) {

			try {
				XMLStreamReader xsr = xif.createXMLStreamReader(xml);
				while (true) {
					if ((xsr.isStartElement()
							&& xsr.getLocalName().equals("report") && xsr
							.getAttributeValue(0).equals(rptId))) {
						JAXBContext jc = JAXBContext.newInstance(Report.class);
						Unmarshaller um = jc.createUnmarshaller();
						String sqlPath = um.unmarshal(xsr, Report.class)
								.getValue().getsqlpath();

						jc = JAXBContext.newInstance(SqlXml.class);
						um = jc.createUnmarshaller();
						JAXBElement<SqlXml> resp = um.unmarshal(
								new StreamSource(sqlPath), SqlXml.class);
						res.put(rptId, resp);
						break;
					}
					xsr.next();
				}
			} catch (java.util.NoSuchElementException e) {
				e.printStackTrace();
				throw new RuntimeException("No SQLs found for report ID '"
						+ rptId + "'");
			}
		}
		return res;
	}

	public static HashMap<String, JAXBElement<Report>> getSqlXmlDtls(
			String inxXmlPath, ArrayList<String> rptIds)
			throws XMLStreamException, JAXBException, NoSuchElementException {

		HashMap<String, JAXBElement<Report>> res = new HashMap<String, JAXBElement<Report>>(
				rptIds.size());

		XMLInputFactory xif = XMLInputFactory.newFactory();
		StreamSource xml = new StreamSource(inxXmlPath);

		for (String rptId : rptIds) {
			try {
				XMLStreamReader xsr = xif.createXMLStreamReader(xml);
				while (true) {
					if ((xsr.isStartElement()
							&& xsr.getLocalName().equals("report") && xsr
							.getAttributeValue(0).equals(rptId))) {
						JAXBContext jc = JAXBContext.newInstance(Report.class);
						Unmarshaller um = jc.createUnmarshaller();
						res.put(rptId, um.unmarshal(xsr, Report.class));
						break;
					}
					xsr.next();
				}
			} catch (java.util.NoSuchElementException e) {
				throw new RuntimeException(
						" Failed to get the details for the report ID :"
								+ rptId);
			}
		}
		return res;
	}

	public static JAXBElement<SqlXml> getSqlXmlJaxbEle(String inxXmlPath,
			String rptId) throws XMLStreamException, JAXBException {

		XMLInputFactory xif = XMLInputFactory.newFactory();
		StreamSource xml = new StreamSource(inxXmlPath);

		try {
			XMLStreamReader xsr = xif.createXMLStreamReader(xml);
			while (true) {
				if ((xsr.isStartElement()
						&& xsr.getLocalName().equals("report") && xsr
						.getAttributeValue(0).equals(rptId))) {
					JAXBContext jc = JAXBContext.newInstance(Report.class);
					Unmarshaller um = jc.createUnmarshaller();
					String sqlPath = um.unmarshal(xsr, Report.class).getValue()
							.getsqlpath();
					return um
							.unmarshal(new StreamSource(sqlPath), SqlXml.class);
				}
				xsr.next();
			}
		} catch (java.util.NoSuchElementException e) {
			e.printStackTrace();
			throw new RuntimeException("No SQLs found for report ID '" + rptId
					+ "'");
		}
	}

	public static String getSqlXmlPath(String inxXmlPath, String rptId)
			throws XMLStreamException, JAXBException {

		XMLInputFactory xif = XMLInputFactory.newFactory();
		StreamSource xml = new StreamSource(inxXmlPath);

		try {
			XMLStreamReader xsr = xif.createXMLStreamReader(xml);
			while (true) {
				if ((xsr.isStartElement()
						&& xsr.getLocalName().equals("report") && xsr
						.getAttributeValue(0).equals(rptId))) {
					JAXBContext jc = JAXBContext.newInstance(Report.class);
					Unmarshaller um = jc.createUnmarshaller();
					return um.unmarshal(xsr, Report.class).getValue()
							.getsqlpath();
				}
				xsr.next();
			}
		} catch (java.util.NoSuchElementException e) {
			e.printStackTrace();
			throw new RuntimeException("No SQLs found for report ID '" + rptId
					+ "'");
		}
	}

	public static void main(String[] args) throws XMLStreamException,
			JAXBException {
		String inxXml = "./sqlInx.xml";
		ArrayList<String> ids = new ArrayList<String>(1);
		ids.add("indic_02");
		HashMap<String, JAXBElement<SqlXml>> res = getSqlXmlJaxbEleMap(inxXml,
				ids);

		for (Iterator<String> it = res.keySet().iterator(); it.hasNext();) {
			SqlXml sqlXml = res.get(it.next()).getValue();
			System.out.print("Query :" + sqlXml.getQuery());
		}
		System.out.println("done");
	}
}
