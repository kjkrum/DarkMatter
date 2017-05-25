package com.chalcodes.weaponm;

import com.chalcodes.weaponm.dagger.AppComponent;
import com.chalcodes.weaponm.dagger.DaggerAppComponent;
import com.chalcodes.weaponm.dagger.EagerSingletons;
import com.chalcodes.weaponm.ui.ErrorDialog;
import com.chalcodes.weaponm.ui.MainWindow;
import com.chalcodes.weaponm.ui.SimpleSwingWorker;
import com.chalcodes.weaponm.ui.SplashScreen;

import javax.annotation.Nonnull;
import javax.swing.SwingUtilities;
import java.net.URISyntaxException;

/**
 * Main class.
 *
 * @author Kevin Krumwiede
 */
public class WeaponM {
	private static final long MIN_SPLASH_MS = 2000L;

	public static void main(@Nonnull final String[] args) throws URISyntaxException {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final long startTime = System.currentTimeMillis();
				final SplashScreen splash = SplashScreen.show();
				LookAndFeel.setLookAndFeel();
				// any work on the UI thread here prevents the splash image from displaying
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
}
