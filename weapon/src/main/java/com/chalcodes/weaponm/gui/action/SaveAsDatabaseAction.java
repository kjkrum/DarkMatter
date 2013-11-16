package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.Strings;

public class SaveAsDatabaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private final Gui gui;
	private final DatabaseManager dbm;
	
	SaveAsDatabaseAction(Gui gui, DatabaseManager dbm) {
		this.gui = gui;
		this.dbm = dbm;
		ActionManager.setText(this, "ACTION_SAVE_AS");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(dbm.isDatabaseOpen()) {
			File newFile = gui.showDatabaseSaveDialog();
			if(newFile != null) {
				try {
					dbm.saveAs(newFile);
				} catch (IOException ex) {
					log.error("error saving database", ex);
					gui.showMessageDialog(ex.getMessage(), Strings.getString("TITLE_DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
