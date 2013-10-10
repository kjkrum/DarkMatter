package com.chalcodes.weaponm.gui;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JScrollPane;

import krum.jtx.SoftFont;
import krum.jtx.StickyScrollPane;
import krum.jtx.SwingDisplay;
import krum.jtx.SwingScrollbackBuffer;
import krum.jtx.VGASoftFont;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;

import com.chalcodes.weaponm.AppSettings;

class Terminal {
	
	private final DefaultSingleCDockable dockable;
	private final SwingScrollbackBuffer buffer;
	private final SoftFont font;
	private final SwingDisplay display;
	
	Terminal() throws IOException {
		buffer = new SwingScrollbackBuffer(
				AppSettings.getBufferColumns(),
				AppSettings.getBufferLines());
		font = new VGASoftFont();
		display = new SwingDisplay(buffer, font, AppSettings.getBufferColumns(), 0);
		buffer.addBufferObserver(display);
		
		// TODO create parser, connect to buffer
		
		StickyScrollPane scrollPane = new StickyScrollPane(display);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setBackground(Color.BLACK);
		
		String title = Strings.getString("TITLE_TERMINAL");
		dockable = new DefaultSingleCDockable("TERMINAL", title, scrollPane);
		dockable.setFocusComponent(scrollPane);
		dockable.setDefaultLocation(ExtendedMode.MINIMIZED, CLocation.base().minimalSouth());
		dockable.setCloseable(true);
		// TODO set icon
	}
	
	DefaultSingleCDockable getDockable() {
		return dockable;
	}
	
}
