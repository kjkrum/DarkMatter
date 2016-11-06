package com.chalcodes.weaponm;

import javax.annotation.Nonnull;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;

/**
 * Main class.
 *
 * @author Kevin Krumwiede
 */
public class WeaponM {

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final File dataDir = Environment.findDataDir();
					final AppModule module = new AppModule(dataDir);
					final AppComponent component = DaggerAppComponent.builder().appModule(module).build();
					component.ui().show();
				} catch (IOException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		});
	}

	private WeaponM(@Nonnull final File dataDir) {}
}
