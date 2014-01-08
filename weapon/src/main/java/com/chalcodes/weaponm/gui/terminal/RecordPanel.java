package com.chalcodes.weaponm.gui.terminal;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.I18n;

class RecordPanel {
	private final JPanel panel;
	private final EventSupport eventSupport;
	private final PropertyChangeListener listener;
	private boolean registered;
	private final StringBuilder sb = new StringBuilder();
	
	RecordPanel(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
		
		ImageIcon icon = new ImageIcon(getClass().getResource("/com/chalcodes/weaponm/icons/record24.gif"));
		JLabel iconLabel = new JLabel(icon);
		JLabel textLabel = new JLabel(I18n.getString("RECORDING"));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		
		panel = new JPanel(new GridBagLayout());
		panel.add(iconLabel, c);
		++c.gridx;
		c.weightx = 1.0;
		panel.add(textLabel, c);
		
		listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				sb.append(e.getNewValue());
			}
		};
	}
	
	void setRecording(boolean recording) {
		if(recording && !registered) {
			eventSupport.addPropertyChangeListener(EventType.TEXT_TYPED, listener);
			registered = true;
		}
		else if(!recording && registered) {
			eventSupport.removePropertyChangeListener(EventType.TEXT_TYPED, listener);
			registered = false;
		}
	}
	
	String getText() {
		return sb.toString();
	}
	
	void reset() {
		sb.setLength(0);
	}
	
	JComponent getComponent() {
		return panel;
	}
}
