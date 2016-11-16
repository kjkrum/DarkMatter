package com.chalcodes.weaponm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import java.awt.Image;

/**
 * Describes a plugin's dockable UI.
 *
 * @author Kevin Krumwiede
 */
public class Dockable {
	/* The point of this class is to prevent plugins from depending on a
	 * particular docking framework. */

	public static class Builder {
		@Nonnull private final String mTitle;
		@Nonnull private final JComponent mComponent;
		@Nullable private Image mIcon;

		public Builder(@Nonnull final String title, @Nonnull final JComponent component) {
			mTitle = title;
			mComponent = component;
		}

		public Builder setIcon(@Nonnull final Image icon) {
			mIcon = icon;
			return this;
		}

		public Dockable build() {
			return new Dockable(mTitle, mComponent, mIcon);
		}
	}

	@Nonnull private final String mTitle;
	@Nonnull private final JComponent mComponent;
	@Nullable private final Image mIcon;

	private Dockable(final String title, final JComponent component, @Nullable final Image icon) {
		if(title == null || component == null) {
			throw new NullPointerException();
		}
		if(title.isEmpty()) {
			throw new IllegalArgumentException();
		}
		mTitle = title;
		mComponent = component;
		mIcon = icon;
	}

	// TODO factory for whatever docking-frames class we want to use

//	private void addDummy(@Nonnull final String title) {
//		final JPanel panel = new JPanel();
//		panel.setSize(300, 200);
//		final DefaultSingleCDockable dockable = new DefaultSingleCDockable(title, title, panel);
//		dockable.setSingleTabShown(false);
//		dockable.setMaximizable(false);
//		mDock.addDockable(dockable);
//		dockable.setVisible(true);
//	}
}
