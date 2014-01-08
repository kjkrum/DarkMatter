package com.chalcodes.weaponm.gui.menu;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JMenu;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;
import com.chalcodes.weaponm.network.NetworkManager;

class NetworkMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	NetworkMenu(Gui gui, DatabaseManager dbm, NetworkManager network, EventSupport eventSupport) {
		I18n.setText(this, "MENU_NETWORK");
		add(new ConnectAction(dbm, network, eventSupport));
		add(new DisconnectAction(network, eventSupport));
		addSeparator();
		add(new ShowLoginOptionsDialogAction(gui, dbm, eventSupport));
		
		// enable on load
		eventSupport.addPropertyChangeListener(EventType.DATABASE_OPEN, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				boolean open = (Boolean) e.getNewValue();
				setEnabled(open);
			}
		});
		setEnabled(false);
	}
}
