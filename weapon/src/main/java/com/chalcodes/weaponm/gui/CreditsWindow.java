package com.chalcodes.weaponm.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CreditsWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(CreditsWindow.class);

	CreditsWindow(ImageIcon icon) {
		super("Credits");
		setIconImage(icon.getImage());
		URL url = getClass().getResource("/com/chalcodes/weaponm/Credits.html");
		try {
			JEditorPane editorPane = new JEditorPane(url);
			editorPane.setEditable(false);
			editorPane.setFocusable(false);
			editorPane.setPreferredSize(new Dimension(650, 550));
			
			editorPane.addHyperlinkListener(new HyperlinkListener() {

				@Override
				public void hyperlinkUpdate(HyperlinkEvent e) {
					if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (Exception ex) {
						log.error("error opening system browser", ex);
					}					
				}
			});
			
			add(editorPane);
		} catch (IOException e) {
			log.error("shouldn't happen", e);
		}
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		pack();
		setResizable(false);
	}	
}
