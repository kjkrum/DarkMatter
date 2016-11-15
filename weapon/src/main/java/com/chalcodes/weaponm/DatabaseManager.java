package com.chalcodes.weaponm;

import com.chalcodes.event.EventBus;
import com.chalcodes.weaponm.entity.Game;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import javax.annotation.Nonnull;
import javax.swing.Action;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
class DatabaseManager {
	private final Session mSession;

	DatabaseManager(@Nonnull final File dataDir, @Nonnull final EventBus<Game> gameBus) throws IOException {
		final File ogmProperties = new File(dataDir, "ogm.properties");
		if(!ogmProperties.exists()) {
			Neo4jOgmUtil.writeDefaultConfig(ogmProperties);
		}
		final Configuration config = Neo4jOgmUtil.loadConfig(ogmProperties);
		final SessionFactory factory = new SessionFactory(config, "com.chalcodes.weaponm.entity");
		mSession = factory.openSession();
	}

	List<Action> getActions() {
		return null; // TODO
	}
}
