package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.gui.Gui;

public class NewDatabaseAction extends AbstractDatabaseAction {
	private static final long serialVersionUID = 1L;

	public NewDatabaseAction(Gui gui, DatabaseManager dbm) {
		super(gui, dbm);
		ActionManager.setText(this, "NEW_ACTION");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(interactiveClose()) {
			// create a standalone LoginOptions because we don't have a db yet
//			LoginOptions dialogOptions = new LoginOptions();
//			if(LoginOptionsDialog.showDialog(gui.mainWindow, dialogOptions) == LoginOptionsDialog.OK_ACTION) {
//				if(gui.databaseFileChooser.showSaveDialog(gui.mainWindow) == JFileChooser.APPROVE_OPTION) {
//					File file = gui.databaseFileChooser.getSelectedFile();
//					String filename = file.getPath();
//					if(!filename.endsWith(".wmd")) {
//						file = new File(filename + ".wmd");
//					}
//					if(gui.confirmOverwrite(file)) {
//						try {
//							Database database = gui.weapon.dbm.create(file);
//							// copy new login options to database
//							LoginOptions dbOptions = database.getLoginOptions();
//							dbOptions.setTitle(dialogOptions.getTitle());
//							dbOptions.setHost(dialogOptions.getHost());
//							dbOptions.setPort(dialogOptions.getPort());
//							dbOptions.setGame(dialogOptions.getGame());
//							dbOptions.setName(dialogOptions.getName());
//							dbOptions.setPassword(dialogOptions.getPassword());
//							dbOptions.setAutoLogin(dialogOptions.isAutoLogin());
//							gui.weapon.dbm.save();
//							gui.weapon.autoLoadScripts();
//						} catch (Throwable t) {
//							log.error("error creating database", t);
//							gui.threadSafeMessageDialog(t.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
//						}
//					}
//				}
//			}			
		}
	}
}
