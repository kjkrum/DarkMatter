package com.chalcodes.weaponm.database;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// TODO uncomment things as classes are added

public class Sector implements Serializable, Constants {
	private static final long serialVersionUID = 1L;
	
	//transient List<Trader> traders = new LinkedList<Trader>();
	//transient List<Alien> aliens = new LinkedList<Alien>();
	//transient List<Ship> ships = new LinkedList<Ship>();
	//transient List<Planet> planets = new LinkedList<Planet>();
	
	Sector(Database db, int number) {
		this.db = db;
		this.number = number;
	}

	private transient Database db;
	
	// called by the db manager after deserialization
	void setDatabase(Database db) {
		this.db = db;
	}

	private final int number;
	
	public int getNumber() {
		return number;
	}

	private int[] warpsOut = new int[0];
	private boolean warpsOutSorted = true;

	void addWarpTo(int sector) {
		synchronized(Database.lock) {
			if(fullyMapped || hasWarpTo(sector)) return;
			warpsOut = Arrays.copyOf(warpsOut, warpsOut.length + 1);
			warpsOut[warpsOut.length - 1] = sector;
			if(warpsOut.length > 1 && warpsOut[warpsOut.length - 1] < warpsOut[warpsOut.length - 2]) {
				warpsOutSorted = false;
			}
			if(warpsOut.length == 6 || warpsOut.length == warpDensity) fullyMapped = true;
			Sector warp = db.getSector(sector);
			warp.addWarpFrom(number);
		}
	}

	void setWarpsOut(int[] warpsOut) {
		synchronized(Database.lock) {
			if(this.warpsOut.length == warpsOut.length) {
				if(warpsOutSorted) {
					// should be identical; nothing to do
					return;
				}
			}
			else {
				// only needed if length changed
				for (int i = 0; i < warpsOut.length; ++i) {
					Sector warp = db.getSector(warpsOut[i]);
					warp.addWarpFrom(number);
				}
			}
			this.warpsOut = warpsOut;
			warpsOutSorted = true;
			explored = true;
			fullyMapped = true;			
		}
	}

	/**
	 * Returns the number of warps this sector is known to have.  This is the
	 * larger of the number of known warps and the number of warps reported by
	 * a density scan.
	 * 
	 * @see #getWarpDensity()
	 * @see #getNumKnownWarpsOut()
	 */
	public int getNumWarpsOut() {
		synchronized(Database.lock) {
			return Math.max(warpDensity, warpsOut.length);
		}
	}

	/**
	 * Returns the number of known warps out.  This differs from
	 * {@link #getNumWarpsOut()} in that this method returns the number of
	 * warps with known destinations.  This may be less than the number of
	 * warps the sector is known to have if, for example, your only
	 * knowledge of the sector is a density scan.
	 * 
	 * @see #getNumWarpsOut()
	 */
	public int getNumKnownWarpsOut() {
		synchronized(Database.lock) {
			return warpsOut.length;
		}
	}

	/**
	 * Returns true if this sector is known to have a warp to the specified
	 * sector.
	 */
	public boolean hasWarpTo(int sector) {
		synchronized(Database.lock) {
			for(int i = 0; i < warpsOut.length; ++i) {
				if(warpsOut[i] == sector) return true;
			}
			return false;
		}
	}

	/**
	 * Gets the known warps out.  The array will be sorted in ascending order.
	 */
	public int[] getWarpsOut() {
		synchronized(Database.lock) {
			if(!warpsOutSorted) {
				Arrays.sort(warpsOut);
				warpsOutSorted = true;
			}
			return Arrays.copyOf(warpsOut, warpsOut.length);
		}
	}

	private transient int[] warpsIn = new int[0];
	private transient boolean warpsInSorted = true;
	
