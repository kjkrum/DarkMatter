package com.chalcodes.weaponm;

import org.neo4j.ogm.config.Configuration;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.Properties;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
class Neo4jOgmUtil {
	Neo4jOgmUtil() {}

	/**
	 * Writes the default configuration to the specified file.
	 *
	 * @param ogmProperties the file to write
	 * @throws IOException if the file cannot be written
	 */
	static void writeDefaultConfig(@Nonnull final File ogmProperties) throws IOException {
		try(final PrintWriter file = new PrintWriter(new FileWriter(ogmProperties))) {
			file.println("driver=org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
			file.println("URI=" + new File(ogmProperties.getParentFile(), "data").toURI());
		}
	}

	/**
	 * Loads a {@link Configuration} from an {@code ogm.properties} file.
	 *
	 * @param ogmProperties the properties file
	 * @return the configuration
	 * @throws IOException if the file does not exist or cannot be read
	 */
	static Configuration loadConfig(@Nonnull final File ogmProperties) throws IOException {
		final Properties props = new Properties();
		try (final InputStream in = new FileInputStream(ogmProperties)) {
			props.load(in);
		}
		return createConfig(props);
	}

	/**
	 * Creates a {@link Configuration} from a {@link Properties}.
	 *
	 * @param properties the properties
	 * @return the configuration
	 */
	private static Configuration createConfig(@Nonnull final Properties properties) {
		final Configuration config = new Configuration();
		for(final String key : properties.stringPropertyNames()) {
			config.set(key, properties.getProperty(key));
		}
		return config;
	}
}
