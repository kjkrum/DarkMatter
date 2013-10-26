package com.chalcodes.weaponm;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides information about the build.  The data lives in a properties file
 * that is filtered by Maven.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class Build {
	private static final Logger log = LoggerFactory.getLogger(Build.class.getSimpleName());
	private static final ResourceBundle bundle = ResourceBundle.getBundle("com/chalcodes/weaponm/Build");
	
	public static String getVersion() {
		return getResource("VERSION");
	}
	
	public static String getTimestamp() {
		return getResource("TIMESTAMP");
	}
	
	private static String getResource(String key) {
		if(bundle.containsKey(key)) {
			return bundle.getString(key);
		}
		else {
			log.warn("missing resource for key \"{}\"", key);
			return "";
		}
	}
	
	private Build() { }
}
