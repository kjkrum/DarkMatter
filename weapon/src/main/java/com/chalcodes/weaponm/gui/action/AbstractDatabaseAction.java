package com.chalcodes.weaponm.gui.action;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.Strings;

@SuppressWarnings("serial") // class is abstract
abstract class AbstractDatabaseAction extends AbstractFileAction {
	final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
	final DatabaseManager dbm;

	AbstractDatabaseAction(Gui gui, DatabaseManager dbm) {
		super(gui);
		this.dbm = dbm;
	}
	
	/**
	 * Returns true if the database is now closed.
	 */
	boolean interactiveClose() {
		if(dbm.isDatabaseOpen()) {
			String[] options = {
					Strings.getString("SAVE_AND_CLOSE"),
					Strings.getString("CLOSE_WITHOUT_SAVING"),
					Strings.getString("DO_NOT_CLOSE")
					};
			int option = gui.showOptionDialog(
					Strings.getString("CLOSE_CURRENT_DATABASE"),
					Strings.getString("TITLE_CONFIRM_CLOSE"),
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
					gui.showMessageDialog(ex.getMessage(), Strings.getString("TITLE_DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
				}
			}
			else if(option == JOptionPane.NO_OPTION) {
				dbm.close();
			}
			else {
				return false;
			}
		}
		return true;
	}
}
