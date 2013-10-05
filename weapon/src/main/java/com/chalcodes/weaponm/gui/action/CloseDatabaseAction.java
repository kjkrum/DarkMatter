package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.gui.Gui;

class CloseDatabaseAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final Gui gui;
	
	CloseDatabaseAction(Gui gui) {
		this.gui = gui;
		ActionManager.setText(this, "ACTION_CLOSE");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.interactiveClose();
	}

}
