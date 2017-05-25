package com.chalcodes.weaponm.ui;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
public class SplashScreen {
	private static final Logger gLog = LoggerFactory.getLogger(SplashScreen.class);

	@Nonnull
	public static SplashScreen show() {
		try {
			final JFrame frame = UiFactory.newJFrame();
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setUndecorated(true);
			final URL url = SplashScreen.class.getResource("/images/splash.jpg");
			final ImageIcon image = new ImageIcon(url);
			final JLabel label = new JLabel(image);
			frame.getContentPane().add(label);
			frame.pack();
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setLocation(screen.width / 2 - frame.getSize().width / 2, screen.height / 2 - frame.getSize().height / 2);
			frame.setVisible(true);
			return new SplashScreen(frame);
		}
		catch(Exception e) {
			gLog.warn("error loading splash screen image", e);
			return new SplashScreen(null);
		}
	};

	private final JFrame mFrame;

	private SplashScreen(@Nullable JFrame frame) {
		mFrame = frame;
	}

	public void dispose() {
		if(mFrame != null) {
			mFrame.dispose();
		}
	}
}
