package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.database.LoginOptions;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.NetworkStatus;
import com.chalcodes.weaponm.gui.I18n;
import com.chalcodes.weaponm.network.NetworkManager;

class ConnectAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final DatabaseManager dbm;
	private final NetworkManager network;
	
	ConnectAction(DatabaseManager dbm, NetworkManager manager, EventSupport eventSupport) {
		this.dbm = dbm;
		this.network = manager;
		I18n.setText(this, "ACTION_CONNECT");
		
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
		if(dbm.isDatabaseOpen()) {
			LoginOptions options = dbm.getLoginOptions();
			network.connect(options.getHost(), options.getPort());
		}
	}
}
