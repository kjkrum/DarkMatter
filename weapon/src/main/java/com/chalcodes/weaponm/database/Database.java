package com.chalcodes.weaponm.database;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.chalcodes.weaponm.event.EventType;

/**
 * All the data collected about a game.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
@XmlRootElement
public class Database implements DataObject {
	@XmlTransient
	private DatabaseManager manager;
	
	@XmlElement(name="LoginOptions")
	private final LoginOptions loginOptions = new LoginOptions(this);
	// TODO are these the right annotations?
	@XmlElementWrapper(name="Sectors") @XmlElement(name="Sector")
	private Sector[] sectors;
	
	/**
	 * Creates a new database.
	 */
	Database() { }
	
	/**
	 * Used by JAXB.
	 * 
	 * @param u the JAXB unmarshaller
	 * @param parent the parent object
	 */
	@SuppressWarnings("unused")
	private void afterUnmarshal(Unmarshaller u, Object parent) {
		// recreate warps in
		if(sectors != null) {
			for(Sector s : sectors) {
				for(int warp : s.getWarpsOut()) {
					getSector(warp).addWarpFrom(s.getNumber());
				}
			}
		}
	}
	
	// manager calls this after creating or unmarshalling
	void setManager(DatabaseManager manager) {
		this.manager = manager;
	}
	
	@Override
	public Database getDatabase() {
		return this;
	}

	// convenience method to fire a data change event
	void fireChanged(DataObject object) {
		// TODO fire event on the manager
	}
	
	// fire other data events like sector explored
	void fireEvent(Object source, EventType type, Object oldValue, Object newValue) {
		// TODO fire event on the manager
	}
	
	/**
	 * Resolves an owner from an owner ID.
	 * 
	 * @param id the owner ID
	 * @return the owner
	 */
	Owner resolveOwner(int id) {
		// TODO are these values correct?
		final int MAX_CORPS = 1000;
		final int MAX_TRADERS = 1000;
		if(id > MAX_CORPS + MAX_TRADERS) {
			// return alien race
			return null;
		}
		else if(id > MAX_CORPS) {
			// return trader
			return null;
		}
		else if(id > 0) {
			// return corp
			return null;
		}
		else {
			return StaticOwner.forId(id);
		}
	}

	public LoginOptions getLoginOptions() {
		return loginOptions;
	}

	/**
	 * Tests if the database is initialized with the number of sectors and
	 * other basic information.  Becomes true after you have viewed the game
	 * stats.
	 * 
	 * @return true if the database is initialized; otherwise false
	 */
	public boolean isInitialized() {
		return sectors != null;
	}
	
	void initialize(int numSectors) {
		sectors = new Sector[numSectors];
		for(int i = 0; i < sectors.length; ++i) {
			sectors[i] = new Sector(this, i + 1);
		}
		fireEvent(this, EventType.DATABASE_INITIALIZED, false, true);
	}
	
	/**
	 * Gets a sector.
	 * 
	 * @param number the 1-based sector number
	 * @return the sector
	 * @throws NullPointerException if the database is not initialized
	 * @see #isInitialized()
	 */
	public Sector getSector(int number) {
		return sectors[number - 1];
	}
}
