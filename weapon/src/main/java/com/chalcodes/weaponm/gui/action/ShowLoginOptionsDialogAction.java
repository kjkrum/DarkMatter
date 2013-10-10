package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.gui.Gui;

class ShowLoginOptionsDialogAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private final Gui gui;
	private final DatabaseManager dbm;
	
	ShowLoginOptionsDialogAction(Gui gui, DatabaseManager dbm) {
		this.gui = gui;
		this.dbm = dbm;
		ActionManager.setText(this, "ACTION_LOGIN_OPTIONS");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gui.showLoginOptionsDialog(dbm.getDatabase().getLoginOptions());
	}

}
