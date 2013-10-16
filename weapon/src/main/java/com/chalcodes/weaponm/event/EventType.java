package com.chalcodes.weaponm.event;

/**
 * Event type constants.
 * 
 * @see Event
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public enum EventType {
	/**
	 * The complete content of a network read.  Scripts cannot register for
	 * this event.
	 * <p>
	 * Parameters: {@link EventParam#TEXT}
	 */
	TEXT_RECEIVED,
	
	/**
	 * Text that was matched and consumed by the data parser.  Scripts can use
	 * this to detect minor events that are not recognized by the data parser.
	 * <p>
	 * Parameters: {@link EventParam#TEXT}
	 */
	TEXT_MATCHED,
	
	/**
	 * Text that was typed in the terminal or otherwise queued for output.
	 * 
	 * Parameters: {@link EventParam#TEXT}
	 */
	TEXT_TYPED,
	
	/**
	 * Fired when a database is created or opened.  It's pointless for scripts
	 * to register for this or any database event, since script lifecycles are
	 * bounded by the database lifecycle.
	 */
	DB_OPENED,
	
	/**
	 * Fired when a database is closed.
	 */
	DB_CLOSED,
	
	/**
	 * Fired when the database is modified.
	 */
	DB_DIRTY,
	
	/**
	 * Fired when the database is saved.
	 */
	DB_SAVED,
	
	/**
	 * Fired when the game title is changed.
	 */
	DB_TITLE,

	/**
	 * Fired when the network is disconnected.
	 */
	NET_DISCONNECTED,
	
	/**
	 * Fired when a connection attempt begins.
	 */
	NET_CONNECTING,
	
	/**
	 * Fired when a connection attempt succeeds.
	 */
	NET_CONNECTED,
	
	/**
	 * Details of an error in the network thread.
	 * <p>
	 * Parameters: {@link EventParam#ERROR}
	 */
	NET_ERROR

}
