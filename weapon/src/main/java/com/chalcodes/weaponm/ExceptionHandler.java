package com.chalcodes.weaponm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

	@Override
	public void uncaughtException(Thread thread, Throwable t) {
		log.error("Uncaught exception in thread {}", thread.getName(), t);
		// TODO uncomment
//		try {
//			BugSense.report(thread, t, false);
//		} catch (Throwable t2) {
//			log.error("Additional error when sending bug report", t2);
//		}
	}

}
