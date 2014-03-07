package com.chalcodes.weaponm.event;

import java.beans.PropertyChangeEvent;

public class WeaponEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;
	private final EventType type;

	/**
	 * Creates a new event.
	 * 
	 * @param source the source of the event
	 * @param type the event type
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public WeaponEvent(Object source, EventType type, Object oldValue, Object newValue) {
		super(source, type.name(), oldValue, newValue);
		this.type = type;
	}

	/**
	 * Gets the enum type of this event.  This can be used instead of
	 * {@link #getPropertyName()} to distinguish the event type.
	 * 
	 * @return the event type
	 */
	public EventType getType() {
		return type;
	}

}
