package com.chalcodes.weaponm.database;

/**
 * Interface for data objects.  Every first-class entity in the database is a
 * data object, including the database itself.  Data objects are the source of
 * data change events.  Setters in data objects should fire change events if
 * and only if the value of a field changes. 
 * <p>
 * A data change event is fired only for the object that changed, not for its
 * parent.  For example, change events are fired on the database only when one
 * of its own fields changes, not when a field of one of its children changes.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public interface DataObject {
	/**
	 * Gets the database that this data object belongs to.
	 * 
	 * @return the database
	 */
	Database getDatabase();
	
//	/**
//	 * Gets the data object ID.  Scripts should not concern themselves with
//	 * data object IDs.  They exist as a way of expressing object references
//	 * when the database is saved and loaded, and their encoding schemes are
//	 * subject to change.  Entities that have visible numeric IDs in the game
//	 * will provide other methods like {@link Sector#getNumber()} to access
//	 * them.
//	 */
//	int getId();
}
