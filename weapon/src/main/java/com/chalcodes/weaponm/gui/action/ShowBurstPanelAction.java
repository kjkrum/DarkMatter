package com.chalcodes.weaponm.gui.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class ShowBurstPanelAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final JPanel burstPanel;
	private final JPanel parent;
	
	public ShowBurstPanelAction(JPanel burstPanel, JPanel parent) {
		this.burstPanel = burstPanel;
		this.parent = parent;
		ActionManager.setText(this, "ACTION_BURST");
		putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		parent.add(burstPanel, BorderLayout.SOUTH);
		parent.validate();
	}
	
}
