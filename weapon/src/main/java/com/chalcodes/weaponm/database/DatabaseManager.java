package com.chalcodes.weaponm.database;

import java.io.Closeable;
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
import com.chalcodes.weaponm.gui.I18n;

/**
 * Manages serialization and deserialization of databases.  Associates the
 * current database with a file, but does not hold the file open.  Although
 * this class is public, scripts cannot normally obtain a reference to the
 * application's instance of it.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class DatabaseManager {
	private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class.getSimpleName());
	private final EventSupport eventSupport;
	private Database db;
	private File file;
	private DatabaseState state = DatabaseState.CLOSED;
	
	public DatabaseManager(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}
	
	void fireEvent(Object source, EventType type, Object oldValue, Object newValue) {
		eventSupport.fireEvent(source, type, oldValue, newValue);
	}
	
	void fireChanged() {
		setState(DatabaseState.OPEN_DIRTY);
	}
	
	public DatabaseState getState() {
		return state;
	}
	
	private void setState(DatabaseState newState) {
		if(this.state != newState) {
			DatabaseState oldState = this.state;
			this.state = newState;
			eventSupport.fireEvent(db, EventType.DATABASE_STATE, oldState, newState);
		}
	}
	
	public Database create(File file, LoginOptions loginOptions) throws IOException {
		File lockFile = new File(file.getPath() + ".lock");
		if (!lockFile.createNewFile()) {
			throw new IOException(I18n.getString("MESSAGE_LOCK_EXISTS").replace("{}", lockFile.getPath()));
		}
		Database db = new Database();
		db.setManager(this);
		db.getLoginOptions().copyFrom(loginOptions);
		try {
			serialize(db, file);
		}
		catch(IOException e) {
			lockFile.delete();
			throw e;
		}
		
		// success!
		this.db = db;
		this.file = file;
		log.info("database created in {}", file.getPath());
		setState(DatabaseState.OPEN_CLEAN);
		return db;
	}
	
	public Database open(File file) throws IOException {
		File lockFile = new File(file.getPath() + ".lock");
		if (!lockFile.createNewFile()) {
			throw new IOException(I18n.getString("MESSAGE_LOCK_EXISTS").replace("{}", lockFile.getPath()));
		}
		Database db;
		try {
			db = deserialize(file);
		}
		catch(IOException e) {
			lockFile.delete();
			throw e;
		}
		catch(ClassNotFoundException e) {
			lockFile.delete();
			throw new IOException(I18n.getString("MESSAGE_FILE_INCOMPATIBLE").replace("{}",	file.getPath()), e);
		}
		
		// success!
		this.db = db;
		this.file = file;
		log.info("database loaded from {}", file.getPath());
		setState(DatabaseState.OPEN_CLEAN);
		fireEvent(this, EventType.DATABASE_INITIALIZED, false, db.isInitialized());
		// TODO more events
		return db;
	}
	
	public void save() throws IOException {
		serialize(db, file);
	}
	
	public void saveAs(File newFile) throws IOException {
		if(newFile.equals(file)) {
			save();
		}
		else {
			File lockFile = new File(newFile.getPath() + ".lock");
			if (!lockFile.createNewFile()) {
				throw new IOException(I18n.getString("MESSAGE_LOCK_EXISTS").replace("{}", lockFile.getPath()));
			}
			try {
				serialize(db, newFile);
			}
			catch(IOException e) {
				lockFile.delete();
				throw e;
			}
			
			// success!
			File oldLockFile = new File(file.getPath() + ".lock");
			oldLockFile.delete();
			file = newFile;
			log.info("database saved as '{}'", file.getPath());
			setState(DatabaseState.OPEN_CLEAN);
		}
	}
	
	public void saveCopy(File copyFile) throws IOException {
		File lockFile = new File(copyFile.getPath() + ".lock");
		if (!lockFile.createNewFile()) {
			throw new IOException(I18n.getString("MESSAGE_LOCK_EXISTS").replace("{}", lockFile.getPath()));
		}
		try {
			serialize(db, copyFile);
		}
		finally {
			lockFile.delete();
		}
		log.info("database copied to {}", copyFile.getPath());
	}
	
	public void close() {
		// TODO shut down network and scripts?
		File lockFile = new File(file.getPath() + ".lock");
		lockFile.delete();
		db = null;
		file = null;
		log.info("database closed");
		setState(DatabaseState.CLOSED);
	}
		
	private static void serialize(Database db, File file) throws IOException {
		if (file.isDirectory()) {
			throw new IOException(I18n.getString("MESSAGE_FILE_IS_DIR"));
		}
		File tempFile = new File(file.getPath() + ".tmp");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFile));
		try {
			out.writeObject(db);
		}
		finally {
			close(out);
		}
		if (file.exists() && !file.delete()) {
			throw new IOException(I18n.getString("MESSAGE_DELETE_FAILED"));
		}
		if (!tempFile.renameTo(file)) {
			throw new IOException(I18n.getString("MESSAGE_RENAME_FAILED"));
		}
	}
	
	private static Database deserialize(File file) throws IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		try {
			return (Database) in.readObject();
		}
		finally {
			close(in);
		}
	}
	
	private static void close(Closeable closeable) {
		try {
			closeable.close();
		}
		catch(IOException e) {
			log.warn("exception on close", e);
		}
	}
}
