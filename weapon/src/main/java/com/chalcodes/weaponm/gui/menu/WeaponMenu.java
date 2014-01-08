package com.chalcodes.weaponm.gui.menu;

import javax.swing.JMenu;

import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class WeaponMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	WeaponMenu(Gui gui) {
		I18n.setText(this, "MENU_WEAPON");
		add(new ShowAboutDialogAction(gui));
		add(new ShowCreditsWindowAction(gui));
		add(new WebsiteAction());
		addSeparator();
		add(new ExitAction(gui));
	}
}
