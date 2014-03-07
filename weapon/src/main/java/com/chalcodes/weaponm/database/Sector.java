package com.chalcodes.weaponm.database;

import static com.chalcodes.weaponm.database.Constants.UNKNOWN;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A sector.  Sectors can contain many things.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
@XmlRootElement
public class Sector implements DataObject {
	@XmlTransient
	private Database db;
	@XmlAttribute
	private final int number;
	@XmlElement(name="WarpsOut") @XmlList
	private final Set<Integer> warpsOut = new TreeSet<Integer>();
	@XmlTransient
	private final Set<Integer> warpsIn = new TreeSet<Integer>();
	@XmlElement(name="WarpsParsed")
	private Boolean warpsParsed;
	
	// use wrappers instead of primitives
	// use null to represent the default value
	// write getters and setters accordingly
	
	@XmlElement(name="WarpDensity")
	private Integer warpDensity;
	@XmlElement(name="Density")
	private Integer density;
	@XmlElement(name="DensityDate")
	private Date densityDate;
	@XmlElement(name="NavHaz")
	private Integer navhaz;
	@XmlElement(name="Anomaly")
	private Boolean anomaly;
	@XmlElement(name="HoloDate")
	private Date holoDate;
	@XmlElement(name="Explored")
	private Boolean explored;
	@XmlElement(name="Avoided")
	private Boolean avoided;
	@XmlElement(name="Nebula")
	private String nebulaName;
	@XmlElement(name="Beacon")
	private String beaconMessage;
	
	@XmlElement(name="Fighters")
	private Integer fighters;
	@XmlElement(name="FighterOwner")
	private Integer fighterOwner;
	@XmlElement(name="FighterMode")
	private FighterMode fighterMode;
	@XmlElement(name="FighterDate")
	private Date fighterDate;
	
	@XmlElement(name="Notes")
	private final Map<String, String> notes = new HashMap<String, String>();
	
	/**
	 * Creates a new sector.
	 * 
	 * @param db the parent database
	 * @param number the sector number
	 */
	Sector(Database db, int number) {
		this.db = db;
		this.number = number;
	}

	/**
	 * Required by JAXB.
	 */
	@SuppressWarnings("unused")
	private Sector() {
		number = UNKNOWN;
	}
	
	/**
	 * Restores the reference to the parent database.
	 * 
	 * @param u the JAXB unmarshaller
	 * @param parent the parent object
	 */
	@SuppressWarnings("unused")
	private void afterUnmarshal(Unmarshaller u, Object parent) {
		db = (Database) parent;
	}
	
	@Override
	public Database getDatabase() {
		return db;
	}
	
	/**
	 * Gets the sector number.
	 * 
	 * @return the sector number
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Tests if this sector is known to have a warp to the specified sector.
	 * 
	 * @param sector
	 * @return
	 */
	public boolean hasWarpTo(Integer sector) {
		return warpsOut.contains(sector);
	}
	
