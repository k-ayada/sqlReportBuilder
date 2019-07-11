package pub.ayada.exeSqls.utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertyFileHandler {

	private Properties props = new Properties();
	private String propFl;

	/**
	 * Public constructor to build the custom facade object of the java object
	 * {@code java.util.Properties}
	 * 
	 * @param (Stirng)
	 *            Path of the .properties file
	 * @throws RuntimeException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public PropertyFileHandler(String path) throws RuntimeException, IOException {
		this.propFl = path;
		this.props = new Properties();
		try {
			this.props.load(new FileInputStream(this.propFl));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Not able to find the file : " + this.propFl);
		}

		// Load the properties from other files.
		Enumeration<String> keys = (Enumeration<String>) this.props.propertyNames();
		for (; keys.hasMoreElements();) {
			String value = getProperty(keys.nextElement());
			if (value == null)
				continue;
			if (value.toLowerCase().startsWith("nonproperties(")) {
				getPropertyFromNonPropertiesFile(value.substring(14, value.length() - 1));
			} else if (value.toLowerCase().startsWith("properties(")) {
				getPropertyFromPropertiesFile(value.substring(11, value.length() - 1));
			}
		}
		// Resolve the value cross references.
		keys = (Enumeration<String>) this.props.propertyNames();
		for (; keys.hasMoreElements();) {
			String key = keys.nextElement();
			String value = getProperty(key);
			if (value.startsWith("${") && value.endsWith("}")) {
				props.setProperty(key,
						this.props.getProperty(value.substring(2, value.length() - 1)).replaceAll("\"", ""));
			}

		}
	}

	/**
	 * Returns the properties object.
	 *
	 * @return (Properties) Properties object
	 */
	public Properties getProperties() {
		return this.props;
	}

	/**
	 * Reads the .properties file and adds the properties
	 *
	 * @param (String)
	 *            Properties file path
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void getPropertyFromPropertiesFile(String path) throws IOException {
		Properties Prop = new Properties();
		Prop.load(new FileInputStream(path));
		Enumeration<String> propertyNames = (Enumeration<String>) Prop.propertyNames();
		for (; propertyNames.hasMoreElements();) {
			String key = propertyNames.nextElement();
			String value = Prop.getProperty(key);
			this.props.put(key, value);
			if (value.toLowerCase().startsWith("nonproperties(")) {
				getPropertyFromNonPropertiesFile(value.substring(14, value.length() - 1));
			} else if (value.toLowerCase().startsWith("properties(")) {
				getPropertyFromPropertiesFile(value.substring(11, value.length() - 1));
			}
		}
	}

	/**
	 * Reads the non-Standard properties file and based on the split char splits
	 * the line to get the key value pair
	 *
	 * @param value
	 *            ( property file path and value key value splitter)
	 * @throws IOException
	 */
	private void getPropertyFromNonPropertiesFile(String value) throws IOException {
		String[] valueSplit = value.split(",");
		String commentChar = "'";
		String splitChar = ":";
		if (valueSplit.length == 3)
			commentChar = valueSplit[2].replaceAll("\"", "");
		if (valueSplit.length > 1)
			splitChar = valueSplit[1].replaceAll("\"", "");
		File propertiesFile = new File(valueSplit[0]);

		BufferedReader br = new BufferedReader(new FileReader(propertiesFile));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.startsWith(commentChar))
				continue;
			String[] data = line.split(splitChar);
			this.props.put(data[0], data[1]);
			if (data[1].toLowerCase().startsWith("nonproperties(")) {
				getPropertyFromNonPropertiesFile(value.substring(14, value.length() - 1));
			} else if (data[1].toLowerCase().startsWith("properties(")) {
				getPropertyFromPropertiesFile(data[1].substring(11, data[1].length() - 1));
			}
		}
		br.close();
	}

	/**
	 * Spools out the property key value pairs
	 * 
	 * @param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void reportPropertyData() {
		String Key = null;
		for (Enumeration<String> propertyNames = (Enumeration<String>) this.props.propertyNames(); propertyNames
				.hasMoreElements();) {
			Key = propertyNames.nextElement();
			System.out.println(Key + ":'" + getProperty(Key) + "'");
		}
		System.out.println("");
	}

	/**
	 * Returns the property value for the input key.
	 * 
	 * @param (Stirng)
	 *            Property Key
	 * @return (String) value. If key not found, null
	 */
	public String getProperty(String key) {
		try {
			String value = this.props.getProperty(key).trim().replaceAll("\"", "");
			if (value.startsWith("${") && value.endsWith("}")) {
				String newKey = value.substring(2, value.length() - 1);
				return this.props.getProperty(newKey).replaceAll("\"", "");
			}
			return this.props.getProperty(key).replaceAll("\"", "");
		} catch (Exception e) {
			return (String) null;
		}
	}

	/**
	 * Return the path of the property file
	 * 
	 * @return (String) Path of the property file
	 */
	public String getPropertyFilePath() {
		return this.propFl;
	}
}
