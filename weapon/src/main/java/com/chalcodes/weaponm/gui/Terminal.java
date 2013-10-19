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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;

import com.chalcodes.weaponm.AppSettings;
import com.chalcodes.weaponm.Debug;
import com.chalcodes.weaponm.emulation.Emulation;
import com.chalcodes.weaponm.emulation.EmulationParser;
import com.chalcodes.weaponm.emulation.lexer.EmulationLexer;
import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.EventListener;
import com.chalcodes.weaponm.event.EventParam;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

class Terminal {
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
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
		EventListener listener = new EventListener() {
			@Override
			public void onEvent(Event e) {
				switch(e.getType()){
				case TEXT_RECEIVED:
					String text = (String) e.getParam(EventParam.TEXT);
					emulation.write(text);
					break;
				case NET_DISCONNECTED:
					String msg = Strings.getString("DISCONNECTED");
					emulation.write("\r\n\r\n\033[1;31m<< " + msg + " >>\033[0m\r\n");
					break;
				}
			}			
		};
		eventSupport.addEventListener(listener, EventType.TEXT_RECEIVED);
		eventSupport.addEventListener(listener, EventType.NET_DISCONNECTED);
		
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
					if(Debug.KEY_LISTENER) {
						log.debug("key typed: {}", c);
					}
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
		send("" + c);
	}	
}
