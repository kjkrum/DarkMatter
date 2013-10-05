package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.gui.Strings;

class CloseDatabaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final DatabaseManager dbm;
	
	CloseDatabaseAction(DatabaseManager dbm) {
		this.dbm = dbm;
		ActionManager.setText(this, "ACTION_CLOSE");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(dbm.isDatabaseOpen()) {
			dbm.close();
		}
	}

}
