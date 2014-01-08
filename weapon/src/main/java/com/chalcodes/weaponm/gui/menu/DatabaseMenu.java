package com.chalcodes.weaponm.gui.menu;

import javax.swing.JMenu;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class DatabaseMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	DatabaseMenu(Gui gui, DatabaseManager dbm, EventSupport eventSupport) {
		I18n.setText(this, "MENU_DATABASE");
		add(new OpenDatabaseAction(gui, dbm));
		add(new NewDatabaseAction(gui, dbm));
		add(new SaveDatabaseAction(gui, dbm, eventSupport));
		add(new SaveAsDatabaseAction(gui, dbm, eventSupport));
		add(new SaveCopyDatabaseAction(gui, dbm, eventSupport));
		add(new CloseDatabaseAction(gui, eventSupport));
	}
}
