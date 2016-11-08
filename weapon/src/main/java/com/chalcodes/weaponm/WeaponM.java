package com.chalcodes.weaponm;

import javax.swing.SwingUtilities;

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
					final AppModule module = new AppModule();
					final AppComponent component = DaggerAppComponent.builder().appModule(module).build();
					component.ui().show();
				} catch (RuntimeException e) {
					System.err.println(e.getMessage());
					System.exit(1);
				}
			}
		});
	}

	private WeaponM() {}
}
