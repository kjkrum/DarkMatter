package com.chalcodes.weaponm;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.station.CScreenDockStationWindowClosingStrategy;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.screen.window.DefaultScreenDockWindowFactory;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;
import org.neo4j.ogm.session.Session;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
class Ui {
	// TODO docking frames: minimize other dockables when one is maximized?
	// TODO docking frames: externalize in dock that can only contain one dockable?
	// TODO docking frames: better layout strategy?

	// TODO menu bar
	// TODO receiver for Dockables from plugins
	// maybe require DefaultSingleCDockables if we want to restrict features
	// or use multiple dockables if we want plugins to provide their unique ids
	// TODO track dockables from user plugins and remove when plugins are reloaded
	// distinguish them by their classloader?
	// TODO restore layout after plugins are loaded

	private final JFrame mMainWindow;
	private final CControl mDockControl;
	private final ApplicationResourceManager mDockResources;
	private final File mLayoutFile;

	// TODO create everything with Dagger?

	Ui(@Nonnull final File dataDir, @Nonnull final Session session) {
		mMainWindow = new JFrame();
		mDockControl = new CControl(mMainWindow);
		mDockResources = mDockControl.getResources();
		mLayoutFile = new File(dataDir, "layout.bin");
		setLookAndFeel();
		configureMainWindow();
		configureDockControl();
		loadLayout();
	}

	private void setLookAndFeel() {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					return;
				}
			}
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Log.w("error setting look and feel", e);
		}
	}

	private void configureMainWindow() {
		mMainWindow.setTitle("Weapon M");
		final URL url = getClass().getResource("/icon.png");
		final ImageIcon icon = new ImageIcon(url);
		mMainWindow.setIconImage(icon.getImage());
		mMainWindow.add(mDockControl.getContentArea());
		mMainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mMainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				confirmExit();
			}
		});
		mMainWindow.setSize(800, 600);
	}

	private void configureDockControl() {
		mDockControl.setTheme(ThemeMap.KEY_ECLIPSE_THEME);
		/* Set a window factory that puts externalized dockables in floating JFrames. */
		final DefaultScreenDockWindowFactory windowFactory = new DefaultScreenDockWindowFactory();
		windowFactory.setKind(DefaultScreenDockWindowFactory.Kind.FRAME);
		windowFactory.setUndecorated(false);
		mDockControl.putProperty(ScreenDockStation.WINDOW_FACTORY, windowFactory);
		/* Set a window closing strategy that allows floating JFrames to be closed. */
		mDockControl.putProperty(ScreenDockStation.WINDOW_CLOSING_STRATEGY, new CScreenDockStationWindowClosingStrategy());
	}

	private static final String LAYOUT_NAME = "default";

	private void loadLayout() {
		if(mLayoutFile.exists() && mLayoutFile.canRead()) {
			try {
				mDockResources.readFile(mLayoutFile);
				mDockControl.load(LAYOUT_NAME);
				/* If a dockable isn't in the layout file, it's made invisible. */
				final int count = mDockControl.getCDockableCount();
				for(int i = 0; i < count; ++i) {
					mDockControl.getCDockable(i).setVisible(true);
				}
			} catch (IOException | IllegalArgumentException e) {
				Log.w("error loading layout", e);
			}
		}
	}

	private void saveLayout() {
		try {
			mDockControl.save(LAYOUT_NAME);
			mDockResources.writeFile(mLayoutFile);
		} catch (IllegalArgumentException | IOException e) {
			Log.w("error saving layout", e);
		}
	}

	private void confirmExit() {
		if (JOptionPane.showOptionDialog(mMainWindow, "Exit Weapon M?", "Confirm exit",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
			exit();
		}
	}

	private void exit() {
		saveLayout();
		// TODO shut down other components
		System.exit(0);
	}

	void show() {
		mMainWindow.setVisible(true);
	}

//	private void addDummy(@Nonnull final String title) {
//		final JPanel panel = new JPanel();
//		panel.setSize(300, 200);
//		final DefaultSingleCDockable dockable = new DefaultSingleCDockable(title, title, panel);
//		dockable.setSingleTabShown(false);
//		dockable.setMaximizable(false);
//		mDock.addDockable(dockable);
//		dockable.setVisible(true);
//	}
}
