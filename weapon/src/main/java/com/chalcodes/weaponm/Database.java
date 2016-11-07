package com.chalcodes.weaponm;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.Properties;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
class Database {
	private final File mOgmPropertiesFile;
	private final Session mSession;

	Database(@Nonnull final File dataDir) {
		mOgmPropertiesFile = new File(dataDir, "ogm.properties").getAbsoluteFile();
		writeDefaultOgmProperties();
		final Configuration config = loadConfig();
		final SessionFactory sessionFactory =
				new SessionFactory(config, "org.neo4j.example.domain"); // TODO use real package name
		mSession = sessionFactory.openSession();
	}

	private void writeDefaultOgmProperties() {
		if(!mOgmPropertiesFile.exists()) {
			try {
				final PrintWriter file = new PrintWriter(new FileWriter(mOgmPropertiesFile));
				file.println("driver=org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
				file.println("URI=" + new File(mOgmPropertiesFile.getParentFile(), "data").toURI());
				file.close();
			} catch (IOException e) {
				throw new RuntimeException(e); // TODO ???
			}
		}
	}

	private Configuration loadConfig() {
		/* The Configuration constructor that takes a file name only looks for
		 * the file on the classpath.  To use an arbitrary File, we need to
		 * manually load it and copy each property into the Configuration. */
		final Properties props = new Properties();
		try (final InputStream in = new FileInputStream(mOgmPropertiesFile)) {
			props.load(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		final Configuration config = new Configuration();
		for(final String key : props.stringPropertyNames()) {
			config.set(key, props.getProperty(key));
		}
		return config;
	}

}
