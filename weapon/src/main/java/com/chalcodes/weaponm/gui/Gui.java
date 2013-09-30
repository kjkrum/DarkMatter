package com.chalcodes.weaponm.gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Constructor;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.screen.ScreenDockWindowFactory;
import bibliothek.gui.dock.station.screen.window.DefaultScreenDockWindowFactory;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;

import com.chalcodes.weaponm.AppSettings;
import com.chalcodes.weaponm.LogName;
import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.gui.action.ActionManager;

/**
 * An abstraction of the UI.  This is the real "main class" of the program.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class Gui {
	private static final String APP_TITLE = "Weapon M";
	private final Logger log = LoggerFactory.getLogger(LogName.forObject(this));
	private final JFrame mainWindow;
	private final ImageIcon icon;
	private final CControl dockControl;
	private final EventSupport eventSupport;
	private final DatabaseManager dbm;
	private final ActionManager actionManager;
	
	public Gui() {
		log.info("{} started", APP_TITLE);
		// this supposedly sets the app title in the menu bar on os x...
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", APP_TITLE);
		setLookAndFeel();
		
		mainWindow = new JFrame(APP_TITLE);
		icon = new ImageIcon(getClass()
				.getResource("/com/chalcodes/weaponm/DarkMatter5.png"));
		dockControl = new CControl(mainWindow);
		eventSupport = new SwingEventSupport();
		dbm = new DatabaseManager(eventSupport);
		actionManager = new ActionManager(eventSupport, dbm);
		
		configureDocking();
		configureMainWindow();
		loadAppleExtensions();
	}

	public void setVisible(boolean visible) {
		mainWindow.setVisible(visible);
	}
	
	public void dispatchEvent(Event event) {
		eventSupport.dispatchEvent(event);
	}
	
	ImageIcon getIcon() {
		return icon;
	}

	private void setLookAndFeel() {
		String laf = AppSettings.getLookAndFeel();
		try {
			if ("Nimbus".equals(laf)) {
				for (LookAndFeelInfo info : UIManager
						.getInstalledLookAndFeels()) {
					if ("Nimbus".equals(info.getName())) {
						UIManager.setLookAndFeel(info.getClassName());
						break;
					}
				}
			} else if ("System".equals(laf)) {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			}
		} catch (Throwable t) {
			log.error("falling back to Metal look & feel", t);
		}
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
	}

	private void configureMainWindow() {
		mainWindow.setIconImage(icon.getImage());
		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO call shutdown on gui or action manager
				log.info("{} exiting", APP_TITLE);
				System.exit(0);
			}
		});
		mainWindow.add(dockControl.getContentArea());
		// TODO mainWindow.setJMenuBar(actionManager.getMenuBar());
		mainWindow.setExtendedState(mainWindow.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		mainWindow.pack();		
	}
	
	protected void loadAppleExtensions() {
		if (System.getProperty("os.name").contains("OS X")) {
			try {
				Class<?> klass = Class.forName("krum.weaponm.gui.AppleExtensions");
				Constructor<?> ctor = klass.getConstructor(Gui.class);
				Runnable ext = (Runnable) ctor.newInstance(this);
				ext.run();
			} catch (Throwable t) {
				log.error("error loading Apple extensions", t);
			}
		}
	}
}
