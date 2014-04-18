package edu.firstteam3189.vision2014.vision;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import team3189.library.Logger.Logger;

public class Utility {
	private static final Logger LOGGER = new Logger(Utility.class);
	private static final Properties properties = new Properties();

	static {
		loadProperties();
	}

	/**
	 * Gets a double from the properties
	 * 
	 * @param key
	 *            the key to look for
	 * @param defaultValue
	 *            the default value.
	 * @return
	 */
	public static double getDoubleFromProperty(String key, double defaultValue) {
		double value = defaultValue;
		try {
			// Get the value from the key and try to parse it into a double
			value = Double.parseDouble(properties.getProperty(key, "" + defaultValue));
		} catch (NumberFormatException e) {
			LOGGER.error("Invalid Double", e);
		}
		return value;
	}

	/**
	 * Gets an integer from the vision properties list by the indicated key, uses default value if non-existent or
	 * invalid
	 * 
	 * @param key
	 *            The key
	 * @param defaultValue
	 *            default value for the camera
	 * @return
	 */
	public static int getIntFromProperty(String key, int defaultValue) {
		int value = defaultValue;
		try {
			// Get the value from the key and try to parse it into an int...
			LOGGER.debug(key);
			value = Integer.parseInt(properties.getProperty(key, "" + defaultValue));
		} catch (NumberFormatException e) {
			LOGGER.error("Invalid value", e);
		}
		return value;
	}

	public static Properties getProperties() {
		return properties;
	}

	public static boolean isInRange(float value, float target, float tolerance) {
		return value < target + tolerance && value > target - tolerance;
	}

	/**
	 * If min < max, then checks if value is within that range, if min > max, then checks if its not within that range
	 * 
	 * @param value
	 *            value of number
	 * @param min
	 *            Minimum
	 * @param max
	 *            Maximum
	 * @return
	 */
	public static boolean range(double value, double min, double max) {
		return value > min && value < max;
	}

	private static void loadProperties() {
		try {
			properties.load(new FileInputStream("config.ini"));
		} catch (FileNotFoundException e) {
			LOGGER.error("Properties not found...", e);
		} catch (IOException e) {
			LOGGER.error("Properties failed to load...", e);
		}
	}
}
