package com.chalcodes.weaponm.gui.action;

import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.Strings;

@SuppressWarnings("serial") // class is abstract
abstract class AbstractFileAction extends AbstractAction {
	
	final Gui gui;
	
	AbstractFileAction(Gui gui) {
		this.gui = gui;
	}

	/** Returns true if the file can be written. */
	boolean interactiveClobber(File file) {
		if(file.exists()) {
			return gui.showYesNoDialog(
					Strings.getString("CLOBBER_FILE"),
					Strings.getString("TITLE_CONFIRM_CLOBBER")
					) == JOptionPane.YES_OPTION;
		}
		return true;
	}

}
