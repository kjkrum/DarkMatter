package com.chalcodes.weaponm.event;

public enum DatabaseStatus {
	/**
	 * Database created or opened.  This should set the database title and
	 * restore the docking frames layout.
	 */
	OPEN,
	
	/**
	 * Database closing.  This should stop the network and unload all scripts.
	 */
	CLOSING,
	
	/**
	 * Database closed.  This should clear the database title and hide the
	 * docking frames.
	 */
	CLOSED
}
