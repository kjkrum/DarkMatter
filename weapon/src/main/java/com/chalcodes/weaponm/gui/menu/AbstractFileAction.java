package com.chalcodes.weaponm.gui.menu;

import java.io.File;

import javax.swing.AbstractAction;

import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.I18n;

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
					I18n.getString("QUESTION_CLOBBER_FILE"),
					I18n.getString("TITLE_CONFIRM_CLOBBER"));
		}
		return true;
	}

}
