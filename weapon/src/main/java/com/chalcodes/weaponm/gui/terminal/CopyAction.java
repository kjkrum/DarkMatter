package com.chalcodes.weaponm.gui.terminal;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.chalcodes.jtx.extensions.SelectionControl;
import com.chalcodes.weaponm.gui.action.ActionManager;

public class CopyAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final SelectionControl selectionControl;

	public CopyAction(SelectionControl selectionControl) {
		this.selectionControl = selectionControl;
		ActionManager.setText(this, "ACTION_COPY");
		putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO convert selection and put it on system clipboard
		selectionControl.clearSelection();
	}
}
