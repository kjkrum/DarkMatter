package com.chalcodes.weaponm.gui;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides localized UI strings.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class Strings {
	public static final String TERMINAL = "TERMINAL";
	public static final String DATABASE_MENU = "DATABASE_MENU";
	public static final String OPEN_ACTION = "OPEN_ACTION";
	public static final String WEAPON_MENU = "WEAPON_MENU";
	public static final String ABOUT_ACTION = "ABOUT_ACTION";
	public static final String CREDITS_ACTION = "CREDITS_ACTION";
	
	private static final Logger log = LoggerFactory.getLogger(Strings.class.getSimpleName());
	private static final ResourceBundle bundle = ResourceBundle.getBundle("com/chalcodes/weaponm/Strings");

	
	public static String getString(String key) {
		if(bundle.containsKey(key)) {
			return bundle.getString(key);
		}
		else {
			log.warn("missing resource for key \"{}\"", key);
			return "";
		}
	}
	
	private Strings() { }
}
