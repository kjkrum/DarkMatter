package com.chalcodes.weaponm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Log wrapper.
 *
 * @author Kevin Krumwiede
 */
public class Log {
	private Log() {}

	private static final Logger LOG = LoggerFactory.getLogger("default");

	public static void d(String s) {
		LOG.debug(s);
	}

	public static void d(String s, Throwable throwable) {
		LOG.debug(s, throwable);
	}

	public static void i(String s) {
		LOG.info(s);
	}

	public static void i(String s, Throwable throwable) {
		LOG.info(s, throwable);
	}

	public static void w(String s) {
		LOG.warn(s);
	}

	public static void w(String s, Throwable throwable) {
		LOG.warn(s, throwable);
	}

	public static void e(String s) {
		LOG.error(s);
	}

	public static void e(String s, Throwable throwable) {
		LOG.error(s, throwable);
	}
}
