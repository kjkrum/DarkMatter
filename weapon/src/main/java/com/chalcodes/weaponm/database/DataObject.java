package com.chalcodes.weaponm.database;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

/**
 * Base class of all database objects.  All setters should check existing
 * values and call {@link #fireChanged()} to report changes.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
abstract public class DataObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient List<DataChangeListener> changeListeners;
	private transient Database db;
	
	void setDatabase(Database db) {
		this.db = db;
	}
	
	// TODO give scripts access through a proxy
	void addChangeListener(DataChangeListener listener) {
		if(listener != null) {
			if(changeListeners == null) {
				changeListeners = new LinkedList<DataChangeListener>();
			}
			changeListeners.add(listener);
		}
	}

	// TODO give scripts access through a proxy
	void removeChangeListener(DataChangeListener listener) {
		if(changeListeners != null) {
			changeListeners.remove(listener);
			if(changeListeners.isEmpty()) {
				changeListeners = null;
			}
		}
	}
	
	void fireChanged() {
		if(SwingUtilities.isEventDispatchThread()) {
			if(changeListeners != null) {
				for(DataChangeListener listener : changeListeners) {
					listener.dataChanged(this);                                                                                                                                                                                                                                                                                                   
				}
			}
			if(db != null) {
				db.fireChanged(this);
				db.fireChanged();
			}
		}
		else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fireChanged();
				}
			});
		}
	}
}
