package com.chalcodes.weaponm.gui;

import javax.swing.JButton;
import javax.swing.UIManager;

import com.chalcodes.weaponm.gui.action.ActionManager;

public class ButtonFactory {

	public static JButton newYesButton() {
		return newButton("OptionPane.yesButtonText", "OptionPane.yesButtonMnemonic", "BUTTON_YES");
	}
	
	public static JButton newNoButton() {
		return newButton("OptionPane.noButtonText", "OptionPane.noButtonMnemonic", "BUTTON_NO");
	}
	
	public static JButton newOkButton() {
		return newButton("OptionPane.okButtonText", "OptionPane.okButtonMnemonic", "BUTTON_OK");
	}
	
	public static JButton newCancelButton() {
		return newButton("OptionPane.cancelButtonText", "OptionPane.cancelButtonMnemonic", "BUTTON_CANCEL");
	}
	
	private static JButton newButton(String textKey, String mnemonicKey, String fallbackKey) {
		JButton button = new JButton();
		String text = UIManager.getString(textKey);
		if(text != null) {
			button.setText(text);
			String mnem = UIManager.getString(mnemonicKey);
			if (mnem != null) {
				// FIXME is this right?
				button.setMnemonic(Integer.parseInt(mnem));
			}
		}
		else {
			ActionManager.setText(button, fallbackKey);
		}
		return button;
	}
	
	private ButtonFactory() {}	
}
