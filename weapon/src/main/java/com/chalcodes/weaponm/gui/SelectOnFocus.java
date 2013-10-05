package com.chalcodes.weaponm.gui;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SelectOnFocus extends FocusAdapter {
	
	private static final SelectOnFocus instance = new SelectOnFocus(); 
	
	@Override
	public void focusGained(final FocusEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				((JTextField) e.getComponent()).selectAll();
			}
		});		
	}
	
	@Override
	public void focusLost(final FocusEvent e) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				((JTextField) e.getComponent()).select(0, 0);
			}
		});		
	}

	private SelectOnFocus() { }
	
	public static SelectOnFocus getInstance() {
		return instance;
	}
}
