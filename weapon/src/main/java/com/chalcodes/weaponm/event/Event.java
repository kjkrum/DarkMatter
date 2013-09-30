package com.chalcodes.weaponm.event;

import java.util.HashMap;
import java.util.Map;

/**
 * A game or client event.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class Event {
	private final EventType type;
	private final Map<EventParam, Object> params;
	
	/**
	 * Makes a shallow copy of the properties map, which may be null.
	 * 
	 * @param type the event type
	 * @param params extra values associated with the event
	 */
	public Event(EventType type, Map<EventParam, Object> params) {
		this.type = type;
		if(params != null) {
			this.params = new HashMap<EventParam, Object>(params);
		}
		else {
			this.params = null;	
		}
	}
	
	public EventType getType() {
		return type;
	}
	
	public Object getParam(EventParam key) {
		if(params != null) {
			return params.get(key);
		}
		else {
			return null;			
		}
	}

}
