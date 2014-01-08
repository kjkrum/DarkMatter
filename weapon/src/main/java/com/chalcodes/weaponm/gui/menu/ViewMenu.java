package com.chalcodes.weaponm.gui.menu;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.menu.SingleCDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.I18n;

class ViewMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	ViewMenu(CControl dockControl, EventSupport eventSupport) {
		I18n.setText(this, "MENU_VIEW");
		// anonymous extensions give the menu icons a consistent size
		final SingleCDockableListMenuPiece piece = new SingleCDockableListMenuPiece(dockControl) {
			@Override
			protected Item create(final Dockable dockable) {
				return new Item(dockable) {
					private static final long serialVersionUID = 1L;
					{
					    Icon icon = dockable.getTitleIcon();
					    if(icon instanceof ImageIcon) {
					    	Image image = ((ImageIcon) icon).getImage();
					    	// TODO can the preferred icon size be obtained from the look & feel?
					    	Image scaled = image.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
					    	ImageIcon scaledIcon = new ImageIcon(scaled);
					    	setIcon(scaledIcon);
					    }
					}
				};
			}
		};
		final RootMenuPiece root = new RootMenuPiece(this) {
			@Override
			protected void updateEnabled() {
				// anonymous extension prevents docking library changing the menu enabled state
			}
		};
		root.setDisableWhenEmpty(false);
		root.add(piece);
		//viewMenu.addSeparator();
		// TODO "save", "reset", "make default"
		
		// enable on load
		eventSupport.addPropertyChangeListener(EventType.DATABASE_OPEN, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				boolean open = (Boolean) e.getNewValue();
				setEnabled(open);
			}
		});
		setEnabled(false);
	}
}
