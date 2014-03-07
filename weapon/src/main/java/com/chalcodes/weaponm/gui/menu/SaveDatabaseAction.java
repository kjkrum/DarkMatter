package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.database.DatabaseState;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class SaveDatabaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private final Gui gui;
	private final DatabaseManager dbm;
	
	SaveDatabaseAction(Gui gui) {
		this.gui = gui;
		this.dbm = gui.getWeapon().getDatabaseManager();
		I18n.setText(this, "ACTION_SAVE");
		putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		
		// enable on dirty
		gui.getWeapon().getEventSupport().addPropertyChangeListener(EventType.DATABASE_STATE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				DatabaseState state = (DatabaseState) e.getNewValue();
				setEnabled(state == DatabaseState.OPEN_DIRTY);
			}
		});
		setEnabled(gui.getWeapon().getDatabaseManager().isDatabaseDirty());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(dbm.isDatabaseOpen()) {
			try {
				dbm.save();
			} catch (IOException e) {
				log.error("error saving database", e);
				gui.showMessageDialog(e.getMessage(), I18n.getString("TITLE_DATABASE_ERROR"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

}
