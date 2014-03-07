package com.chalcodes.weaponm.gui.menu;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenu;

import com.chalcodes.weaponm.database.DatabaseState;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class NetworkMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	NetworkMenu(Gui gui) {
		I18n.setText(this, "MENU_NETWORK");
		add(new ConnectAction(gui));
		add(new DisconnectAction(gui));
		addSeparator();
		add(new ShowLoginOptionsDialogAction(gui));
		
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
}
