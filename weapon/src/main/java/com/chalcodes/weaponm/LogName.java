package com.chalcodes.weaponm;

import java.util.Locale;

/**
 * Creates logger names for individual objects.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class LogName {

	public static String forObject(Object obj) {
		StringBuilder sb = new StringBuilder();
		sb.append(obj.getClass().getSimpleName());
		sb.append('-');
		sb.append(Integer.toString(obj.hashCode(), 36).toUpperCase(Locale.US));
		return sb.toString();
	}
	
	private LogName() { }
}
