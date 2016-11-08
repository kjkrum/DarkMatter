package com.chalcodes.weaponm;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.station.CScreenDockStationWindowClosingStrategy;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.screen.window.DefaultScreenDockWindowFactory;
import dagger.Module;
import dagger.Provides;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;

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
		return new File(dataDir, "ogm.properties");
	}

	@Provides
	@Nonnull
	@Singleton
	Configuration ogmConfig(@Named(OGM_PROPERTIES_FILE) @Nonnull final File ogmProperties) {
		try {
			if(!ogmProperties.exists()) {
				Neo4jOgmUtil.writeDefaultConfig(ogmProperties);
			}
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
	@Singleton
	Image icon() {
		final URL url = getClass().getResource("/icon.png");
		final ImageIcon icon = new ImageIcon(url);
		return icon.getImage();
	}

	@Provides
	@Nonnull
	@Singleton
	JFrame mainWindow(@Nonnull final Image icon) {
		final JFrame frame = new JFrame("Weapon M");
		frame.setLayout(new BorderLayout());
		frame.setIconImage(icon);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setSize(800, 600);
		return frame;
	}
	
	@Provides
	@Nonnull
	@Singleton
	CControl dockControl(@Nonnull final JFrame mainWindow) {
		final CControl control = new CControl(mainWindow);
		control.setTheme(ThemeMap.KEY_ECLIPSE_THEME);
		/* Set a window factory that puts externalized dockables in floating JFrames. */
		final DefaultScreenDockWindowFactory windowFactory = new DefaultScreenDockWindowFactory();
		windowFactory.setKind(DefaultScreenDockWindowFactory.Kind.FRAME);
		windowFactory.setUndecorated(false);
		control.putProperty(ScreenDockStation.WINDOW_FACTORY, windowFactory);
		/* Set a window closing strategy that allows floating JFrames to be closed. */
		control.putProperty(ScreenDockStation.WINDOW_CLOSING_STRATEGY, new CScreenDockStationWindowClosingStrategy());
		return control;
	}

	private static final String LAYOUT_FILE = "LAYOUT_FILE";

	@Provides
	@Nonnull
	@Singleton
	@Named(LAYOUT_FILE)
	File layoutFile(@Named(DATA_DIR) @Nonnull final File dataDir) {
		return new File(dataDir, "layout.bin");
	}

	@Provides
	@Nonnull
	@Singleton
	WeaponM weapon(@Nonnull final JFrame mainWindow,
	               @Nonnull final CControl dockControl,
	               @Named(LAYOUT_FILE) @Nonnull final File layoutFile,
	               @Nonnull final Session session) {
		return new WeaponM(mainWindow, dockControl, layoutFile, session);
	}
}
