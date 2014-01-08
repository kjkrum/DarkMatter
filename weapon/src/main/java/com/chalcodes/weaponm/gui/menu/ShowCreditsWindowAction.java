package com.chalcodes.weaponm.gui.menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

class ShowCreditsWindowAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	
	private final Gui gui;
	
	ShowCreditsWindowAction(Gui gui) {
		this.gui = gui;
		I18n.setText(this, "ACTION_CREDITS");
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		gui.showCreditsWindow();		
	}

}
