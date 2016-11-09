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

	public static void i(String message) {
		LOG.info(message);
	}

	public static void w(String message, Throwable throwable) {
		LOG.warn(message, throwable);
	}

	public static void e(String message, Throwable throwable) {
		LOG.error(message, throwable);
	}
}
