package com.chalcodes.weaponm.database;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Database extends DataObject {
	private static final long serialVersionUID = 1L;
	static final Object lock = new Object();
	private transient final Map<Class<?>, List<DataChangeListener>> changeListeners =
			new HashMap<Class<?>, List<DataChangeListener>>();
	private final LoginOptions loginOptions;
	private Sector[] sectors;
	
	Database(LoginOptions loginOptions) {
		this.loginOptions  = loginOptions;
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
	
	// TODO give scripts access through a proxy
	void addChangeListener(Class<? extends DataObject> klass, DataChangeListener listener) {
		if(listener == null) throw new NullPointerException();
		if(!changeListeners.containsKey(klass)) {
			changeListeners.put(klass, new LinkedList<DataChangeListener>());
		}
		changeListeners.get(klass).add(listener);
	}
	
	// TODO give scripts access through a proxy
	void removeChangeListener(Class<? extends DataObject> klass, DataChangeListener listener) {
		if(listener != null && changeListeners.containsKey(klass)) {
			changeListeners.get(klass).remove(listener);
		}
	}
	
	// this is only called in the UI thread by DataObject#fireChanged()
	void fireChanged(DataObject obj) {
		if(changeListeners.containsKey(obj.getClass())) {
			for(DataChangeListener listener : changeListeners.get(obj.getClass())) {
				listener.dataChanged(obj);
			}
		}
		// DataObject#fireChanged() calls #fireChanged()
	}
	
	@Override
	void setDatabase(Database db) {
		// ignored - setting this would cause infinite recursion in #fireChanged()
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
		
		// TODO set db on loginOptions

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
