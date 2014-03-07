package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.database.DatabaseState;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class CloseDatabaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final Gui gui;
	
	CloseDatabaseAction(Gui gui) {
		this.gui = gui;
		I18n.setText(this, "ACTION_CLOSE");
		
		// enable on load
		gui.getWeapon().getEventSupport().addPropertyChangeListener(EventType.DATABASE_STATE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				DatabaseState state = (DatabaseState) e.getNewValue();
				setEnabled(state == DatabaseState.OPEN_CLEAN || state == DatabaseState.OPEN_DIRTY);
			}
		});
		setEnabled(gui.getWeapon().getDatabaseManager().isDatabaseOpen());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.interactiveClose();
	}

}
