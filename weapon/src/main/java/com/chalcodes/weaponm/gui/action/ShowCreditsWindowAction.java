package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.gui.Gui;

class ShowCreditsWindowAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private final Gui gui;
	
	ShowCreditsWindowAction(Gui gui) {
		this.gui = gui;
		ActionManager.setText(this, "ACTION_CREDITS");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.showCreditsWindow();		
	}

}
