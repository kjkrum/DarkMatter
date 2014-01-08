package com.chalcodes.weaponm.gui;

import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for localizing UI strings.  String names and values are
 * defined in the "Strings" resource bundle.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class I18n {
	private static final Logger log = LoggerFactory.getLogger(I18n.class.getSimpleName());
	private static final ResourceBundle bundle = ResourceBundle.getBundle("com/chalcodes/weaponm/Strings");
	
	/**
	 * Obtains a localized string.
	 * 
	 * @param key the string name
	 * @return the requested string, or the key itself if there is no value
	 * for the specified key
	 */
	public static String getString(String key) {
		if(bundle.containsKey(key)) {
			return bundle.getString(key);
		}
		else {
			log.warn("missing resource for key \"{}\"", key);
			return key;
		}
	}
	
	/**
	 * Sets the text and keyboard mnemonic for an action.
	 * 
	 * @param action the action
	 * @param key the string name
	 */
	public static void setText(AbstractAction action, String key) {
		String raw = I18n.getString(key);
		String stripped = raw.replace("_", "");
		action.putValue(Action.NAME, stripped);
		int idx = raw.indexOf('_');
		if(idx != -1 && idx < stripped.length()) {
			action.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(stripped.charAt(idx)));
		}
	}
	
	/**
	 * Sets the text and keyboard mnemonic for a button, menu, or menu item.
	 * 
	 * @param button the button
	 * @param key the string name
	 */
	public static void setText(AbstractButton button, String key) {
		String raw = I18n.getString(key);
		String stripped = raw.replace("_", "");
		button.setText(stripped);
		int idx = raw.indexOf('_');
		if(idx != -1 && idx < stripped.length()) {
			button.setMnemonic(stripped.charAt(idx));
		}
	}
	
	private I18n() { }
}
