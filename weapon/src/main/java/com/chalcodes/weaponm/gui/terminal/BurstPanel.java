package com.chalcodes.weaponm.gui.terminal;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

class BurstPanel {
	private static final Logger log = LoggerFactory.getLogger(BurstPanel.class.getSimpleName());
	private final JPanel panel = new JPanel(new GridBagLayout());
	private final JTextField inputField;
	
	BurstPanel(final EventSupport eventSupport, final Terminal terminal) {
		// lay out the components
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		
		inputField = new JTextField();
		inputField.setEditable(true);
		c.weightx = 1.0;
		panel.add(inputField, c);
		c.weightx = 0.0;
		
		++c.gridx;
		ImageIcon repeatIcon = new ImageIcon(getClass().getResource("/com/chalcodes/weaponm/icons/repeat16.png"));
		panel.add(new JLabel(repeatIcon));
		
		++c.gridx;
		// max value of 999 reduces the space taken up by the spinner
		SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 999, 1);
		final JSpinner repeatSpinner = new JSpinner(spinnerModel);
		panel.add(repeatSpinner, c);
		
		++c.gridx;
		final JButton sendButton = new JButton();
		sendButton.setHideActionText(true);
		panel.add(sendButton, c);
		
		// click action - fires send action by simulating a click on the send button
		// could bind send action directly to enter key, but this is a nice touch
		final AbstractAction clickAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				sendButton.doClick();	
			}
		};
		panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "click");
		panel.getActionMap().put("click", clickAction);
		
		// the action that does the real work
		final AbstractAction sendAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			
			{
				// this is here because the button's attributes are wiped when an action is set
				this.putValue(LARGE_ICON_KEY, new ImageIcon(getClass().getResource("/com/chalcodes/weaponm/icons/send16.png")));
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				String input = inputField.getText();
				if(!input.isEmpty()) {
					input = input.replaceAll("\\^[mM]", "\r\n");
					int repeat = (Integer) repeatSpinner.getValue();
					for(int i = 0; i < repeat; ++i) {
						eventSupport.firePropertyChange(EventType.TEXT_TYPED, null, input);
					}
					terminal.removePanels();
				}
			}
		};
		sendButton.setAction(sendAction);
		
		final AbstractAction insertNewlineAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(inputField.isFocusOwner()) {
					try {
						inputField.getDocument().insertString(inputField.getCaretPosition(), "^M", null);
					}
					catch (BadLocationException ex) {
						log.warn("this shouldn't happen", ex);
					}
				}
			}			
		};
		panel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.SHIFT_MASK, true), "insertNewline");
		panel.getActionMap().put("insertNewline", insertNewlineAction);
		
		/*
		 * this was an attempt to solve the problem of focus changing to the
		 * terminal when the dockable's container hierarchy changes while the
		 * burst panel is visible.  this didn't help.  but anyway, it's a
		 * minor problem.
		 */
//		panel.addAncestorListener(new AncestorAdapter() {
//			@Override
//			public void ancestorAdded(AncestorEvent e) {
//				if(panel.getParent() != null) {
//					SwingUtilities.invokeLater(new Runnable() {
//						@Override
//						public void run() {
//							inputField.requestFocusInWindow();
//						}
//					});
//				}
//			}
//		});
	}
	
	void focusInputField() {
		inputField.requestFocusInWindow();
		inputField.selectAll();
	}
	
	void setBurst(String text) {
		inputField.setText(text);
	}
	
	/**
	 * Returns the UI component for this burst panel.
	 * 
	 * @return the component
	 */
	JComponent getComponent() {
		return panel;
	}
}
