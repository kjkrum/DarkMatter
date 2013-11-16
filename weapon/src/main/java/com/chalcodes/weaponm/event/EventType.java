package com.chalcodes.weaponm.event;

/**
 * Event type constants.
 * 
 * @see WeaponEvent
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public enum EventType {
	// TODO is source ever used?
	
	/**
	 * The complete content of a network read.  Scripts cannot register for
	 * this event.  It is intended only for terminal and log output.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>null</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>String</td></tr>
	 * </table>
	 */
	TEXT_RECEIVED,
	
	/**
	 * Text that was matched and consumed by the data parser.  Scripts can use
	 * this to detect minor events that are not recognized by the data parser.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>null</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>String</td></tr>
	 * </table>
	 */
	TEXT_MATCHED,
	
	// TODO TEXT_TYPED will go away... everything needs direct
	// access to network manager to receive NLEs
	
	/**
	 * Text that was typed in the terminal or otherwise queued for output.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>null</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>char[]</td></tr>
	 * </table>
	 */
	TEXT_TYPED,
	
	/**
	 * The database loaded status.  Scripts cannot register for this event,
	 * since script lifecycles are bounded by the database lifecycle.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>null</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>{@link DatabaseStatus}</td></tr>
	 * </table>
	 */
	DATABASE_STATUS,
	
	/**
	 * Fired when the database is modified or saved.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>null</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>Boolean</td></tr>
	 * </table>
	 */
	DATABASE_DIRTY,
	
	/**
	 * Fired when the database title is loaded or set.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>null</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>String</td></tr>
	 * </table>
	 */
	DATABASE_TITLE,
	
	/**
	 * The network connection status.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>null</td></tr>
	 * <tr><td>oldValue</td><td>{@link NetworkStatus}</td></tr>
	 * <tr><td>newValue</td><td>{@link NetworkStatus}</td></tr>
	 * </table>
	 */
	NETWORK_STATUS,

	/**
	 * Details of an error in the network thread.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>null</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>Throwable</td></tr>
	 * </table>
	 */
	NETWORK_ERROR

}
