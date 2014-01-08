package com.chalcodes.weaponm.gui.menu;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.gui.I18n;

class WebsiteAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	
	public WebsiteAction() {
		I18n.setText(this, "ACTION_WEBSITE");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Desktop.getDesktop().browse(new URL("http://chalcodes.com/wiki/index.php?title=Weapon_M").toURI());
		} catch (Exception ex) {
			log.error("error opening system browser", ex);
		}
	}

}
