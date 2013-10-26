package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.database.LoginOptions;
import com.chalcodes.weaponm.network.NetworkManager;

class ConnectAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final DatabaseManager dbm;
	private final NetworkManager network;
	
	ConnectAction(DatabaseManager dbm, NetworkManager manager) {
		this.dbm = dbm;
		this.network = manager;
		ActionManager.setText(this, "ACTION_CONNECT");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(dbm.isDatabaseOpen()) {
			LoginOptions options = dbm.getLoginOptions();
			network.connect(options.getHost(), options.getPort());
		}
	}
}
