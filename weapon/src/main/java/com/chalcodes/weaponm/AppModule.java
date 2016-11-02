package com.chalcodes.weaponm;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.station.CScreenDockStationWindowClosingStrategy;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.screen.window.DefaultScreenDockWindowFactory;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;
import dagger.Module;
import dagger.Provides;

import javax.annotation.Nonnull;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.io.File;

/**
 * Makes stuff.
 *
 * @author Kevin Krumwiede
 */
@Module
class AppModule {
	@Provides
	@Singleton
	@Nonnull
	@Named("data_dir")
	File dataDir() {
		return AppData.findDataDir();
	}

	@Provides
	@Singleton
	@Named("layout_file")
	File layoutFile(@Named("data_dir") final File dataDir) {
		return new File(dataDir, "layout.bin");
	}

	@Provides
	@Singleton
	@Nonnull
	JFrame rootWindow() {
		// TODO icon
		final JFrame mFrame = new JFrame("Weapon M");
		mFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mFrame.setSize(800, 600);
		return mFrame;
	}
	
	@Provides
	@Singleton
	@Nonnull
	CControl dockControl(@Nonnull final JFrame rootWindow) {
		final CControl dock = new CControl(rootWindow);
		dock.setTheme(ThemeMap.KEY_ECLIPSE_THEME);
		/* Set a window factory that puts externalized dockables in floating JFrames. */
		final DefaultScreenDockWindowFactory windowFactory = new DefaultScreenDockWindowFactory();
		windowFactory.setKind(DefaultScreenDockWindowFactory.Kind.FRAME);
		windowFactory.setUndecorated(false);
		dock.putProperty(ScreenDockStation.WINDOW_FACTORY, windowFactory);
		/* Set a window closing strategy that allows floating JFrames to be closed. */
		dock.putProperty(ScreenDockStation.WINDOW_CLOSING_STRATEGY, new CScreenDockStationWindowClosingStrategy());
		return dock;
	}

	@Provides
	@Singleton
	@Nonnull
	ApplicationResourceManager dockResources(@Nonnull final CControl dock) {
		return dock.getResources();
	}

	// TODO more provides methods

//	@Provides
//	@Singleton
//	Executor uiExecutor() {
//		return new Executor() {
//			@Override
//			public void execute(@Nonnull final Runnable command) {
//				SwingUtilities.invokeLater(command);
//			}
//		};
//	}

}