	void addWarpFrom(int sector) {
		synchronized(Database.lock) {
			if(hasWarpFrom(sector)) return;
			warpsIn = Arrays.copyOf(warpsIn, warpsIn.length + 1);
			warpsIn[warpsIn.length - 1] = sector;
			if(warpsIn.length > 1 && warpsIn[warpsIn.length - 1] < warpsIn[warpsIn.length - 2]) {
				warpsInSorted = false;
			}
		}
	}

	/**
	 * Returns the number of known warps into the sector.
	 */
	public int getNumWarpsIn() {
		synchronized(Database.lock) {
			return warpsIn.length;
		}
	}

	/**
	 * Returns true if this sector is known to have a warp from the specified
	 * sector.
	 */
	public boolean hasWarpFrom(int sector) {
		synchronized(Database.lock) {
			for(int i = 0; i < warpsIn.length; ++i) {
				if(warpsIn[i] == sector) return true;
			}
			return false;
		}
	}

	/**
	 * Gets the known warps in.  The array will be sorted in ascending order.
	 */
	public int[] getWarpsIn() {
		synchronized(Database.lock) {
			if(!warpsInSorted) {
				Arrays.sort(warpsIn);
				warpsInSorted = true;
			}
			return Arrays.copyOf(warpsIn, warpsIn.length);
		}
	}

	private int density = UNKNOWN;
	
	void setDensityData(int density, int warpDensity, int navhaz, boolean anomaly) {
		synchronized(Database.lock) {
			this.density = density;
			this.warpDensity = warpDensity;
			this.navhaz = navhaz;
			this.anomaly = anomaly;
			this.densityDate = new Date();
			if(warpDensity == warpsOut.length) fullyMapped = true;
		}
	}

	/**
	 * Returns true if this sector has been density scanned.
	 */
	public boolean hasDensityData() {
		synchronized(Database.lock) {
			return densityDate != null;
		}
	}

	/**
	 * Returns the sector's density.  Returns <tt>UNKNOWN</tt> if the sector
	 * has not been density scanned.
	 */
	public int getDensity() {
		synchronized(Database.lock) {
			return density;
		}
	}

	private int warpDensity = UNKNOWN;
	
	/**
	 * Returns the number of warps reported by a density scan.  Returns
	 * <tt>UNKNOWN</tt> if the sector has not been density scanned.
	 * 
	 * @see #getNumWarpsOut()
	 */
	public int getWarpDensity() {
		synchronized(Database.lock) {
			return warpDensity;
		}
	}

	private Date densityDate; // date of last density scan
	/**
	 * Returns the date of the last density scan.  Returns null if the sector
	 * has not been density scanned.
	 */
	public Date getDensityDate() {
		synchronized(Database.lock) {
			return densityDate;
		}
	}

	private boolean fullyMapped;
	
	void setFullyMapped(boolean fullyMapped) {
		synchronized(Database.lock) {
			this.fullyMapped = fullyMapped;
		}
	}

	/**
	 * Returns true if it is certain that all the sector's outbound warps are
	 * known.  This is true if the sector's warps have been parsed, or it has
	 * six known warps, or it has been density scanned and the number of known
	 * warps equals the number reported by the density scan.
	 */
	public boolean isFullyMapped() {
		synchronized(Database.lock) {
			return fullyMapped;
		}
	}

	private boolean explored;
	
	void setExplored(boolean explored) {
		synchronized(Database.lock) {
			this.explored = explored;
		}
	}

	/**
	 * Returns true if the sector is explored.
	 */
	public boolean isExplored() {
		synchronized(Database.lock) {
			return explored;
		}
	}

	private boolean avoided;
	
	void setAvoided(boolean avoided) {
		synchronized(Database.lock) {
			this.avoided = avoided;
		}
	}

	/**
	 * Returns true if the sector is avoided.
	 */
	public boolean isAvoided() {
		synchronized(Database.lock) {
			return avoided;
		}
	}

	private String nebula;
	
	void setNebula(String nebula) {
		synchronized(Database.lock) {
			this.nebula = nebula;
		}
	}

