package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.database.LoginOptions;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class NewDatabaseAction extends AbstractFileAction {
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private final DatabaseManager dbm;

	NewDatabaseAction(Gui gui) {
		super(gui);
		this.dbm = gui.getWeapon().getDatabaseManager();
		I18n.setText(this, "ACTION_NEW");
		putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(gui.interactiveClose()) {
			LoginOptions options = new LoginOptions(null);
			if(gui.showLoginOptionsDialog(options) == JOptionPane.OK_OPTION) {
				File file = gui.showSaveDialog();
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
							gui.showMessageDialog(ex.getMessage(), I18n.getString("TITLE_DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		}
	}
}
