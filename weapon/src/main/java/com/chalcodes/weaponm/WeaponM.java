package com.chalcodes.weaponm;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.support.util.ApplicationResourceManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
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
				new WeaponM().start();
			}
		});
	}

	private WeaponM() {
		final AppComponent component = DaggerAppComponent.builder().appModule(new AppModule()).build();
		component.inject(this);
	}

	@Inject	JFrame mMainWindow;
	@Inject	CControl mDockControl;
	@Inject ApplicationResourceManager mResources;
	@Inject @Named("layout_file") File mLayoutFile;

	private void start() {
		configureMainWindow();
		loadLayout();
		mMainWindow.setVisible(true);
	}

	private void configureMainWindow() {
		mMainWindow.add(mDockControl.getContentArea());
		mMainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				confirmExit();
			}
		});
	}

	private void confirmExit() {
		if (JOptionPane.showOptionDialog(mMainWindow, "Shut down Weapon M?", "Confirm exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
			exit();
		}
	}

	private void exit() {
		saveLayout();
		// TODO shut down other components
		System.exit(0);
	}

	private static final String LAYOUT_NAME = "default";

	private void loadLayout() {
		if(mLayoutFile.exists() && mLayoutFile.canRead()) {
			try {
				mResources.readFile(mLayoutFile);
				mDockControl.load(LAYOUT_NAME);
				/* If a dockable isn't in the layout file, it's made invisible. */
				final int count = mDockControl.getCDockableCount();
				for(int i = 0; i < count; ++i) {
					mDockControl.getCDockable(i).setVisible(true);
				}
			} catch (IOException | IllegalArgumentException e) {
				e.printStackTrace(); // TODO use logging framework
			}

		}
	}

	private void saveLayout() {
		try {
			mDockControl.save(LAYOUT_NAME);
			mResources.writeFile(mLayoutFile);
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace(); // TODO use logging framework
		}
	}

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
