package com.chalcodes.weaponm.gui.action;

import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.database.DatabaseManager;
import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.EventListener;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

/**
 * Maintains collections of actions and UI elements that are enabled and
 * disabled in response to events.  Also generates the main window's menu bar.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class ActionManager implements EventListener {
	private static final Logger log =
			LoggerFactory.getLogger(ActionManager.class.getSimpleName());
	private final JMenuBar menuBar = new JMenuBar();
	
	private final Set<AbstractAction> enableOnLoad = new HashSet<AbstractAction>(); // disable on unload
	private final Set<JMenu> enableOnLoadMenus = new HashSet<JMenu>(); // because AbstractAction and JMenu have no ancestor in common that includes setEnabled(...)
	private final Set<AbstractAction> enableOnConnect = new HashSet<AbstractAction>(); // disable on disconnect
	private final Set<AbstractAction> disableOnConnect = new HashSet<AbstractAction>(); // enable on disconnect
	
	public ActionManager(EventSupport eventSupport, DatabaseManager dbm) {
		// TODO moar
		populateMenuBar();
		eventSupport.addEventListener(this, EventType.DB_OPENED);
	}

	private void populateMenuBar() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(Event event) {
		// TODO Auto-generated method stub
		
	}
}
