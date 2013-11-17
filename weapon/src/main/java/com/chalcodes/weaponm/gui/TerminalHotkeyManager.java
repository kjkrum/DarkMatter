package com.chalcodes.weaponm.gui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.chalcodes.jtx.extensions.SelectionControl;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.NetworkStatus;
import com.chalcodes.weaponm.gui.action.ShowBurstPanelAction;
import com.chalcodes.weaponm.gui.action.TerminalCopyAction;
import com.chalcodes.weaponm.gui.action.TerminalPasteAction;

class TerminalHotkeyManager {
	private final AbstractAction copyAction;
	private final AbstractAction pasteAction;
	private final AbstractAction burstAction;
	private final BurstPanel burstPanel;
	private boolean connected;
	private boolean textOnClipboard;

	TerminalHotkeyManager(EventSupport eventSupport, SelectionControl selectionControl, JComponent focusComponent, JPanel parentPanel) {
		final JPopupMenu popupMenu = new JPopupMenu();
		copyAction = new TerminalCopyAction(selectionControl);
		copyAction.setEnabled(false);
		popupMenu.add(copyAction);
		focusComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK), "copy");
		focusComponent.getActionMap().put("copy", copyAction);
		
		pasteAction = new TerminalPasteAction(eventSupport);
		pasteAction.setEnabled(false);
		popupMenu.add(pasteAction);
		focusComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK), "paste");
		focusComponent.getActionMap().put("paste", pasteAction);
		
		burstPanel = new BurstPanel(eventSupport, focusComponent);
		burstAction = new ShowBurstPanelAction(burstPanel, parentPanel);
		burstAction.setEnabled(false);
		popupMenu.add(burstAction);
		focusComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK), "burst");
		focusComponent.getActionMap().put("burst", burstAction);
		
		MouseListener popupListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					showPopupMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					showPopupMenu(e);
				}
			}
			
			private void showPopupMenu(MouseEvent e) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		};
		focusComponent.addMouseListener(popupListener);
		Component[] children = focusComponent.getComponents();
		for(Component child : children) {
			child.addMouseListener(popupListener);
		}
		
		PropertyChangeListener propertyListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if(e.getPropertyName().equals(EventType.NETWORK_STATUS.name())) {
					connected = (NetworkStatus) e.getNewValue() == NetworkStatus.CONNECTED;
					pasteAction.setEnabled(connected && textOnClipboard);
					burstAction.setEnabled(connected);
				}
				else { // SelectionControl.PROPERTY_SELECTION_ACTIVE
					copyAction.setEnabled((boolean) e.getNewValue());
				}
			}
		};
		eventSupport.addPropertyChangeListener(EventType.NETWORK_STATUS, propertyListener);
		eventSupport.addPropertyChangeListener(SelectionControl.PROPERTY_SELECTION_ACTIVE, propertyListener);
		
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		textOnClipboard = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
		clipboard.addFlavorListener(new FlavorListener() {
			@Override
			public void flavorsChanged(FlavorEvent e) {
				textOnClipboard = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
				pasteAction.setEnabled(connected && textOnClipboard);
			}
		});
	}
	
}
