package com.chalcodes.weaponm;

import com.chalcodes.event.ClassBusFactory;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
class GameManager {
	private final File mOgmProperties;
	private final JMenu mMenu;

	GameManager(@Nonnull final ClassBusFactory eventBuses,
	            @Nonnull final File dataDir,
	            @Nonnull final JFrame mainWindow) throws IOException {
		mOgmProperties = new File(dataDir, "ogm.properties");
		mMenu = createMenu();
	}

	private final Action newAction = new AbstractAction("New\u2026") {
		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			// TODO create new settings and show dialog
		}
	};

	private final Action openAction = new AbstractAction("Open\u2026") {
		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			// TODO query games and show picker dialog
		}
	};

	private final Action settingsAction = new AbstractAction("Settings\u2026") {
		{
			putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
			setEnabled(false); // enabled when game is loaded
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			// TODO show settings for current game
		}
	};

	private JMenu createMenu() {
		final JMenu menu = new JMenu("Game");
		menu.setMnemonic('G');
		menu.add(newAction);
		menu.add(openAction);
		menu.addSeparator();
		menu.add(settingsAction);
		menu.setEnabled(false); // enabled when connected to database
		return menu;
	}

	JMenu getMenu() {
		return mMenu;
	}

	private Session mSession;

	void connect() throws IOException {
		if(mSession == null) {
			new ConnectWorker().execute();
		}
	}

	private class ConnectWorker extends SimpleSwingWorker<Session> {
		@Override
		protected Session doInBackground() throws Exception {
			if (!mOgmProperties.exists()) {
				Neo4jOgmUtil.writeDefaultConfig(mOgmProperties);
			}
			final Configuration config = Neo4jOgmUtil.loadConfig(mOgmProperties);
			final SessionFactory factory = new SessionFactory(config, "com.chalcodes.weaponm.entity");
			return factory.openSession();
		}

		@Override
		protected void onResult(final Session session) {
			mSession = session;
			mMenu.setEnabled(true);
		}

		@Override
		protected void onError(final Exception error) {
			// TODO display error dialog
		}
	}
}
