package com.chalcodes.weaponm.database;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.LogName;
import com.chalcodes.weaponm.event.EventSupport;

/**
 * Manages serialization and deserialization of databases.  This class
 * primarily exists so that {@link Database} doesn't have to expose methods
 * like <tt>close()</tt>.  Although this class is public, scripts cannot
 * normally obtain a reference to the Weapon's instance of it.  Its methods
 * are intended to be called only in the UI thread.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class DatabaseManager {
	private final Logger log = LoggerFactory.getLogger(LogName.forObject(this));
	private final EventSupport eventSupport;
	private File file;
	private Database database;
	
	/**
	 * 
	 * @param eventSupport the GUI's event dispatcher
	 */
	public DatabaseManager(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}
	
	// create
	// open
	// save
	// save as
	// save copy
	// export xml
	// import xml
	// merge
	// close
	

}
