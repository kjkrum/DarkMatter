package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class SaveAsDatabaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private final Gui gui;
	private final DatabaseManager dbm;
	
	SaveAsDatabaseAction(Gui gui, DatabaseManager dbm, EventSupport eventSupport) {
		this.gui = gui;
		this.dbm = dbm;
		I18n.setText(this, "ACTION_SAVE_AS");
		
		// enable on load
		eventSupport.addPropertyChangeListener(EventType.DATABASE_OPEN, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				boolean open = (Boolean) e.getNewValue();
				setEnabled(open);
			}
		});
		setEnabled(false);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(dbm.isDatabaseOpen()) {
			File newFile = gui.showSaveDialog();
			if(newFile != null) {
				try {
					dbm.saveAs(newFile);
				} catch (IOException ex) {
					log.error("error saving database", ex);
					gui.showMessageDialog(ex.getMessage(), I18n.getString("TITLE_DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