	/**
	 * Adds a warp to the specified sector.
	 * 
	 * @param sector
	 */
	void addWarpTo(Integer sector) {
		if(!warpsOut.contains(sector)) {
			warpsOut.add(sector);
			db.fireChanged(this);
			// TODO db.fireEvent(db, EventType.WARP_DISCOVERED, this, warp);
			
			Sector other = db.getSector(sector);
			other.addWarpFrom(number);
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
		return Math.max(getWarpDensity(), warpsOut.size());
	}

	/**
	 * Returns the number of known warps out.  Note that this may be differ from
	 * the number of warps that the sector is known to have.
	 * 
	 * @see #getNumWarpsOut()
	 */
	public int getNumKnownWarpsOut() {
		return warpsOut.size();
	}
	
	/**
	 * Gets the known warps out.  The array will be sorted in ascending order.
	 */
	public List<Integer> getWarpsOut() {
		return new ArrayList<Integer>(warpsOut);
	}
	
	void setWarpsOut(List<Integer> parsedWarps) {
		if(warpsOut.size() < parsedWarps.size()) {
			warpsOut.addAll(parsedWarps);
			explored = Boolean.TRUE;
			warpsParsed = Boolean.TRUE;
			db.fireChanged(this);
		}
	}
	
	/**
	 * Returns true if this sector is known to have a warp from the specified
	 * sector.
	 */
	public boolean hasWarpFrom(Integer sector) {
		return warpsIn.contains(sector);
	}
	
	/**
	 * Adds a warp from the specified sector.
	 * 
	 * @param sector
	 */
	void addWarpFrom(Integer sector) {
		// do not fire changed
		warpsIn.add(sector);
	}
	
	/**
	 * Returns the number of known warps into the sector.
	 */
	public int getNumWarpsIn() {
		return warpsIn.size();
	}
	
	/**
	 * Gets the known warps in.  The array will be sorted in ascending order.
	 */
	public List<Integer> getWarpsIn() {
		return new ArrayList<Integer>(warpsIn);
	}
	
	/**
	 * Returns true if the sector is explored.
	 */
	public boolean isExplored() {
		return explored == Boolean.TRUE;
	}
	
	void setExplored() {
		if(explored != Boolean.TRUE) {
			explored = Boolean.TRUE;
			db.fireChanged(this);
		}
	}

	/**
	 * Returns true if it is certain that all of the sector's outbound warps
	 * are known.  This is true if the sector's warps have been displayed, or
	 * it has six known warps, or it has been density scanned and the number
	 * of known warps equals the number reported by the density scan.
	 */
	public boolean isFullyMapped() {
		return warpsParsed == Boolean.TRUE || warpsOut.size() == 6 ||
				warpsOut.size() == getWarpDensity();
	}
	
	/**
	 * Tests if the sector has been density scanned.
	 * 
	 * @return true if the sector has been density scanned; otherwise false
	 */
	public boolean isDensityScanned() {
		return densityDate != null;
	}
	
	/**
	 * Returns the date of the last density scan.  Returns null if the sector
	 * has not been density scanned.
	 * 
	 * @see #isDensityScanned()
	 */
	public Date getDensityDate() {
		return densityDate;
	}
	
	/**
	 * Returns the sector's density.  Returns {@link Constants#UNKNOWN} if the
	 * sector has not been density scanned.
	 *
	 * @see #isDensityScanned()
	 */
	public int getDensity() {
		if(density == null) {
			return UNKNOWN;
		}
		else {
			return density;
		}
	}
	
	/**
	 * Returns the number of warps reported by a density scan.  Returns
	 * {@link Constants#UNKNOWN} if the sector has not been density scanned.
	 * 
	 * @see #isDensityScanned()
	 * @see #getNumWarpsOut()
	 */
	public int getWarpDensity() {
		if(warpDensity == null) {
			return UNKNOWN;
		}
		else {
			return warpDensity;
		}
	}
	
	/**
	 * Returns the last navhaz value recorded by a density scan, holo scan,
	 * ether probe, or presence.  Returns {@link Constants#UNKNOWN} if the
	 * navhaz level has never been recorded.
	 */
	public int getNavhaz() {
		if(navhaz == null) {
			return UNKNOWN;
		}
		else {
			return navhaz;
		}
	}
	
	// TODO method to calculate decayed navhaz?
	
	/**
	 * Returns true if an anomaly was detected on the last density scan.
	 * Returns false if there was no anomaly or the sector has not been
	 * density scanned.
	 * 
	 * @see #hasDensityData()
	 */
	public boolean hasAnomaly() {
		return anomaly == Boolean.TRUE;
	}

	/**
	 * Called when a density scan is parsed.
	 * 
	 * @param density
	 * @param warpDensity
	 * @param navhaz
	 * @param anomaly
	 */
	void setDensityData(int density, int warpDensity, int navhaz, boolean anomaly) {
		// none of these values will be set to UNKNOWN
		this.density = density;			
		this.warpDensity = warpDensity;
		this.navhaz = navhaz;
		if(anomaly) {
			this.anomaly = Boolean.TRUE;
			// TODO fire anomaly event?
		}
		else {
			this.anomaly = null;	
		}
		this.densityDate = new Date();
		db.fireChanged(this);
	}
	
	/**
	 * Returns the date of your last holo scan, ether probe report, or
	 * presence in the sector.  Returns null if no holo data has been
	 * recorded.  The holo date is not updated when your probe is destroyed in
	 * the sector.
	 */
	public Date getHoloDate() {
		return holoDate;
	}
	
	void setHoloDate(Date holoDate) {
		this.holoDate = holoDate;
		db.fireChanged(this);
	}

	/**
	 * Returns the date of your last density scan, holo scan, ether probe
	 * report, or presence in the sector.  Returns null if the sector has not
	 * been scanned.  This date is not updated when your probe is destroyed in
	 * the sector.
	 */
	public Date getScanDate() {
		if(holoDate.getTime() == 0) return densityDate;
		if(densityDate.getTime() == 0) return holoDate;
		return densityDate.after(holoDate) ? densityDate : holoDate;
	}
	
	/**
	 * Returns true if the sector is avoided.
	 */
	public boolean isAvoided() {
		return avoided == Boolean.TRUE;
	}
	
	/**
	 * Sets the avoided state.
	 * 
	 * @param avoided
	 */
	void setAvoided(boolean avoided) {
		Boolean value = avoided ? Boolean.TRUE : null;
		if(this.avoided != value) {
			this.avoided = value;
			db.fireChanged(this);
		}
	}
	
	/**
	 * Returns true if the sector is part of a nebula.
	 */
	public boolean hasNebulaName() {
		return nebulaName != null;
	}

	/**
	 * Returns the sector's nebula name.
	 * 
	 * @return the nebula name, or null if none is known
	 */
	public String getNebulaName() {
		return nebulaName;
	}	
	
	void setNebulaName(String nebulaName) {
		// this shouldn't be set back to null, but who knows with the game edits
		if(this.nebulaName == null ? nebulaName != null : !this.nebulaName.equals(nebulaName)) {
			this.nebulaName = nebulaName;
			db.fireChanged(this);
		}
	}
	
	/**
	 * Returns true if the sector contains a beacon.
	 */
	public boolean hasBeaconMessage() {
		return beaconMessage != null;
	}
	
	/**
	 * Gets the beacon message.
	 * 
	 * @return the beacon message, or null if none is known
	 */
	public String getBeaconMessage() {
		return beaconMessage;
	}
	
	/**
	 * Sets the beacon message.
	 * 
	 * @param beaconMessage the beacon message, or null if there is none
	 */
	void setBeaconMessage(String beaconMessage) {
		// this could be set back to null
		if(this.beaconMessage == null ? beaconMessage != null : !this.beaconMessage.equals(beaconMessage)) {
			this.beaconMessage = beaconMessage;
			db.fireChanged(this);
		}
	}	
	
	/**
	 * Gets a note from this sector.
	 * 
	 * @param name the name of the note
	 * @return the content of the note
	 */
	public String getNote(String name) {
		return notes.get(name);
	}
	
	/**
	 * Sets a note on this sector.  To avoid conflicts, note names should be
	 * prefixed with a unique string such as the class or package name of the
	 * script that created them.
	 * 
	 * @param name the name of the note
	 * @param note the content of the note
	 * @throws NullPointerException if the name or the note is null
	 */
	public void setNote(String name, String note) {
		if(name == null || note == null) {
			throw new NullPointerException();
		}
		if(!notes.containsKey(name) || !notes.get(name).equals(note)) {
			notes.put(name, note);
			db.fireChanged(this);
		}
	}

	/**
	 * Removes a note from this sector.
	 * 
	 * @param name the name of the note
	 */
	public void removeNote(String name) {
		if(notes.containsKey(name)) {
			notes.remove(name);
			db.fireChanged(this);
		}
	}
	
	/**
	 * Returns the number of fighters the sector is known to contain.  Unlike
	 * most other numeric fields, this value defaults to zero.  A value of
	 * {@link Constants#UNKNOWN} indicates that enemy fighters destroyed one
	 * of your probes in the sector.
	 */
	public int getFighters() {
		if(fighters == null) {
			return 0;
		}
		else {
			return fighters;
		}
	}
	
	/**
	 * Gets the owner of the fighters in the sector.  Returns null if there
	 * are no fighters in the sector.
	 * 
	 * @see #getFighters()
	 */
	public Owner getFighterOwner() {
		if(fighters == null) {
			return null;
		}
		else {
			return db.resolveOwner(fighterOwner);
		}
	}
	
	/**
	 * Gets the fighter mode.  Returns null if there are no fighters in the
	 * sector.
	 * 
	 * @return the fighter mode; may be null
	 */
	public FighterMode getFighterMode() {
		return fighterMode;
	}
	
	/**
	 * Returns the date when the fighters in this sector were placed or
	 * discovered.  Returns null if there are no fighters in the sector.
	 */
	public Date getFighterDate() {
		return fighterDate;
	}

	// TODO setter for fighter info
	// also registerProbeHit(...) that does not overwrite known fighter data
	
//	/**
//	 * Returns the name of the last entity to hit your fighters in this
//	 * sector.  Includes hits by traders, aliens, probes, and Feds.  Returns
//	 * null if no fighter hit has been recorded in this sector.
//	 */
//	public String getFigHitName() {
//		synchronized(Database.lock) {
//			return figHitName;
//		}
//	}
//
//	/**
//	 * Returns the date of the last fighter hit in this sector.  Returns null
//	 * if no fighter hit has been recorded.
//	 */
//	public Date getFigHitDate() {
//		synchronized(Database.lock) {
//			return figHitDate;
//		}
//	}
//
//	void setFigHit(String name, Date date) {
//		synchronized(Database.lock) {
//			figHitName = name;
//			figHitDate = date;
//		}
//	}
	
//	private int armids = UNKNOWN;
//	private Owner armidOwner;
//
//	void setArmids(int armids, Owner owner) {
//		synchronized(Database.lock) {
//			this.armids = armids;
//			armidOwner = owner;
//		}
//	}
//
//	/**
//	 * Returns the number of armid mines in this sector.
//	 */
//	public int getArmids() {
//		synchronized(Database.lock) {
//			return armids;
//		}
//	}
//	
//	/**
//	 * Returns the owner of the armid mines in this sector.  Returns null if
//	 * there are no armid mines in this sector.
//	 */
//	public Owner getArmidOwner() {
//		synchronized(Database.lock) {
//			return armidOwner;
//		}
//	}
//	
//	private int limpets;
//	private Owner limpetOwner;
//	
//	void setLimpets(int limpets, Owner owner) {
//		synchronized(Database.lock) {
//			this.limpets = limpets;
//			limpetOwner = owner;
//		}
//	}
//
//	/**
//	 * Returns the number of limpets in this sector.  Only returns information
//	 * about your own limpets.
//	 */
//	public int getLimpets() {
//		synchronized(Database.lock) {
//			return limpets;
//		}
//	}
//	
//	/**
//	 * Returns the owner of the limpets in this sector.  Returns null if there
//	 * are no limpets in this sector.  Only returns information about your own
//	 * limpets.
//	 */
//	public Owner getLimpetOwner() {
//		synchronized(Database.lock) {
//			return limpetOwner;
//		}
//	}
	
//	/**
//	 * Returns true if this sector is known to contain a port.
//	 */
//	public boolean hasPort() {
//		return port != null;
//	}
	
//	/**
//	 * Returns the port in this sector.  Returns null if no port is known.
//	 */
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
	
	
	
	@Override
	public String toString() {
		return "Sector " + number;
	}
	
}
