package com.chalcodes.weaponm.database;

import java.io.Serializable;

/**
 * Base class of all database objects.  All setters should check existing
 * values and call {@link #fireChanged()} to report changes.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
abstract public class DataObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient Database db;
	
	void setDatabase(Database db) {
		if(db != null) {
			throw new IllegalStateException("database already set");
		}
		this.db = db;
	}
	
	Database getDatabase() {
		return db;
	}
	
	void fireChanged() {
		db.getManager().fireChanged(this);
	}
}
