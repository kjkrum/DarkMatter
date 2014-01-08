package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.NetworkStatus;
import com.chalcodes.weaponm.gui.I18n;
import com.chalcodes.weaponm.network.NetworkManager;

class DisconnectAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final NetworkManager network;
	
	DisconnectAction(NetworkManager network, EventSupport eventSupport) {
		this.network = network;
		I18n.setText(this, "ACTION_DISCONNECT");
		
		// enable on connect
		eventSupport.addPropertyChangeListener(EventType.NETWORK_STATUS, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				NetworkStatus status = (NetworkStatus) e.getNewValue();
				setEnabled(status == NetworkStatus.CONNECTING || status == NetworkStatus.CONNECTED);
			}
		});
		setEnabled(false);
	}	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		network.disconnect();		
	}

}
