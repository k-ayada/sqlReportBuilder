package pub.ayada.exeSqls.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import pub.ayada.exeSqls.utils.file.PropertyFileHandler;



/* Pass the below JVM args to enable the debugging
-Doracle.jdbc.Trace=true
-Djava.util.logging.config.file=./JDBC_Debug.properties

*/
public class DBConn {

	/**
	 * Returns the Database Connection object based on the values specified in
	 * the propertyHandler (facade for Java property file object -
	 * {@link com.rps.utils.PropertyFileHandler})
	 * 
	 * <pre>
	 * <p> Key list used:
	 *    <tt>JDBC_CLASS</tt> - JDBC class name to be used.
	 *    <tt>JDBC_URL</tt>   - JDBC URL to connect to the Database.
	 *    <tt>JDBC_UID</tt>   - Database User ID.
	 *    <tt>JDBC_PWD</tt>   - Database password.
	 * </p>
	 * </pre>
	 * 
	 * @param Property
	 *            file hander object {@link com.rps.utils.PropertyFileHandler}
	 * @return Database Connection object
	 * @throws SQLException
	 * @throws RuntimeException
	 * @throws MalformedObjectNameException
	 * @throws ReflectionException
	 * @throws MBeanException
	 * @throws InstanceNotFoundException
	 * @throws AttributeNotFoundException
	 * @throws InvalidAttributeValueException
	 */
	public static Connection getDBConnection(PropertyFileHandler propfileHandler)
			throws SQLException, RuntimeException, MalformedObjectNameException, AttributeNotFoundException,
			InstanceNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException {
		try {
			Class.forName(propfileHandler.getProperty("JDBC_CLASS"));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(
					"Failed to laod JDBC driver class :" + propfileHandler.getProperty("JDBC_CLASS"));
		}

		Connection oraConn = enableLogging(DriverManager.getConnection(propfileHandler.getProperty("JDBC_URL"),
				propfileHandler.getProperty("JDBC_UID"), propfileHandler.getProperty("JDBC_PWD")));

		if (oraConn != null)
			return oraConn;
		else
			return null;
	}

	private static Connection enableLogging(Connection oraConn)
			throws MalformedObjectNameException, AttributeNotFoundException, InstanceNotFoundException, MBeanException,
			ReflectionException, InvalidAttributeValueException {
		// compute the ObjectName
		String loader = Thread.currentThread().getContextClassLoader().toString();
		loader = loader.replaceAll("[,=:\"]+", "");
		javax.management.ObjectName name = new javax.management.ObjectName(
				"com.oracle.jdbc:type=diagnosability,name=" + loader);

		// get the MBean server
		javax.management.MBeanServer mbs = java.lang.management.ManagementFactory.getPlatformMBeanServer();

		// find out if logging is enabled or not
		System.out.println("LoggingEnabled = " + mbs.getAttribute(name, "LoggingEnabled"));

		// enable logging
		mbs.setAttribute(name, new javax.management.Attribute("LoggingEnabled", true));

		// disable logging
		// mbs.setAttribute(name, new
		// javax.management.Attribute("LoggingEnabled", false));

		// find out if logging is enabled or not
		System.out.println("LoggingEnabled = " + mbs.getAttribute(name, "LoggingEnabled"));
		
		return oraConn;

	}

	public static Connection getDBConnection(HashMap<String, String> connPropList)
			throws SQLException, RuntimeException {
		try {
			Class.forName(popertyVal(connPropList, "JDBC_CLASS"));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to laod JDBC driver class :" + connPropList.get("JDBC_CLASS"));
		}
		Connection oraConn = DriverManager.getConnection(popertyVal(connPropList, "JDBC_URL"),
				popertyVal(connPropList, "JDBC_UID"), popertyVal(connPropList, "JDBC_PWD"));
		if (oraConn != null)
			return oraConn;
		else
			return null;

	}

	private static String popertyVal(HashMap<String, String> connPropList, String key) {
		try {
			return connPropList.get(key);
		} catch (NullPointerException e) {
			throw new RuntimeException("Property not found:JDBC_CLASS");
		}
	}

}
