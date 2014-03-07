package com.chalcodes.weaponm.gui.menu;

import javax.swing.JMenu;

import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class DatabaseMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	DatabaseMenu(Gui gui) {
		I18n.setText(this, "MENU_DATABASE");
		add(new OpenDatabaseAction(gui));
		add(new NewDatabaseAction(gui));
		add(new SaveDatabaseAction(gui));
		add(new SaveAsDatabaseAction(gui));
		add(new SaveCopyDatabaseAction(gui));
		add(new CloseDatabaseAction(gui));
	}
}
