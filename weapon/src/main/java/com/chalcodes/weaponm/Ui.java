package com.chalcodes.weaponm;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.station.CScreenDockStationWindowClosingStrategy;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.screen.window.DefaultScreenDockWindowFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.Image;
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
	private final JFrame mMainWindow;
	private final CControl mDockControl;
	private final File mLayoutFile;

	Ui(@Nonnull final File dataDir,
	   @Nonnull final DatabaseManager databaseManager,
	   @Nonnull final NetworkManager networkManager,
	   @Nonnull final PluginManager pluginManager) {
		setLookAndFeel();
		mMainWindow = createMainWindow();
		mDockControl = createDockControl(mMainWindow);
		mMainWindow.add(mDockControl.getContentArea());
		mLayoutFile = new File(dataDir, "layout.bin");
	}

	void show() {
		mMainWindow.setVisible(true);
	}

	private static void setLookAndFeel() {
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

	private JFrame createMainWindow() {
		final JFrame frame = new JFrame("Weapon M");
		frame.setIconImage(loadIcon());
//		frame.setJMenuBar(menuBar); // TODO add menu bar
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				confirmExit();
			}
		});
		frame.setSize(800, 600);
		return frame;
	}

	private static Image loadIcon() {
		final URL url = Ui.class.getResource("/icon.png");
		final ImageIcon icon = new ImageIcon(url);
		return icon.getImage();
	}

	private static CControl createDockControl(@Nonnull final JFrame mainWindow) {
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

	private static final String LAYOUT_NAME = "default";

	// TODO call this when plugins are loaded
	private void loadLayout() {
		if(mLayoutFile.exists() && mLayoutFile.canRead()) {
			try {
				mDockControl.getResources().readFile(mLayoutFile);
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
			mDockControl.getResources().writeFile(mLayoutFile);
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
