package com.chalcodes.weaponm;

import javax.swing.UIManager;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
class SwingUtil {
	private SwingUtil() {}

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
			Log.w("error setting look and feel", e);
		}
	}
}
