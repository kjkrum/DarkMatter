package com.chalcodes.weaponm.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.action.ActionManager;

class BurstPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final JComponent focusOnHide;
	
	BurstPanel(final EventSupport eventSupport, final JComponent focusOnHide) {
		super(new BorderLayout());
		this.focusOnHide = focusOnHide;
		
		final JTextField inputField = new JTextField();
		
		// the action that does the real work
		final AbstractAction sendAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = inputField.getText();
				if(!input.isEmpty()) {
					input = input.replaceAll("\\^[mM]", "\r\n");
					eventSupport.firePropertyChange(EventType.TEXT_TYPED, null, input);
				}
				removeSelf();		
			}
		};
		ActionManager.setText(sendAction, "BUTTON_SEND");
		final JButton sendButton = new JButton(sendAction);
		
		// cancel action - bound to escape
		final AbstractAction cancelAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				removeSelf();				
			}
		};
		
		// click action - fires send action by simulating click on send button
		// could just bind send action to enter key, but this is cosmetically nicer
		final AbstractAction clickAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				sendButton.doClick();	
			}
		};
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "click");
		getActionMap().put("click", clickAction);
		
		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "cancel");
		getActionMap().put("cancel", cancelAction);
		
		// focus the input field when this panel is added to a container
		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				Container parent = BurstPanel.this.getParent(); 
				if(parent != null) {
					inputField.requestFocusInWindow();
				}
			}
		});

		add(inputField, BorderLayout.CENTER);
		add(sendButton, BorderLayout.EAST);
	}
	
	private void removeSelf() {
		Container parent = BurstPanel.this.getParent();	
		parent.remove(BurstPanel.this);
		parent.validate();
		focusOnHide.requestFocusInWindow();		
	}

}
