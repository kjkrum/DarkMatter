package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.Strings;

class SaveDatabaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private final Gui gui;
	private final DatabaseManager dbm;
	
	SaveDatabaseAction(Gui gui, DatabaseManager dbm) {
		this.gui = gui;
		this.dbm = dbm;
		ActionManager.setText(this, "ACTION_SAVE");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(dbm.isDatabaseOpen()) {
			try {
				dbm.save();
			} catch (IOException e) {
				log.error("error saving database", e);
				gui.showMessageDialog(e.getMessage(), Strings.getString("TITLE_DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
