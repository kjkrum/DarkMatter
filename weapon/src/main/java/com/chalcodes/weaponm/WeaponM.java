package com.chalcodes.weaponm;

import com.chalcodes.weaponm.dagger.AppComponent;
import com.chalcodes.weaponm.dagger.DaggerAppComponent;
import com.chalcodes.weaponm.dagger.EagerSingletons;
import com.chalcodes.weaponm.ui.ErrorDialog;
import com.chalcodes.weaponm.ui.MainWindow;
import com.chalcodes.weaponm.ui.SimpleSwingWorker;
import com.chalcodes.weaponm.ui.SplashScreen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Main class.
 *
 * @author Kevin Krumwiede
 */
public class WeaponM {
	private static final long MIN_SPLASH_MS = 2000L;

	public static void main(@Nonnull final String[] args) throws URISyntaxException {
		BugReport.init();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final long startTime = System.currentTimeMillis();
				final SplashScreen splash = SplashScreen.show();
				LookAndFeel.setLookAndFeel();
				// any work on the UI thread here prevents the splash image from being displayed
				new SimpleSwingWorker<AppComponent>() {
					@Override
					protected AppComponent doInBackground() throws Exception {
						final AppComponent component = DaggerAppComponent.create();
						component.inject(new EagerSingletons());
						final long delay = MIN_SPLASH_MS - (System.currentTimeMillis() - startTime);
						if(delay > 0) {
							Thread.sleep(delay);
						}
						return component;
					}

					@Override
					protected void onResult(final AppComponent appComponent) {
						splash.dispose();
						new MainWindow(appComponent.getSessionFactory()).show();
					}

					@Override
					protected void onError(final Exception error) {
						splash.dispose();
						ErrorDialog.show(null, error, true);
					}
				}.execute();
			}
		});
	}

	private static final Logger gLog = LoggerFactory.getLogger(WeaponM.class);
	private static final String gVersion;

	static {
		final Properties props = new Properties();
		try(final InputStream in = WeaponM.class.getResourceAsStream("/META-INF/maven/com.chalcodes.weaponm/weapon/pom.properties")) {
			props.load(in);
		} catch(IOException e) {
			gLog.warn("error reading pom.properties", e);
		}
		gVersion = props.getProperty("version", "unknown");
	}

	public static String getVersion() {
		return gVersion;
	}
}
