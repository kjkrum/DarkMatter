package com.chalcodes.weaponm;

import com.getsentry.raven.Raven;
import com.getsentry.raven.RavenFactory;
import com.getsentry.raven.event.EventBuilder;
import com.getsentry.raven.event.helper.EventBuilderHelper;

/**
 * Remote bug reporting.
 *
 * @author Kevin Krumwiede
 */
public class BugReport {
	// not using the slf4j appender because we don't necessarily want to report everything

	private BugReport() {}

	// https://docs.sentry.io/clients/java/config/
	private static final String DSN =
			"https://b753eb6a92fd45aabea17f762f64e7d8:1562e11a4a2048a0be5fd2cc8d8bf98a@sentry.io/172969" +
			"?raven.async.threads=1" +
			"&raven.buffer.shutdowntimeout=5000";
	private static final Raven gRaven = RavenFactory.ravenInstance(DSN);

	static {
		// remove standard builder helpers, if there are any
		for(final EventBuilderHelper helper : gRaven.getBuilderHelpers()) {
			gRaven.removeBuilderHelper(helper);
		}

		// customize what gets sent
		gRaven.addBuilderHelper(new EventBuilderHelper() {
			@Override
			public void helpBuildingEvent(final EventBuilder builder) {
				// can't just set this to null or it will be repopulated in builder.build()
				builder.withServerName("unknown");
				builder.withRelease(WeaponM.getVersion());
				addTag(builder, "os.name");
				addTag(builder, "os.version");
				addTag(builder, "java.vm.name");
				addTag(builder, "java.vm.version");
				// processors, memory?
			}

			private void addTag(final EventBuilder builder, final String name) {
				builder.withTag(name, System.getProperty(name, "unknown"));
			}
		});
	}

	/**
	 * Sets up the uncaught exception handler.
	 */
	public static void init() {
		final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		if(!(defaultHandler instanceof SentryUncaughtExceptionHandler)) {
			Thread.setDefaultUncaughtExceptionHandler(new SentryUncaughtExceptionHandler(defaultHandler));
		}
	}

	private static class SentryUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		private final Thread.UncaughtExceptionHandler mDefaultHandler;

		private SentryUncaughtExceptionHandler(final Thread.UncaughtExceptionHandler defaultHandler) {
			mDefaultHandler = defaultHandler;
		}

		@Override
		public void uncaughtException(final Thread t, final Throwable e) {
			sendException(e);
			if(mDefaultHandler != null) {
				mDefaultHandler.uncaughtException(t, e);
			}
		}
	}

	synchronized public static void sendMessage(final String message) {
		gRaven.sendMessage(message);
	}

	synchronized public static void sendException(final Throwable throwable) {
		gRaven.sendException(throwable);
	}
}
