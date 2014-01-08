package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.NetworkStatus;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class ShowLoginOptionsDialogAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private final Gui gui;
	private final DatabaseManager dbm;
	
	ShowLoginOptionsDialogAction(Gui gui, DatabaseManager dbm, EventSupport eventSupport) {
		this.gui = gui;
		this.dbm = dbm;
		I18n.setText(this, "ACTION_LOGIN_OPTIONS");
		
		// enable on disconnect
		eventSupport.addPropertyChangeListener(EventType.NETWORK_STATUS, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				NetworkStatus status = (NetworkStatus) e.getNewValue();
				setEnabled(status == NetworkStatus.DISCONNECTED);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		gui.showLoginOptionsDialog(dbm.getLoginOptions(), true);
	}

}
