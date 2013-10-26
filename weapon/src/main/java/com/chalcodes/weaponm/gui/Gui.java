package com.chalcodes.weaponm.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.event.CDockableStateListener;
import bibliothek.gui.dock.common.intern.AbstractCDockable;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.screen.ScreenDockWindowFactory;
import bibliothek.gui.dock.station.screen.window.DefaultScreenDockWindowFactory;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

import com.chalcodes.weaponm.AppSettings;
import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.database.LoginOptions;
import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.EventListener;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.action.ActionManager;
import com.chalcodes.weaponm.network.NetworkManager;

/**
 * An abstraction of the UI.  This is the real "main class" of the program.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class Gui {
	private static final String APP_TITLE = "Weapon M";
	private final Logger log = LoggerFactory.getLogger(Gui.class.getSimpleName());
	private final JFrame mainWindow;
	private final ImageIcon icon;
	private final CControl dockControl;
	private final EventSupport eventSupport;
	private final DatabaseManager dbm;
	private final NetworkManager network;
	private final ActionManager actionManager;
	private final JFileChooser databaseFileChooser;
	private final CreditsWindow creditsWindow;
	
	public Gui() {
		log.info("{} started", APP_TITLE);
		// this is supposed to set the app title in the menu bar on OS X
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_TITLE);
		setLookAndFeel();
		
		mainWindow = new JFrame(APP_TITLE);
		icon = new ImageIcon(getClass()
				.getResource("/com/chalcodes/weaponm/DarkMatter.png"));
		dockControl = new CControl(mainWindow);
		eventSupport = new GuiEventSupport();
		dbm = new DatabaseManager(eventSupport);
		network = new NetworkManager(eventSupport);
		actionManager = new ActionManager(this, eventSupport, dbm, network, dockControl);
		databaseFileChooser = new JFileChooser();
		creditsWindow = new CreditsWindow(icon);
		
		configureFileChoosers();
		configureDocking();
		createDockables();
		configureMainWindow();
		loadAppleExtensions();
	}

	public void setVisible(boolean visible) {
		mainWindow.setVisible(visible);
	}
	
	Image getIconImage() {
		return icon.getImage();
	}

	private void setLookAndFeel() {
		String laf = AppSettings.getLookAndFeel();
		try {
			if ("System".equals(laf)) {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			}
			else {
				for (LookAndFeelInfo info : UIManager
				.getInstalledLookAndFeels()) {
					if (info.getName().equals(laf)) {
						UIManager.setLookAndFeel(info.getClassName());
						return;
					}
				}
				log.warn("unable to locate \"{}\" look & feel", laf);
			}
		} catch (Throwable t) {
			log.error("error setting look & feel", t);
		}
	}
	
	private void configureFileChoosers() {
		databaseFileChooser.setFileFilter(new FileNameExtensionFilter("Weapon M databases", "wmd"));
		databaseFileChooser.setAcceptAllFileFilterUsed(false);
		databaseFileChooser.setMultiSelectionEnabled(false);
		
		// TODO listen for db title events to suggest file names
	}

	private void configureDocking() {
		// float dockables in decorated frames
		ScreenDockWindowFactory sdwf = dockControl
				.getProperty(ScreenDockStation.WINDOW_FACTORY);
		DefaultScreenDockWindowFactory dsdwf;
		if (sdwf instanceof DefaultScreenDockWindowFactory) {
			dsdwf = (DefaultScreenDockWindowFactory) sdwf;
		} else {
			dsdwf = new DefaultScreenDockWindowFactory();
			dockControl.putProperty(ScreenDockStation.WINDOW_FACTORY, dsdwf);
		}
		dsdwf.setKind(DefaultScreenDockWindowFactory.Kind.FRAME);
		dsdwf.setUndecorated(false);

		// put tabs on top
		dockControl.putProperty(StackDockStation.TAB_PLACEMENT,
				TabPlacement.TOP_OF_DOCKABLE);

		// set the theme
		dockControl.getThemes().select(ThemeMap.KEY_ECLIPSE_THEME);

		// give it a default size
		dockControl.getContentArea().setPreferredSize(new Dimension(640, 480));
		
		// add listener to close all dockables when db is closed
		EventListener listener = new EventListener() {
			@Override
			public void onEvent(Event event) {
				for(int i = 0; i < dockControl.getCDockableCount(); ++i) {
					dockControl.getCDockable(i).setVisible(false);
				}
			}
		};
		eventSupport.addEventListener(listener, EventType.DB_CLOSED);
		// TODO restore layout when db is opened
	}
	
	private void createDockables() {
		try {
			dockControl.addDockable(new Terminal(eventSupport).getDockable());
		}
		catch(IOException | ClassNotFoundException e) {
			// TODO something better
			e.printStackTrace();
		}
		
		// add state listener to all dockables
		CDockableStateListener listener = new CDockableStateListener() {
			@Override
			public void extendedModeChanged(CDockable arg0, ExtendedMode arg1) {
				// ignored
			}
			@Override
			public void visibilityChanged(CDockable cd) {
				((AbstractCDockable) cd).toFront();			
			}
		};
		
		for(int i = 0; i < dockControl.getCDockableCount(); ++i) {
			dockControl.getCDockable(i).addCDockableStateListener(listener);
		}
	}

	private void configureMainWindow() {
		mainWindow.setIconImage(icon.getImage());
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				interactiveShutdown();
			}
		});
		mainWindow.add(dockControl.getContentArea());
		mainWindow.setJMenuBar(actionManager.getMenuBar());
		mainWindow.setExtendedState(mainWindow.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		mainWindow.pack();		
	}
	
	private void loadAppleExtensions() {
		if (System.getProperty("os.name").contains("OS X")) {
			try {
				Class<?> klass = Class.forName("com.chalcodes.weaponm.gui.AppleExtensions");
				Constructor<?> ctor = klass.getConstructor(Gui.class);
				Runnable ext = (Runnable) ctor.newInstance(this);
				ext.run();
			} catch (Throwable t) {
				log.error("error loading Apple extensions", t);
			}
		}
	}
	
	/**
	 * Thread-safe.
	 * 
	 * @param message
	 * @param title
	 * @param messageType
	 */
	public void showMessageDialog(final Object message, final String title, final int messageType) {
		if(SwingUtilities.isEventDispatchThread()) {
			JOptionPane.showMessageDialog(mainWindow, message, title, messageType);
		}
		else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(mainWindow, message, title, messageType);
				}
			});
		}
	}
	
	/**
	 * Not thread-safe.
	 * 
	 * @param message
	 * @param title
	 * @param optionType
	 * @param messageType
	 * @param options
	 * @param initialValue
	 * @return
	 */
	public int showOptionDialog(Object message, String title, int optionType,
			int messageType, Object[] options, Object initialValue) {
		return JOptionPane.showOptionDialog(mainWindow, message, title,
				optionType, messageType, null, options, initialValue);
	}
	
	/**
	 * Not thread-safe.
	 * 
	 * @param message
	 * @param title
	 * @return
	 */
	public boolean showYesNoDialog(Object message, String title) {
		return JOptionPane.showConfirmDialog(mainWindow, message, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) ==
				JOptionPane.YES_OPTION;
	}
	
	public File showDatabaseOpenDialog() {
		if(databaseFileChooser.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
			return databaseFileChooser.getSelectedFile();
		}
		else {
			return null;
		}
	}
	
	public File showDatabaseSaveDialog() {
		if(databaseFileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
			return databaseFileChooser.getSelectedFile();
		}
		else {
			return null;
		}
	}
	
	public void showAboutDialog() {
		AboutDialog.showDialog(mainWindow);
	}

	public void showCreditsWindow() {
		creditsWindow.setVisible(true);
		creditsWindow.toFront();
		creditsWindow.setLocationRelativeTo(mainWindow);
	}

	public int showLoginOptionsDialog(LoginOptions loginOptions, boolean fireTitleEventOnSave) {
		return LoginOptionsDialog.showDialog(mainWindow, loginOptions, fireTitleEventOnSave ? eventSupport : null);
	}
	
	/**
	 * Interactively shut down the application.
	 */
	public void interactiveShutdown() {
		if(showYesNoDialog(
				Strings.getString("QUESTION_EXIT"),
				Strings.getString("TITLE_CONFIRM_EXIT"))) {
			// TODO fire event to kill network & scripts
			if(dbm.isDatabaseDirty()) {
				if(interactiveClose()) {
					shutdown();
				}
			}
			else {
				shutdown();
			}
		}
	}
	
	private void shutdown() {
		// TODO fire events
		log.info("{} exiting", APP_TITLE);
		System.exit(0);
	}
	
	/**
	 * Interactively close the database.
	 * 
	 * @return true if the database is now closed, false if it is still open
	 */
	public boolean interactiveClose() {
		if(dbm.isDatabaseOpen()) {
			String[] options = {
					Strings.getString("BUTTON_SAVE_CLOSE"),
					Strings.getString("BUTTON_DISCARD_CLOSE"),
					Strings.getString("BUTTON_CANCEL_CLOSE")
					};
			int option = showOptionDialog(
					Strings.getString("QUESTION_CLOSE"),
					Strings.getString("TITLE_CONFIRM_CLOSE"),
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					options,
					options[0]);
			if(option == JOptionPane.YES_OPTION) {
				try {
					dbm.save();
					dbm.close();
				} catch (IOException ex) {
					log.error("error saving database", ex);
					showMessageDialog(ex.getMessage(), Strings.getString("TITLE_DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
				}
			}
			else if(option == JOptionPane.NO_OPTION) {
				dbm.close();
			}
			else {
				return false;
			}
		}
		return true;
	}

}
