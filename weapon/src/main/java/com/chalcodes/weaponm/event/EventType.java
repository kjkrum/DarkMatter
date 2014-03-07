package com.chalcodes.weaponm.event;

import com.chalcodes.weaponm.database.Database;
import com.chalcodes.weaponm.database.Sector;
import com.chalcodes.weaponm.network.NetworkState;

/**
 * Event type constants.
 * 
 * @see WeaponEvent
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public enum EventType {
	// ******************** DATABASE ********************
	
	/**
	 * Fired when the database is loaded or closed.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>{@link Database}</td></tr>
	 * <tr><td>oldValue</td><td>Boolean</td></tr>
	 * <tr><td>newValue</td><td>Boolean</td></tr>
	 * </table>
	 */
	DATABASE_LOADED,
	
	/**
	 * Fired when the database is modified or saved.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>{@link Database}</td></tr>
	 * <tr><td>oldValue</td><td>Boolean</td></tr>
	 * <tr><td>newValue</td><td>Boolean</td></tr>
	 * </table>
	 */
	DATABASE_DIRTY,
	
	/**
	 * Fired when the database title is loaded or set.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>{@link Database}</td></tr>
	 * <tr><td>oldValue</td><td>String</td></tr>
	 * <tr><td>newValue</td><td>String</td></tr>
	 * </table>
	 */
	DATABASE_TITLE,
	
	/**
	 * Fired when the database is initialized.  This happens the first time
	 * you view the game stats, or when a previously initialized database is
	 * loaded.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>{@link Database}</td></tr>
	 * <tr><td>oldValue</td><td>Boolean</td></tr>
	 * <tr><td>newValue</td><td>Boolean</td></tr>
	 * </table>
	 */
	DATABASE_INITIALIZED,
	
	// ******************** NETWORK ********************
	
	/**
	 * The network connection state.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>undefined</td></tr>
	 * <tr><td>oldValue</td><td>{@link NetworkState}</td></tr>
	 * <tr><td>newValue</td><td>{@link NetworkState}</td></tr>
	 * </table>
	 */
	NETWORK_STATE,

	/**
	 * Details of an error in the network thread.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>undefined</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>Throwable</td></tr>
	 * </table>
	 */
	NETWORK_ERROR,
	
	// ******************** TEXT ********************
	
	/**
	 * The complete content of a network read.  Scripts cannot register for
	 * this event.  It is intended only for terminal and log output.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>undefined</td></tr>
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
	 * <tr><td>source</td><td>undefined</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>String</td></tr>
	 * </table>
	 */
	TEXT_MATCHED,
	
	/**
	 * Text that was typed or pasted into the terminal.  This kind of output
	 * will not be retried if the network is locked when it is sent.
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>undefined</td></tr>
	 * <tr><td>oldValue</td><td>null</td></tr>
	 * <tr><td>newValue</td><td>String</td></tr>
	 * </table>
	 */
	TEXT_TYPED,
	
	// ******************** SCRIPTS & DATA ********************
	
	/**
	 * 
	 * <p>
	 * <table border="1">
	 * <tr><td>source</td><td>{@link Database}</td></tr>
	 * <tr><td>oldValue</td><td>{@link Sector}</td></tr>
	 * <tr><td>newValue</td><td>{@link Sector}</td></tr>
	 * </table>
	 */
	WARP_DISCOVERED, 

}
