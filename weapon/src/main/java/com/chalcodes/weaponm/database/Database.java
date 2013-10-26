package com.chalcodes.weaponm.database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Database extends DataObject {
	private static final long serialVersionUID = 1L;
	static final Object lock = new Object();
	private transient DatabaseManager manager;
	private final LoginOptions loginOptions;
	private Sector[] sectors;
	
	// call this before a new or loaded db is returned by its creator
	void setManager(DatabaseManager manager) {
		if(this.manager != null) {
			throw new IllegalStateException("manager already set");
		}
		this.manager = manager; 
	}
	
	DatabaseManager getManager() {
		return manager;
	}
	
	Database(LoginOptions loginOptions) {
		this.loginOptions  = loginOptions;
		restoreTransients();
	}
	
	LoginOptions getLoginOptions() {
		return loginOptions;
	}
	
	/**
	 * Becomes true after you have viewed the game stats.
	 * 
	 * @return true if the database is initialized; otherwise false
	 */
	public boolean isInitialized() {
		synchronized(lock) {
			return sectors != null;
		}
	}
	
	void initialize() {
		synchronized(lock) {
			int s = 100; // TODO int s = gameStats.sectors();
			sectors = new Sector[s];
			for(int i = 0; i < s; ++i) {
				sectors[i] = new Sector(this, i + 1);
			}
		}
		fireChanged();
	}
	
	public Sector getSector(int number) {
		synchronized(lock) {
			return(sectors[number - 1]);
		}
	}
	
	/************************************************************************/
	
	void restoreTransients() {
		setDatabase(this);
		loginOptions.setDatabase(this);
		if(isInitialized()) {
			for(Sector sector : sectors) {
				sector.setDatabase(this);
				for(int w : sector.getWarpsOut()) {
					getSector(w).addWarpFrom(sector.getNumber());
				}
			}
		}
//		shipTypeNameIndex = new HashMap<String, ShipType>();
//		for(ShipType shipType : shipTypes) {
//			shipTypeNameIndex.put(shipType.getName(), shipType);
//		}
//		
//		traderNameIndex = new HashMap<String, Trader>();
//		for(Trader trader : traders) {
//			String name = trader.getName();
//			if (name.length() > 6) name = name.substring(0, 6);
//			traderNameIndex.put(name, trader);
//		}
//
//		bossIndex = new HashMap<String, Boss>();
//		for(Boss boss : bosses) {
//			bossIndex.put(boss.getName(), boss);
//		}
//		
//		alienIndex = new HashMap<String, Alien>();
//		for(Alien alien : aliens) {
//			alienIndex.put(alien.getName(), alien);
//		}
//		
//		planetTypeIndex = new HashMap<String, PlanetType>();
//		for(PlanetType planetType : planetTypes) {
//			planetTypeIndex.put(planetType.getName(), planetType);
//		}
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		synchronized(lock) {
			out.defaultWriteObject();
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		restoreTransients();
	}
}
