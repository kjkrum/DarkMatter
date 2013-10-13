package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.Strings;

public class OpenDatabaseAction extends AbstractFileAction {
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private final DatabaseManager dbm;
	
	public OpenDatabaseAction(Gui gui, DatabaseManager dbm) {
		super(gui);
		this.dbm = dbm;
		ActionManager.setText(this, "ACTION_OPEN");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(gui.interactiveClose()) {
			File file = gui.showDatabaseOpenDialog();
			if(file != null) {
				try {
					dbm.open(file);
				} catch (IOException ex) {
					log.error("error opening database", ex);
					gui.showMessageDialog(ex.getMessage(), Strings.getString("TITLE_DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
