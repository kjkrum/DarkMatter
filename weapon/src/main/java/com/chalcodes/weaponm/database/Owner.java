package com.chalcodes.weaponm.database;

/**
 * Marker interface for things that can be owners.  If needed, the actual
 * class of an <tt>Owner</tt> may be identified with the <tt>instanceof</tt>
 * operator.
 */
public interface Owner {
	
	/**
	 * Returns the owner's name.
	 */
	public String getName();
	
}
