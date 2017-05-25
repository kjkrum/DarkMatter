package com.chalcodes.weaponm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.UIManager;

/**
 * Sets the look and feel.
 *
 * @author Kevin Krumwiede
 */
class LookAndFeel {
	private static final Logger LOG = LoggerFactory.getLogger(LookAndFeel.class);
	private LookAndFeel() {}

	static void setLookAndFeel() {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					return;
				}
			}
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			LOG.warn("error setting look and feel", e);
		}
	}
}
