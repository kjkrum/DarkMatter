package com.chalcodes.weaponm;

import bibliothek.gui.dock.common.CControl;
import org.neo4j.ogm.session.Session;

import javax.annotation.Nonnull;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/**
 * Main class.
 *
 * @author Kevin Krumwiede
 */
public class WeaponM {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final AppModule module = new AppModule();
					final AppComponent component = DaggerAppComponent.builder().appModule(module).build();
					component.weapon().start();
				} catch (RuntimeException e) {
					Log.e("something bad happened", e);
					System.exit(1);
				}
			}
		});
	}

	private final JFrame mMainWindow;
	private final CControl mDockControl;
	private final File mLayoutFile;
	private final Session mSession;

	WeaponM(@Nonnull final JFrame mainWindow,
	        @Nonnull final CControl dockControl,
	        @Nonnull final File layoutFile,
	        @Nonnull final Session session) {
		mMainWindow = mainWindow;
		mDockControl = dockControl;
		mLayoutFile = layoutFile;
		mSession = session;
	}

	/**
	 * Effectively the main method.
	 */
	private void start() {
		setLookAndFeel();
		configureMainWindow();
		loadLayout(); // TODO actually do this after plugins are loaded
		// TODO ALL THE THINGS!
		mMainWindow.setVisible(true);
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
		mMainWindow.add(mDockControl.getContentArea(), BorderLayout.CENTER);
		mMainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				confirmExit();
			}
		});
	}

	private static final String LAYOUT_NAME = "default";

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
