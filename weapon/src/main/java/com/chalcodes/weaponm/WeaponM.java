package com.chalcodes.weaponm;

import java.io.IOException;

import javax.swing.SwingUtilities;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.network.NetworkManager;

/**
 * Container for the various components that make up the 'M' and 'C' of MVC.
 * Conceptually, an instance of this class <em>is</em> the application.  It
 * contains the main method because I like it that way.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class WeaponM {
	private final EventSupport eventSupport;
	private final DatabaseManager databaseManager;
	private final NetworkManager networkManager;
	
	public WeaponM() {
		eventSupport = new EventSupport();
		databaseManager = new DatabaseManager(eventSupport);
		networkManager = new NetworkManager(eventSupport);
	}
	
	public EventSupport getEventSupport() {
		return eventSupport;
	}
	
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}
	
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final WeaponM weapon = new WeaponM();
					final Gui gui = new Gui(weapon);
					gui.setVisible(true);
				}
				catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
