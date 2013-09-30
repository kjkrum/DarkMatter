package com.chalcodes.weaponm.gui;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.QuitEvent;
import com.apple.eawt.Application;
import com.apple.eawt.QuitHandler;
import com.apple.eawt.QuitResponse;
import com.apple.eawt.QuitStrategy;

/**
 * Improves Dock and Finder integration on OS X.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class AppleExtensions implements Runnable {
	protected final Gui gui;
	
	public AppleExtensions(Gui gui) {
		this.gui = gui;
	}

	@Override
	public void run() {
		Application app = Application.getApplication();

		app.setDockIconImage(gui.getIcon().getImage());
		
		app.setAboutHandler(new AboutHandler() {
			@Override
			public void handleAbout(AboutEvent evt) {
				// TODO AboutDialog.showDialog(gui.mainWindow);
			}
		});
		
		app.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
		
		app.setQuitHandler(new QuitHandler() {
			@Override
			public void handleQuitRequestWith(QuitEvent evt, QuitResponse response) {
				// cancel the OS-initiated quit, then invoke WM's own quit
				response.cancelQuit();
				// TODO gui.mainWindow.confirmExit();
			}
		});
	}

}
