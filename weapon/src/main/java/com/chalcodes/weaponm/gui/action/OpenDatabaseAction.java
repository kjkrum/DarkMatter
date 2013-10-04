package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.Strings;

public class OpenDatabaseAction extends AbstractDatabaseAction {
	private static final long serialVersionUID = 1L;
	
	public OpenDatabaseAction(Gui gui, DatabaseManager dbm) {
		super(gui, dbm);
		ActionManager.setText(this, "OPEN_ACTION");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(interactiveClose()) {
			File file = gui.showDatabaseOpenDialog();
			if(file != null) {
				try {
					dbm.open(file);
				} catch (IOException ex) {
					log.error("error opening database", ex);
					gui.showMessageDialog(ex.getMessage(), Strings.getString("DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
