package com.chalcodes.weaponm.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import com.chalcodes.jtx.Display;
import com.chalcodes.jtx.extensions.SelectionControl;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.NetworkStatus;
import com.chalcodes.weaponm.gui.action.TerminalCopyAction;
import com.chalcodes.weaponm.gui.action.TerminalPasteAction;

class TerminalClipboardManager {
	private final AbstractAction copyAction;
	private final AbstractAction pasteAction;
	private boolean connected;
	private boolean somethingOnClipboard;

	TerminalClipboardManager(EventSupport eventSupport, Display display, SelectionControl selectionControl) {
		// TODO maybe this should be attached to the viewport instead
		JPopupMenu popupMenu = new JPopupMenu();
		copyAction = new TerminalCopyAction(selectionControl);
		copyAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyAction.setEnabled(false);
		popupMenu.add(copyAction);
		pasteAction = new TerminalPasteAction(eventSupport);
		pasteAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		pasteAction.setEnabled(false);
		popupMenu.add(pasteAction);
		display.setPopupMenu(popupMenu);
		
		PropertyChangeListener propertyListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if(e.getPropertyName().equals(EventType.NETWORK_STATUS.name())) {
					connected = (NetworkStatus) e.getNewValue() == NetworkStatus.CONNECTED;
					pasteAction.setEnabled(connected && somethingOnClipboard);
				}
				else { // SelectionControl.PROPERTY_SELECTION_ACTIVE
					copyAction.setEnabled((boolean) e.getNewValue());
				}
			}
		};
		eventSupport.addPropertyChangeListener(EventType.NETWORK_STATUS, propertyListener);
		eventSupport.addPropertyChangeListener(SelectionControl.PROPERTY_SELECTION_ACTIVE, propertyListener);
		
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		FlavorListener flavorListener =	new FlavorListener() {
			@Override
			public void flavorsChanged(FlavorEvent e) {
				somethingOnClipboard = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
				pasteAction.setEnabled(connected && somethingOnClipboard);
			}
		};
		clipboard.addFlavorListener(flavorListener);
		flavorListener.flavorsChanged(null);
	}
}
