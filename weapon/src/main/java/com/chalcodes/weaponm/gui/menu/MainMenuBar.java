package com.chalcodes.weaponm.gui.menu;

import javax.swing.JMenuBar;

import bibliothek.gui.dock.common.CControl;

import com.chalcodes.weaponm.gui.Gui;

public class MainMenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;

	public MainMenuBar(Gui gui, CControl dockControl) {
		add(new DatabaseMenu(gui));
		add(new ViewMenu(gui, dockControl));
		add(new NetworkMenu(gui));
		add(new WeaponMenu(gui));
	}
}
