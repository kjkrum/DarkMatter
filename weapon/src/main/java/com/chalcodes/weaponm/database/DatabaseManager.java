package com.chalcodes.weaponm.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.gui.Strings;

/**
 * Manages serialization and deserialization of databases. This class primarily
 * exists so that {@link Database} doesn't have to expose methods like
 * <tt>close()</tt>. Although this class is public, scripts cannot normally
 * obtain a reference to the Weapon's instance of it. Its methods are intended
 * to be called only in the UI thread.
 * 
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class DatabaseManager {
	private final Logger log = LoggerFactory.getLogger(DatabaseManager.class.getSimpleName());
	private final EventSupport eventSupport;
	private File file;
	private Database database;
	
	/* 
	 * none of these methods are interactive, so will not block network
	 * only need to sync in private save method
	 * network thread won't exist during open/create and will be killed by close
	 * database reference doesn't change even if file name changes
	 */

	/**
	 * Creates a new <tt>DatabaseManager</tt> using the specified
	 * <tt>EventSupport</tt>.
	 * 
	 * @param eventSupport the GUI's event dispatcher
	 */
	public DatabaseManager(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}

	/**
	 * Returns true if a database is currently open.
	 * 
	 * @return true if a database is open; otherwise false
	 */
	public boolean isDatabaseOpen() {
		return database != null;
	}
	
	/**
	 * Returns true if the database has been modified since it was opened or
	 * saved.
	 * 
	 * @return true if the database is open and dirty; otherwise false
	 */
	public boolean isDatabaseDirty() {
		// TODO real stuff
		return isDatabaseOpen();
	}
	
	/**
	 * Gets the currently open database.
	 * 
	 * @return the currently open database, or null if none
	 */
	public Database getDatabase() {
		return database;
	}

	/**
	 * Creates a new database in the specified file.
	 * 
	 * @param file the file in which to create the database
	 * @return the newly-created database
	 * @throws IOException if an error occurs
	 */
	public Database create(File file, LoginOptions loginOptions) throws IOException {
		File lockFile = new File(file.getPath() + ".lock");
		if (!lockFile.createNewFile()) {
			throw new IOException(Strings.getString("MESSAGE_LOCK_EXISTS")
					.replace("{}", lockFile.getPath()));
		}
		Database database = new Database(loginOptions);
		save(file, database);
		close();
		this.file = file;
		this.database = database;
		eventSupport.dispatchEvent(EventType.DB_OPENED);
		log.info("database created in {}", file.getPath());
		return database;
	}

	/**
	 * Opens the database contained in the specified file.
	 * 
	 * @param file the file to open
	 * @return the opened database
	 * @throws IOException if an error occurs
	 */
	public Database open(File file) throws IOException {
		File lockFile = new File(file.getPath() + ".lock");
		if (!lockFile.createNewFile()) {
			throw new IOException(Strings.getString("MESSAGE_LOCK_EXISTS")
					.replace("{}", lockFile.getPath()));
		}
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					file));
			try {
				this.database = (Database) in.readObject();
			} finally {
				in.close();
			}
			this.file = file;
			eventSupport.dispatchEvent(EventType.DB_OPENED);
			if (database.isInitialized()) {
				// TODO weapon.gui.firePropertyChange(GUI.DATABASE_INITIALIZED,
				// database, true);
			}
			// TODO if(database.getStardockSector() != null) {
			// weapon.gui.firePropertyChange(GUI.STARDOCK_DISCOVERED, null,
			// database.getStardockSector().getNumber());
			// }
			// TODO if(database.getYou().getSector() != null) {
			// weapon.gui.firePropertyChange(GUI.SHIP_SECTOR, null,
			// database.getYou().getSector().getNumber());
			// }
			log.info("database loaded from {}", file.getPath());
			// TODO
			// weapon.gui.updateTitles(database.getLoginOptions().getTitle());
			return database;
		} catch (Exception e) {
			lockFile.delete();
			if (e instanceof IOException)
				throw (IOException) e;
			else { // ClassNotFoundException, ClassCastException
				throw new IOException(Strings.getString(
						"MESSAGE_FILE_INCOMPATIBLE").replace("{}",
								file.getPath()), e);
			}
		}
	}

	/**
	 * Saves the database in the file currently associated with it.
	 * 
	 * @throws IOException if an error occurs
	 */
	public void save() throws IOException {
		save(file, database);
		log.info("database saved");
	}

	/**
	 * Saves the database in the specified file, changing the file associated
	 * with the database.
	 * 
	 * @param newFile the file to save into
	 * @throws IOException if an error occurs
	 */
	public void saveAs(File newFile) throws IOException {
		// create new lock file
		File lockFile = new File(newFile.getPath() + ".lock");
		if (!lockFile.createNewFile()) {
			throw new IOException(Strings.getString("MESSAGE_LOCK_EXISTS")
					.replace("{}", lockFile.getPath()));
		}
		save(newFile, database);
		// delete old lock file
		new File(file.getPath() + ".lock").delete();
		file = newFile;
		log.info("database saved as '{}'", file.getPath());
	}

	/**
	 * Saves the database in the specified file, without changing the file
	 * associated with the database.
	 * 
	 * @param copyFile the file to copy into
	 * @throws IOException if an error occurs
	 */
	public void saveCopy(File copyFile) throws IOException {
		File lockFile = new File(file.getPath() + ".lock");
		if (!lockFile.createNewFile()) {
			throw new IOException(Strings.getString("MESSAGE_LOCK_EXISTS")
					.replace("{}", lockFile.getPath()));
		}
		save(copyFile, database);
		lockFile.delete();
		log.info("database copied to {}", copyFile.getPath());
	}

	private void save(File file, Database database) throws IOException {
		if (file.isDirectory())
			throw new IOException(Strings.getString("MESSAGE_FILE_IS_DIR"));
		File tmpFile = new File(file.getPath() + ".tmp");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				tmpFile));
		try {
			synchronized(Database.lock) {
				out.writeObject(database);
			}
		} finally {
			out.close();
		}
		if (file.exists() && !file.delete()) {
			throw new IOException(Strings.getString("MESSAGE_DELETE_FAILED"));
		}
		if (!tmpFile.renameTo(file)) {
			throw new IOException(Strings.getString("MESSAGE_RENAME_FAILED"));
		}
	}

	/**
	 * Closes the currently open database.
	 */
	public void close() {
		if (file != null) {
			eventSupport.dispatchEvent(EventType.DB_CLOSED);
			new File(file.getPath() + ".lock").delete();
			file = null;
			database = null;
			// TODO reset lexer/parser
			log.info("database closed");
		}
	}

	// TODO export, import, merge
	
	/**
	 * Add a change listener to a data object.  This functionality is not
	 * public in <tt>DataObject</tt> itself so that scripts are forced to use
	 * a proxy that tracks their listeners.  This method is not static for the
	 * same reason.
	 * 
	 * @param object
	 * @param listener
	 */
	public void addDataChangeListener(DataObject object, DataChangeListener listener) {
		object.addChangeListener(listener);
	}
	
	public void removeDataChangeListener(DataObject object, DataChangeListener listener) {
		object.removeChangeListener(listener);
	}
	
	public void addDataChangeListener(Database db, Class<? extends DataObject> klass, DataChangeListener listener) {
		db.addChangeListener(klass, listener);
	}
	
	public void removeDataChangeListener(Database db, Class<? extends DataObject> klass, DataChangeListener listener) {
		db.removeChangeListener(klass, listener);
	}

}
