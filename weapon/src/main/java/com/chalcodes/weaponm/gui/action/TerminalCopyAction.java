package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.chalcodes.jtx.extensions.SelectionControl;

public class TerminalCopyAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final SelectionControl selectionControl;

	public TerminalCopyAction(SelectionControl selectionControl) {
		this.selectionControl = selectionControl;
		ActionManager.setText(this, "ACTION_COPY");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO convert selection and put it on system clipboard
		selectionControl.clearSelection();
	}
}
