package com.chalcodes.weaponm.ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
class UiFactory {
	private UiFactory() {}

	public static JFrame newJFrame() {
		final JFrame frame = new JFrame();
		frame.setTitle("Weapon M");
		final ImageIcon icon = new ImageIcon(SplashScreen.class.getResource("/images/icon.png"));
		frame.setIconImage(icon.getImage());
		return frame;
	}
}
