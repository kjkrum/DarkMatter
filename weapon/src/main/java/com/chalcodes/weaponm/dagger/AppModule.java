package com.chalcodes.weaponm.dagger;

import com.chalcodes.weaponm.Environment;
import com.chalcodes.weaponm.Neo4jOgm;
import com.chalcodes.weaponm.WeaponM;
import dagger.Module;
import dagger.Provides;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;

import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.net.URISyntaxException;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Dagger module.
 *
 * @author Kevin Krumwiede
 */
@Module
public class AppModule {
	@Qualifier
	@Retention(RUNTIME)
	private @interface NoInject {}

	@Provides
	@Singleton
	SessionFactory sessionFactory() {
		try {
			final File ogmProps = new File(Environment.getDataDir(), "ogm.properties");
			return Neo4jOgm.getSessionFactory(ogmProps,"com.chalcodes.weaponm.database");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// TODO moar
}
