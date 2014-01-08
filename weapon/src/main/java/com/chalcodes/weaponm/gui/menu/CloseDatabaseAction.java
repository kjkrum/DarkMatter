package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class CloseDatabaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final Gui gui;
	
	CloseDatabaseAction(Gui gui, EventSupport eventSupport) {
		this.gui = gui;
		I18n.setText(this, "ACTION_CLOSE");
		
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.interactiveClose();
	}

}