	/**
	 * Returns the sector's nebula name.  Returns null if the sector is not
	 * part of a nebula.
	 */
	public String getNebula() {
		synchronized(Database.lock) {
			return nebula;
		}
	}

	private int navhaz = UNKNOWN;
	
	/**
	 * Returns the last known navhaz value, as recorded by a density scan,
	 * holo scan, ether probe, or presence.  Returns <tt>UNKNOWN</tt> if the
	 * navhaz level has never been recorded.
	 */
	public int getNavhaz() {
		synchronized(Database.lock) {
			return navhaz;
		}
	}

	private boolean anomaly;
	
	/**
	 * Returns true if an anomaly was detected on the last density scan.
	 * Returns false if there was no anomaly or the sector has not been
	 * density scanned.
	 * 
	 * @see #hasDensityData()
	 */
	public boolean containsAnomaly() {
		synchronized(Database.lock) {
			return anomaly;
		}
	}

	private Date holoDate;

	void setHoloDate(Date holoDate) {
		synchronized(Database.lock) {
			this.holoDate = holoDate;
		}
	}

	/**
	 * Returns the date of your last holo scan, ether probe report, or
	 * presence in the sector.  Returns null if no holo data has been
	 * recorded.  The holo date is <em>not</em> updated when your probe is
	 * destroyed in the sector.
	 */
	public Date getHoloDate() {
		synchronized(Database.lock) {
			return holoDate;
		}
	}

	/**
	 * Returns the date of your last density scan, holo scan, ether probe
	 * report, or presence in the sector.  Returns null if the sector has not
	 * been scanned.  This date is <em>not</em> updated when your probe is
	 * destroyed in the sector.
	 */
	public Date getScanDate() {
		synchronized(Database.lock) {
			if(holoDate == null) return densityDate;
			if(densityDate == null) return holoDate;
			return densityDate.after(holoDate) ? densityDate : holoDate;
		}
	}

	private final Map<String, String> notes = new HashMap<String, String>();
	
	/**
	 * Sets the specified note for this sector.  Note names must be unique.
	 * To avoid conflicts, it is recommended that note names be prefixed with
	 * the fully qualified class name of the script that created them.
	 * 
	 * @param name the name of the note
	 * @param note the content of the note
	 */
	public void setNote(String name, String note) {
		synchronized(Database.lock) {
			notes.put(name, note);
		}
	}

	/**
	 * Gets the specified note for this sector.
	 * 
	 * @param name the name of the note
	 * @return the content of the note
	 */
	public String getNote(String name) {
		synchronized(Database.lock) {	
			return notes.get(name);
		}
	}

	/**
	 * Removes the specified note from this sector.
	 * 
	 * @param name the name of the note
	 */
	public void removeNote(String name) {
		synchronized(Database.lock) {
			notes.remove(name);
		}
	}

	//private volatile Port port;
	
	private String beaconMessage;
	
	void setBeaconMessage(String message) {
		synchronized(Database.lock) {
			beaconMessage = message;
		}
	}

	public String getBeaconMessage() {
		synchronized(Database.lock) {
			return beaconMessage;
		}
	}
	
	//private FighterMode fighterMode;
		
		/**
		 * Returns the mode of the fighters in this sector.  Returns null if there
		 * are no fighters in the sector, or their owner is unknown.
		 * 
		 * @see #getFighters()
		 */
	//	synchronized public FighterMode getFighterMode() {
	//		return fighterMode;
	//	}
		
		
		
	//	synchronized void setFighters(int fighters, Owner owner, FighterMode mode) {
	//		// don't update fighter date if nothing has changed
	//		if(this.fighters == fighters && fighterOwner == owner && fighterMode == mode) return;
	//		this.fighters = fighters;
	//		fighterOwner = owner;
	//		fighterMode = mode;
	//		fighterDate = (fighters == 0) ? null : new Date();
	//	}

	private int fighters;
	
