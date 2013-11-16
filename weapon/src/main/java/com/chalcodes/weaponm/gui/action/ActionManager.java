package com.chalcodes.weaponm.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.menu.SingleCDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.DatabaseStatus;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.NetworkStatus;
import com.chalcodes.weaponm.event.WeaponEvent;
import com.chalcodes.weaponm.gui.Gui;
import com.chalcodes.weaponm.gui.Strings;
import com.chalcodes.weaponm.network.NetworkManager;

/**
 * Maintains collections of actions and UI elements that are enabled and
 * disabled in response to events.  Also generates the main window's menu bar.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class ActionManager implements PropertyChangeListener {
	//private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
	private final JMenuBar menuBar = new JMenuBar();
	private final Set<AbstractAction> enableOnLoad = new HashSet<AbstractAction>(); // disable on unload
	private final Set<JMenu> enableOnLoadMenus = new HashSet<JMenu>(); // because AbstractAction and JMenu have no common ancestor that includes setEnabled(...)
	private final Set<AbstractAction> enableOnConnect = new HashSet<AbstractAction>(); // disable on disconnect
	private final Set<AbstractAction> disableOnConnect = new HashSet<AbstractAction>(); // enable on disconnect
	
	public ActionManager(Gui gui, EventSupport eventSupport, DatabaseManager dbm, NetworkManager network, CControl dockControl) {
		populateMenus(gui, eventSupport, dbm, network, dockControl);
		eventSupport.addPropertyChangeListener(EventType.DATABASE_STATUS, this);
		eventSupport.addPropertyChangeListener(EventType.NETWORK_STATUS, this);
	}
	
	public JMenuBar getMenuBar() {
		return menuBar;
	}

	private void populateMenus(Gui gui, EventSupport eventSupport, DatabaseManager dbm, NetworkManager network, CControl dockControl) {
		final JMenu dbMenu = new JMenu();
		setText(dbMenu, "MENU_DATABASE");
		AbstractAction openAction = new OpenDatabaseAction(gui, dbm);
		openAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		dbMenu.add(openAction);
		AbstractAction newAction = new NewDatabaseAction(gui, dbm);
		newAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		dbMenu.add(newAction);
		AbstractAction saveAction = new SaveDatabaseAction(gui, dbm);
		enableOnLoad.add(saveAction);
		saveAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		dbMenu.add(saveAction);
		AbstractAction saveAsAction = new SaveAsDatabaseAction(gui, dbm);
		enableOnLoad.add(saveAsAction);
		dbMenu.add(saveAsAction);
		AbstractAction saveCopyAction = new SaveCopyDatabaseAction(gui, dbm);
		enableOnLoad.add(saveCopyAction);
		dbMenu.add(saveCopyAction);
		AbstractAction closeAction = new CloseDatabaseAction(gui);
		enableOnLoad.add(closeAction);
		dbMenu.add(closeAction);
		menuBar.add(dbMenu);
		
		final JMenu viewMenu = new JMenu();
		setText(viewMenu, "MENU_VIEW");
		final SingleCDockableListMenuPiece piece = new SingleCDockableListMenuPiece(dockControl);
		final RootMenuPiece root = new RootMenuPiece(viewMenu) {
			@Override
			protected void updateEnabled() {
				// do nothing
			}
		};
		root.setDisableWhenEmpty(false);
		root.add(piece);
		//viewMenu.addSeparator();
		// TODO "save" saves to database; "make default" saves to config
		enableOnLoadMenus.add(viewMenu);
		menuBar.add(viewMenu);
		
		final JMenu netMenu = new JMenu();
		setText(netMenu, "MENU_NETWORK");
		enableOnLoadMenus.add(netMenu);
		AbstractAction connectAction = new ConnectAction(dbm, network);
		disableOnConnect.add(connectAction);
		netMenu.add(connectAction);
		AbstractAction disconnectAction = new DisconnectAction(network);
		enableOnConnect.add(disconnectAction);
		netMenu.add(disconnectAction);
		netMenu.addSeparator();
		AbstractAction optionsAction = new ShowLoginOptionsDialogAction(gui, dbm);
		disableOnConnect.add(optionsAction);
		netMenu.add(optionsAction);
		menuBar.add(netMenu);
		
		final JMenu weaponMenu = new JMenu();
		setText(weaponMenu, "MENU_WEAPON");
		weaponMenu.add(new ShowAboutDialogAction(gui));
		weaponMenu.add(new ShowCreditsWindowAction(gui));
		weaponMenu.add(new WebsiteAction());
		weaponMenu.addSeparator();
		AbstractAction exitAction = new ExitAction(gui);
		exitAction.putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		weaponMenu.add(exitAction);
		menuBar.add(weaponMenu);
		
		// set initial enabled states
		configureNoDatabase();
		configureNoConnection();
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if(e instanceof WeaponEvent) {
			WeaponEvent evt = (WeaponEvent) e;
			switch(evt.getType()) {
			case DATABASE_STATUS:
				DatabaseStatus dbStatus = (DatabaseStatus) evt.getNewValue();
				if(dbStatus == DatabaseStatus.OPEN) {
					for(AbstractAction action : enableOnLoad) {
						action.setEnabled(true);
					}
					for(JMenu menu : enableOnLoadMenus) {
						menu.setEnabled(true);
					}
				}
				else if(dbStatus == DatabaseStatus.CLOSED) {
					configureNoDatabase();
				}
				break;
			case NETWORK_STATUS:
				NetworkStatus netStatus = (NetworkStatus) evt.getNewValue();
				if(netStatus == NetworkStatus.CONNECTED) {
					for(AbstractAction action : enableOnConnect) {
						action.setEnabled(true);
					}
					for(AbstractAction action : disableOnConnect) {
						action.setEnabled(false);
					}
				}
				else if(netStatus == NetworkStatus.DISCONNECTED) {
					configureNoConnection();
				}
				break;
			}
		}
		else {
			// ???
		}
	}
	
	private void configureNoDatabase() {
		for(AbstractAction action : enableOnLoad) {
			action.setEnabled(false);
		}
		for(JMenu menu : enableOnLoadMenus) {
			//log.debug("disabling {}", menu);
			menu.setEnabled(false);
		}
	}
	
	private void configureNoConnection() {
		for(AbstractAction action : enableOnConnect) {
			action.setEnabled(false);
		}
		for(AbstractAction action : disableOnConnect) {
			action.setEnabled(true);
		}	
	}
	
	/**
	 * Sets the text and keyboard mnemonic for an action.
	 * 
	 * @param action the action
	 * @param key a key constant from {@link com.chalcodes.weaponm.gui.Strings}
	 */
	static void setText(AbstractAction action, String key) {
		String raw = Strings.getString(key);
		String stripped = raw.replace("_", "");
		action.putValue(Action.NAME, stripped);
		int idx = raw.indexOf('_');
		if(idx != -1 && idx < stripped.length()) {
			action.putValue(Action.MNEMONIC_KEY, KeyEvent.getExtendedKeyCodeForChar(stripped.charAt(idx)));
		}
	}
	
	/**
	 * Sets the text and keyboard mnemonic for a button, menu, or menu item.
	 * 
	 * @param button the button
	 * @param key a key constant from {@link com.chalcodes.weaponm.gui.Strings}
	 */
	public static void setText(AbstractButton button, String key) {
		String raw = Strings.getString(key);
		String stripped = raw.replace("_", "");
		button.setText(stripped);
		int idx = raw.indexOf('_');
		if(idx != -1 && idx < stripped.length()) {
			button.setMnemonic(stripped.charAt(idx));
		}
	}
}
