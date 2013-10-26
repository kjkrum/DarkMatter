package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.database.LoginOptions;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.Strings;

public class NewDatabaseAction extends AbstractFileAction {
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private final DatabaseManager dbm;

	public NewDatabaseAction(Gui gui, DatabaseManager dbm) {
		super(gui);
		this.dbm = dbm;
		ActionManager.setText(this, "ACTION_NEW");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(gui.interactiveClose()) {
			LoginOptions options = new LoginOptions();
			if(gui.showLoginOptionsDialog(options) == JOptionPane.OK_OPTION) {
				File file = gui.showDatabaseSaveDialog();
				if(file != null) {
					String filename = file.getPath();
					if(!filename.endsWith(".wmd")) {
						file = new File(filename + ".wmd");
					}
					if(interactiveClobber(file)) {
						try {
							dbm.create(file, options);
							dbm.save();
							// TODO fire events
						}
						catch(IOException ex) {
							log.error("error creating database", ex);
							gui.showMessageDialog(ex.getMessage(), Strings.getString("TITLE_DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
	}
}
