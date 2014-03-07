package com.chalcodes.weaponm.gui;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.chalcodes.weaponm.database.LoginOptions;

public class LoginOptionsDialog {
	// http://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html#stayup

	public static int showDialog(Frame parent, LoginOptions options) {
		final LoginOptionsPanel panel = new LoginOptionsPanel(options);
		final JOptionPane optionPane = new JOptionPane(panel,
				JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);  
		final JDialog dialog = new JDialog(parent,
				I18n.getString("TITLE_LOGIN_OPTIONS"), true);
		dialog.setContentPane(optionPane);
		
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				String prop = e.getPropertyName();
				if (dialog.isVisible() && e.getSource() == optionPane
						&& prop.equals(JOptionPane.VALUE_PROPERTY)) {
					if(e.getNewValue().equals(JOptionPane.OK_OPTION)) {
						try {
							panel.validateFields();
							panel.saveFields();
							dialog.setVisible(false);
						}
						catch(IllegalStateException ex) {
							optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
							JOptionPane.showMessageDialog(dialog,
									ex.getMessage(),
									I18n.getString("TITLE_ERROR"),
									JOptionPane.ERROR_MESSAGE);
						}
					}
					else if(e.getNewValue().equals(JOptionPane.CANCEL_OPTION)) {
						dialog.setVisible(false);
					}
				}
			}
		});
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);

		final Object value = optionPane.getValue();
		int ret;
		if(value == null) {
			ret = JOptionPane.CLOSED_OPTION;
		}
		else if(value instanceof Integer) {
			ret = (Integer) value;
		}
		else {
			ret = JOptionPane.UNDEFINED_CONDITION;
		}
		return ret;
	}
}
