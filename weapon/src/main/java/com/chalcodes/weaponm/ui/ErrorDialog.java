package com.chalcodes.weaponm.ui;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JOptionPane;
import java.awt.Component;

/**
 * Displays an error dialog.
 *
 * @author Kevin Krumwiede
 */
public class ErrorDialog {
	private ErrorDialog() {}

	public static void show(@Nullable final Component parent,
	                        @Nonnull final String message,
	                        final boolean fatal) {
		JOptionPane.showOptionDialog(parent,
				WordUtils.wrap(message, 80),
				"Error",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null, null, null);
		if(fatal) {
			System.exit(1);
		}
	}

	public static void show(@Nullable final Component parent,
	                        @Nonnull final Throwable error,
	                        final boolean fatal) {
		show(parent, ExceptionUtils.getRootCauseMessage(error), fatal);
	}

}
