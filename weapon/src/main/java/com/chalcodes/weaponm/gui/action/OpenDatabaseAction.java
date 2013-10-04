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

public class OpenDatabaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OpenDatabaseAction.class);
	private final Gui gui;
	private final DatabaseManager dbm;
	
	public OpenDatabaseAction(Gui gui, DatabaseManager dbm) {
		this.gui = gui;
		this.dbm = dbm;
		ActionManager.setText(this, "OPEN_ACTION");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(dbm.isDatabaseOpen()) {
			String[] options = { "Save and close", "Close without saving", "Do not close" };
			int option = gui.showOptionDialog(
					"Close current database?",
					"Confirm close",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					options,
					options[0]);
			if(option == JOptionPane.YES_OPTION) {
				try {
					dbm.save();
					dbm.close();
				} catch (IOException ex) {
					log.error("error saving database", ex);
					gui.showMessageDialog(ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			else if(option == JOptionPane.NO_OPTION) {
				dbm.close();
			}
			else return;
		}
		File file = gui.showDatabaseOpenDialog();
		if(file != null) {
			try {
				dbm.open(file);
			} catch (IOException ex) {
				log.error("error opening database", ex);
				gui.showMessageDialog(ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
