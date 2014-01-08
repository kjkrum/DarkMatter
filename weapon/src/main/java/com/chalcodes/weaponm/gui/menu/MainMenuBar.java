package com.chalcodes.weaponm.gui.menu;

import javax.swing.JMenuBar;

import bibliothek.gui.dock.common.CControl;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.network.NetworkManager;

public class MainMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;

	public MainMenuBar(Gui gui, CControl dockControl, DatabaseManager dbm, NetworkManager network, EventSupport eventSupport) {
		add(new DatabaseMenu(gui, dbm, eventSupport));
		add(new ViewMenu(dockControl, eventSupport));
		add(new NetworkMenu(gui, dbm, network, eventSupport));
		add(new WeaponMenu(gui));
	}
}
