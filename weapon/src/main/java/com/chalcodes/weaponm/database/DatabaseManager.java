package com.chalcodes.weaponm.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.LogName;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

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
	
	public boolean isDatabaseOpen() {
		return database != null;
	}
	
	public Database create(File file) throws IOException {
		File lockFile = new File(file.getPath() + ".lock");
		if(!lockFile.createNewFile()) {
			throw new IOException("Lock file " + lockFile.getPath() + " exists.");
		}		
		Database database = new Database();
		save(file, database);
		close();
		this.file = file;
		this.database = database;
		// TODO parser = new DataParser(weapon, lexer, database);
		eventSupport.dispatchEvent(EventType.DB_OPENED);
		log.info("database created in {}", file.getPath());
		return database;
	}
	
	public Database open(File file) throws IOException {
		File lockFile = new File(file.getPath() + ".lock");
		if(!lockFile.createNewFile()) {
			throw new IOException("Lock file " + lockFile.getPath() + " exists.");
		}		
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			try {
				this.database = (Database) in.readObject();
			}
			finally { in.close(); }
			this.file = file;
			// TODO parser = new DataParser(weapon, lexer, database);
			eventSupport.dispatchEvent(EventType.DB_OPENED);
			if(database.isInitialized()) {
				// TODO weapon.gui.firePropertyChange(GUI.DATABASE_INITIALIZED, database, true);
			}
			// TODO if(database.getStardockSector() != null) {
			//	weapon.gui.firePropertyChange(GUI.STARDOCK_DISCOVERED, null, database.getStardockSector().getNumber());
			//}
			// TODO if(database.getYou().getSector() != null) {
			//	weapon.gui.firePropertyChange(GUI.SHIP_SECTOR, null, database.getYou().getSector().getNumber());
			//}
			log.info("database loaded from {}", file.getPath());
			// TODO weapon.gui.updateTitles(database.getLoginOptions().getTitle());
			return database;
		} catch(Exception e) {
			lockFile.delete();
			if(e instanceof IOException) throw (IOException) e;
			else { // ClassNotFoundException, ClassCastException
				throw new IOException("The file \"" + file.getPath() + "\" is not a compatible Weapon M database.", e); 
			}
		}
	}
	
	synchronized public void save() throws IOException {
		save(file, database);
		log.info("database saved");
	}
	
	synchronized public void saveAs(File newFile) throws IOException {
		// create new lock file
		File lockFile = new File(newFile.getPath() + ".lock");
		if(!lockFile.createNewFile()) {
			throw new IOException("Lock file " + lockFile.getPath() + " exists.");
		}
		save(newFile, database);
		// delete old lock file
		new File(file.getPath() + ".lock").delete();
		file = newFile;
		log.info("database saved as '{}'", file.getPath());
	}
	
	synchronized public void saveCopy(File copyFile) throws IOException {
		File lockFile = new File(file.getPath() + ".lock");
		if(!lockFile.createNewFile()) {
			throw new IOException("Lock file " + lockFile.getPath() + " exists.");
		}
		save(copyFile, database);
		lockFile.delete();
		log.info("database copied to {}", copyFile.getPath());
	}
	
	protected void save(File file, Database database) throws IOException {
		if(file.isDirectory()) throw new IOException("Target file is a directory.");
		File tmpFile = new File(file.getPath() + ".tmp");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpFile));
		try {
			out.writeObject(database);
		} finally {
			out.close();
		}
		if(file.exists() && !file.delete()) {
			throw new IOException("Save incomplete: could not delete old file.");
		}
		if(!tmpFile.renameTo(file)) {
			throw new IOException("Save incomplete: could not rename temp file.");
		}
	}
	
	/**
	 * Nulls database references and shuts down network and scripts.
	 */
	public void close() {
		if(file != null) {
			// TODO weapon.scripts.unloadAll();
			// TODO weapon.network.disconnect();
			new File(file.getPath() + ".lock").delete();
			file = null;
			database = null;
			// TODO lexer.removeEventListener(parser);
			// TODO parser = null;
			// TODO weapon.gui.firePropertyChange(GUI.DATABASE_INITIALIZED, null, false);
			eventSupport.dispatchEvent(EventType.DB_CLOSED);
			log.info("database closed");
		}
		
	}
	
	// export xml
	// import xml
	// merge

	

}
