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
	 * Game text.
	 * <p>
	 * Parameters: {@link EventParam#TEXT}
	 */
	GAME_TEXT,
	
	DB_OPENED,
	
	DB_CLOSED,
	
	DB_DIRTY,
	
	DB_SAVED,

	NET_DISCONNECTED,
	
	NET_CONNECTING,
	
	NET_CONNECTED,
	
	/**
	 * A network error.
	 * <p>
	 * Parameters: {@link EventParam#ERROR}
	 */
	NET_ERROR

}
