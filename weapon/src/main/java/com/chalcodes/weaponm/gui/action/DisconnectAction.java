package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.network.NetworkManager;

class DisconnectAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final NetworkManager network;
	
	DisconnectAction(NetworkManager network) {
		this.network = network;
		ActionManager.setText(this, "ACTION_DISCONNECT");
	}	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		network.disconnect();		
	}

}
