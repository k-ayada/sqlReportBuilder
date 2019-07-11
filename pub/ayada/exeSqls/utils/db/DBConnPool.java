package pub.ayada.exeSqls.utils.db;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DBConnPool {
	private static DBConnPool datasource;
	private ComboPooledDataSource cpds;

	private DBConnPool(Properties pf) throws IOException, SQLException, PropertyVetoException {
		cpds = new ComboPooledDataSource();
		cpds.setDriverClass(pf.getProperty("JDBC_CLASS")); // "oracle.jdbc.driver.OracleDriver");
															// //loads the jdbc
															// driver
		cpds.setJdbcUrl(pf.getProperty("JDBC_URL")); // "jdbc:oracle:thin:@gp-devdb:1521/d_fnet.isis.gwl.com");
		cpds.setUser(pf.getProperty("JDBC_UID")); // "FIDARCNT_USER");
		cpds.setPassword(pf.getProperty("JDBC_PWD")); // "d0natello");

		// the settings below are optional -- c3p0 can work with defaults
		cpds.setMinPoolSize(Integer.parseInt(pf.getProperty("C3P0_MinPoolSize")));
		cpds.setInitialPoolSize(Integer.parseInt(pf.getProperty("C3P0_InitialPoolSize")));				
		cpds.setAcquireIncrement(Integer.parseInt(pf.getProperty("C3P0_AcquireIncrement")));
		cpds.setMaxPoolSize(Integer.parseInt(pf.getProperty("C3P0_MaxPoolSize")));
		cpds.setMaxStatements(Integer.parseInt(pf.getProperty("C3P0_MaxStatements")));
		cpds.setAutoCommitOnClose(Boolean.parseBoolean(pf.getProperty("C3P0_AutoCommitOnClose")));
		cpds.setCheckoutTimeout(Integer.parseInt(pf.getProperty("C3P0_CheckoutTimeout")));
	}

	public static Connection getConnFrmPool (Properties pf)
			throws IOException, SQLException, PropertyVetoException {
		if (datasource == null) {
			datasource = new DBConnPool(pf);
			return datasource.getConnection();
		} else {
			return datasource.getConnection();
		}
	}

	public Connection getConnection() throws SQLException {
		return this.cpds.getConnection();
	}

}
