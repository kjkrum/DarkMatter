package com.chalcodes.weaponm.gui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;

import com.chalcodes.jtx.Display;
import com.chalcodes.jtx.ScrollbackBuffer;
import com.chalcodes.jtx.SoftFont;
import com.chalcodes.jtx.StickyScrollPane;
import com.chalcodes.jtx.VgaSoftFont;
import com.chalcodes.jtx.extensions.SelectionControl;
import com.chalcodes.weaponm.AppSettings;
import com.chalcodes.weaponm.Debug;
import com.chalcodes.weaponm.emulation.Emulation;
import com.chalcodes.weaponm.emulation.EmulationParser;
import com.chalcodes.weaponm.emulation.lexer.EmulationLexer;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.NetworkStatus;
import com.chalcodes.weaponm.event.WeaponEvent;

class Terminal {
	private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private final EventSupport eventSupport;
	private final DefaultSingleCDockable dockable;
	private final ScrollbackBuffer buffer;
	private final SoftFont font;
	private final Display display;
	private final EmulationLexer lexer;
	private final EmulationParser parser;
	private final Emulation emulation;
	
	Terminal(EventSupport eventSupport) throws IOException, ClassNotFoundException {
		buffer = new ScrollbackBuffer(
				AppSettings.getBufferColumns(),
				AppSettings.getBufferLines());
		font = new VgaSoftFont();
		display = new Display(buffer, font, AppSettings.getBufferColumns(), 0);
		buffer.addBufferObserver(display);
		lexer = new EmulationLexer();
		parser = new EmulationParser(buffer);
		lexer.addEventListener(parser);
		emulation = new Emulation(lexer);
		this.eventSupport = eventSupport;
		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				WeaponEvent evt = (WeaponEvent) e;
				switch(evt.getType()) {
				case TEXT_RECEIVED:
					String text = (String) evt.getNewValue();
					emulation.write(text);
					break;
				case NETWORK_STATUS:
					NetworkStatus oldStatus = (NetworkStatus) evt.getOldValue();
					NetworkStatus newStatus = (NetworkStatus) evt.getNewValue();
					if(oldStatus == NetworkStatus.CONNECTED && newStatus == NetworkStatus.DISCONNECTED) {
						String msg = Strings.getString("DISCONNECTED");
						emulation.write("\r\n\r\n\033[1;31m<< " + msg + " >>\033[0m\r\n");
					}
					break;					
				}
			}
		};
		eventSupport.addPropertyChangeListener(EventType.TEXT_RECEIVED, listener);
		eventSupport.addPropertyChangeListener(EventType.NETWORK_STATUS, listener);
		
		StickyScrollPane scrollPane = new StickyScrollPane(display);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setBackground(Color.DARK_GRAY);
		scrollPane.setAutoscrolls(true);
		
		// TODO save this ref and do stuff with it
		new SelectionControl(scrollPane.getViewport(), eventSupport);
		
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
		WeaponEvent event = new WeaponEvent(EventType.TEXT_TYPED, null, string);
		eventSupport.firePropertyChange(event);
	}
	
	private void send(char c) {
		send(new StringBuilder().append(c).toString());
	}	
}
