package com.chalcodes.weaponm.gui.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.WeaponEvent;

public class TerminalPasteAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(TerminalPasteAction.class.getSimpleName());
	private final EventSupport eventSupport;
	private final Clipboard clipboard;
	
	public TerminalPasteAction(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
		this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		ActionManager.setText(this, "ACTION_PASTE");
		putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
			try {
				String string = (String) clipboard.getData(DataFlavor.stringFlavor);
				string = string.replaceAll("(\n|\r\n?)", "\r\n");
				WeaponEvent event = new WeaponEvent(EventType.TEXT_TYPED, null, string);
				eventSupport.firePropertyChange(event);	
			}
			catch (UnsupportedFlavorException | IOException ex) {
				log.warn("shouldn't happen", e);
			}		
		}
	}

}
