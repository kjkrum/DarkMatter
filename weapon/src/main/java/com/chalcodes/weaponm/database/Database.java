package com.chalcodes.weaponm.database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Database implements Serializable {
	private static final long serialVersionUID = 1L;

	static final Object lock = new Object();
	
	private final LoginOptions loginOptions = new LoginOptions();
	private Sector[] sectors;
	
	Database() {
		restoreTransients();
	}
	
	public LoginOptions getLoginOptions() {
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
	}
	
	public Sector getSector(int number) {
		synchronized(lock) {
			return(sectors[number - 1]);
		}
	}
	
	/************************************************************************/
	
	void restoreTransients() {
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

		if(isInitialized()) {
			for(Sector sector : sectors) {
				sector.setDatabase(this);
				for(int w : sector.getWarpsOut()) {
					getSector(w).addWarpFrom(sector.getNumber());
				}
			}
		}		
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
