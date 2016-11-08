package com.chalcodes.weaponm;

import dagger.Module;
import dagger.Provides;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

/**
 * Dagger module.  Makes all the things.
 *
 * @author Kevin Krumwiede
 */
@Module
class AppModule {
	private static final String DATA_DIR = "DATA_DIR";

	@Provides
	@Nonnull
	@Singleton
	@Named(DATA_DIR)
	File dataDir() {
		try {
			return Environment.getDataDir();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final String OGM_PROPERTIES_FILE = "OGM_PROPERTIES_FILE";

	@Provides
	@Nonnull
	@Singleton
	@Named(OGM_PROPERTIES_FILE)
	File ogmPropertiesFile(@Named(DATA_DIR) @Nonnull final File dataDir) {
		final File ogmProperties = new File(dataDir, "ogm.properties");
		if(!ogmProperties.exists()) {
			try {
				Neo4jOgmUtil.writeDefaultConfig(ogmProperties);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return ogmProperties;
	}

	@Provides
	@Nonnull
	@Singleton
	Configuration ogmConfig(@Named(OGM_PROPERTIES_FILE) @Nonnull final File ogmProperties) {
		try {
			return Neo4jOgmUtil.loadConfig(ogmProperties);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	@Nonnull
	@Singleton
	Session ogmSession(@Nonnull final Configuration config) {
		final SessionFactory factory = new SessionFactory(config, "com.example.foo"); // TODO use real package name
		return factory.openSession();
	}

	@Provides
	@Nonnull
	@Singleton
	Ui ui(@Named(DATA_DIR) @Nonnull final File dataDir,
	      @Nonnull final Session session) {
		return new Ui(dataDir, session);
	}
}
