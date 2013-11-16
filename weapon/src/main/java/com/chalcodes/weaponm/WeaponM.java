package com.chalcodes.weaponm;

import java.io.IOException;

import javax.swing.SwingUtilities;

import com.chalcodes.weaponm.gui.Gui;

/**
 * Application startup stub.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class WeaponM {

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new Gui().setVisible(true);
				}
				catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

}