	/**
	 * Returns the number of fighters the sector is known to contain.  Unlike
	 * most other numeric fields, this value is initialized to zero.  A value
	 * of <tt>UNKNOWN</tt> indicates that enemy fighters destroyed one of your
	 * probes in the sector.
	 */
	public int getFighters() {
		synchronized(Database.lock) {
			return fighters;
		}
	}

	private Owner fighterOwner;
	
	/**
	 * Gets the owner of the fighters in the sector.  Returns null if there
	 * are no fighters in the sector, or their owner is unknown.
	 * 
	 * @see #getFighters()
	 */
	public Owner getFighterOwner() {
		synchronized(Database.lock) {
			return fighterOwner;			
		}
	}
	
	private Date fighterDate;
	
	/**
	 * Returns the date the fighters in this sector were placed or discovered.
	 * Returns null if there are no fighters in the sector.
	 */
	public Date getFighterDate() {
		synchronized(Database.lock) {
			return fighterDate;
		}
	}	

	private String figHitName;
	private Date figHitDate;
	
	void setFigHit(String name, Date date) {
		synchronized(Database.lock) {
			figHitName = name;
			figHitDate = date;
		}
	}
		
	/**
	 * Returns the name of the last entity to hit your fighters in this
	 * sector.  Includes hits by traders, aliens, probes, and Feds.  Returns
	 * null if no fighter hit has been recorded in this sector.
	 */
	public String getFigHitName() {
		synchronized(Database.lock) {
			return figHitName;
		}
	}

	/**
	 * Returns the date of the last fighter hit in this sector.  Returns null
	 * if no fighter hit has been recorded.
	 */
	public Date getFigHitDate() {
		synchronized(Database.lock) {
			return figHitDate;
		}
	}



	private int armids = UNKNOWN;
	private Owner armidOwner;

	void setArmids(int armids, Owner owner) {
		synchronized(Database.lock) {
			this.armids = armids;
			armidOwner = owner;
		}
	}

	/**
	 * Returns the number of armid mines in this sector.
	 */
	public int getArmids() {
		synchronized(Database.lock) {
			return armids;
		}
	}
	
	/**
	 * Returns the owner of the armid mines in this sector.  Returns null if
	 * there are no armid mines in this sector.
	 */
	public Owner getArmidOwner() {
		synchronized(Database.lock) {
			return armidOwner;
		}
	}
	
	private int limpets;
	private Owner limpetOwner;
	
	void setLimpets(int limpets, Owner owner) {
		synchronized(Database.lock) {
			this.limpets = limpets;
			limpetOwner = owner;
		}
	}

	/**
	 * Returns the number of limpets in this sector.  Only returns information
	 * about your own limpets.
	 */
	public int getLimpets() {
		synchronized(Database.lock) {
			return limpets;
		}
	}
	
	/**
	 * Returns the owner of the limpets in this sector.  Returns null if there
	 * are no limpets in this sector.  Only returns information about your own
	 * limpets.
	 */
	public Owner getLimpetOwner() {
		synchronized(Database.lock) {
			return limpetOwner;
		}
	}
	
	/**
	 * Returns true if this sector is known to contain a port.
	 */
//	public boolean hasPort() {
//		return port != null;
//	}
	
	/**
	 * Returns the port in this sector.  Returns null if no port is known.
	 */
//	public Port getPort() {
//			return port;
//	}
	
//	void setPort(Port port) {
//			this.port = port;
//	}
	

	
//	public boolean isFedSpace() {
//		return(number <= 10 || this == db.getStardockSector());
//	}
	
//	public boolean isYourLocation() {
//		return this == db.getYou().getSector();
//	}
	
	/************************************************************************/
	
	@Override
	public String toString() {
		return "Sector " + number;
	}
	
//	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//		in.defaultReadObject();
//		warpsIn = new int[0];
//		warpsInSorted = true;
//		traders = new LinkedList<Trader>();
//		aliens = new LinkedList<Alien>();
//		ships = new LinkedList<Ship>();
//		planets = new LinkedList<Planet>();
//	}
}
