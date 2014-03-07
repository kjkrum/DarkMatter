package com.chalcodes.weaponm.database;

/**
 * Interface for entities that can own planets, fighters, etc.  Owners include
 * traders, corporations, alien races, and The Federation.
 */
public interface Owner {
	/**
	 * Gets the owner's name.
	 * 
	 * @return the owner's name
	 */
	String getName();
	
	/**
	 * Gets the owner ID.  Scripts should not concern themselves with owner
	 * IDs.  They exist to encode object references when the database is saved
	 * and loaded, and their encoding scheme is subject to change.
	 * 
	 * @return the owner ID
	 */
	int getOwnerId();

}
