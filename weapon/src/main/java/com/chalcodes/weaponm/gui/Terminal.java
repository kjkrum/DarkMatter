package com.chalcodes.weaponm.gui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import com.chalcodes.weaponm.emulation.Emulation;
import com.chalcodes.weaponm.emulation.EmulationParser;
import com.chalcodes.weaponm.emulation.lexer.EmulationLexer;
import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.EventListener;
import com.chalcodes.weaponm.event.EventParam;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

class Terminal {
	private final EventSupport eventSupport;
	private final DefaultSingleCDockable dockable;
	private final SwingScrollbackBuffer buffer;
	private final SoftFont font;
	private final SwingDisplay display;
	private final EmulationLexer lexer;
	private final EmulationParser parser;
	private final Emulation emulation;
	
	Terminal(EventSupport eventSupport) throws IOException, ClassNotFoundException {
		this.eventSupport = eventSupport;
		buffer = new SwingScrollbackBuffer(
				AppSettings.getBufferColumns(),
				AppSettings.getBufferLines());
		font = new VGASoftFont();
		display = new SwingDisplay(buffer, font, AppSettings.getBufferColumns(), 0);
		buffer.addBufferObserver(display);
		lexer = new EmulationLexer();
		parser = new EmulationParser(buffer);
		lexer.addEventListener(parser);
		emulation = new Emulation(lexer);
		
		eventSupport.addEventListener(new EventListener() {
			@Override
			public void onEvent(Event e) {
				String text = (String) e.getParam(EventParam.TEXT);
				emulation.write(text);	
			}			
		}, EventType.TEXT_RECEIVED);
		
		StickyScrollPane scrollPane = new StickyScrollPane(display);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setBackground(Color.BLACK);
		
		String title = Strings.getString("TITLE_TERMINAL");
		dockable = new DefaultSingleCDockable("TERMINAL", title, scrollPane);
		dockable.setFocusComponent(scrollPane);
		dockable.setDefaultLocation(ExtendedMode.MINIMIZED, CLocation.base().minimalSouth());
		dockable.setCloseable(true);
		scrollPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				switch(c) {
				case KeyEvent.CHAR_UNDEFINED:
					break;
				case KeyEvent.VK_ENTER:
					send("\r\n");
					break;
				case KeyEvent.VK_TAB:
					send('\t');
					break;
				default:
					send(c);
					break;
				}
			}
		});
		// TODO set icon
	}
	
	DefaultSingleCDockable getDockable() {
		return dockable;
	}
	
	private void send(String string) {
		Event event = new Event(EventType.TEXT_TYPED, EventParam.TEXT, string);
		Terminal.this.eventSupport.dispatchEvent(event);
	}
	
	private void send(char c) {
		Event event = new Event(EventType.TEXT_TYPED, EventParam.TEXT, "" + c);
		Terminal.this.eventSupport.dispatchEvent(event);		
	}	
}
