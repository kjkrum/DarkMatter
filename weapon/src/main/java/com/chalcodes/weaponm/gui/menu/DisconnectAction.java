package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;
import com.chalcodes.weaponm.network.NetworkManager;
import com.chalcodes.weaponm.network.NetworkState;

class DisconnectAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final NetworkManager network;
	
	DisconnectAction(Gui gui) {
		this.network = gui.getWeapon().getNetworkManager();
		I18n.setText(this, "ACTION_DISCONNECT");
		
		// enable on connect
		gui.getWeapon().getEventSupport().addPropertyChangeListener(EventType.NETWORK_STATE, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				NetworkState status = (NetworkState) e.getNewValue();
				setEnabled(status == NetworkState.CONNECTING || status == NetworkState.CONNECTED);
			}
		});
		setEnabled(false);
	}	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		network.disconnect();		
	}

}
