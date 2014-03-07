package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;
import com.chalcodes.weaponm.network.NetworkState;

class ShowLoginOptionsDialogAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private final Gui gui;
	private final DatabaseManager dbm;
	
	ShowLoginOptionsDialogAction(Gui gui) {
		this.gui = gui;
		this.dbm = gui.getWeapon().getDatabaseManager();
		I18n.setText(this, "ACTION_LOGIN_OPTIONS");
		
		// enable on disconnect
		gui.getWeapon().getEventSupport().addPropertyChangeListener(EventType.NETWORK_STATE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				NetworkState status = (NetworkState) e.getNewValue();
				setEnabled(status == NetworkState.DISCONNECTED);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gui.showLoginOptionsDialog(dbm.getLoginOptions());
	}

}
