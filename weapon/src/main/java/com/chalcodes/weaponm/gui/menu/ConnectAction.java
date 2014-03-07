package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.database.LoginOptions;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;
import com.chalcodes.weaponm.network.NetworkManager;
import com.chalcodes.weaponm.network.NetworkState;

class ConnectAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final DatabaseManager dbm;
	private final NetworkManager network;
	
	ConnectAction(Gui gui) {
		this.dbm = gui.getWeapon().getDatabaseManager();
		this.network = gui.getWeapon().getNetworkManager();
		I18n.setText(this, "ACTION_CONNECT");
		
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
		if(dbm.isDatabaseOpen()) {
			LoginOptions options = dbm.getLoginOptions();
			network.connect(options.getHost(), options.getPort());
		}
	}
}
