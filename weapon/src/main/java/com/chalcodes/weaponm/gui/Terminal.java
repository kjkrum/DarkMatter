package com.chalcodes.weaponm.gui;

import java.util.UUID;

import javax.swing.JPanel;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;

class Terminal {
	
	private final DefaultSingleCDockable dockable;

	Terminal() {
		// TODO set up JTX terminal
		final JPanel fakePanel = new JPanel();
		String title = Strings.getString("TITLE_TERMINAL");
		// TODO remove bogus stuff below
		String id = UUID.randomUUID().toString();
		dockable = new DefaultSingleCDockable(id, title + ' ' + id, fakePanel);
		dockable.setFocusComponent(fakePanel);
		dockable.setDefaultLocation(ExtendedMode.MINIMIZED, CLocation.base().minimalSouth());
		dockable.setCloseable(true);
		// TODO set icon
	}
	
	DefaultSingleCDockable getDockable() {
		return dockable;
	}
	
}
