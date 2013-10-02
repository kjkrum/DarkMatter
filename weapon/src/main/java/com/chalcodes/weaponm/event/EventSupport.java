package com.chalcodes.weaponm.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chalcodes.weaponm.LogName;

/**
 * Manages a collection of event listners.  The {@link #dispatchEvent(Event)}
 * method should be implemented to appropriately handle exceptions propagated
 * from listeners.  This class is not thread safe.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
abstract public class EventSupport {
	protected final Logger log = LoggerFactory.getLogger(LogName.forObject(this));
	protected final Map<EventType, Set<EventListener>> listeners =
			new HashMap<EventType, Set<EventListener>>();

	public void addEventListener(EventListener listener, EventType type) {
		if(listener == null || type == null) {
			throw new NullPointerException();
		}
		if(!listeners.containsKey(type)) {
			listeners.put(type, new HashSet<EventListener>());
		}
		listeners.get(type).add(listener);
	}
	
	public void removeEventListener(EventListener listener, EventType type) {
		if(listener == null || type == null) {
			return;
		}
		Set<EventListener> set = listeners.get(type);
		if(set != null) {
			set.remove(listener);
			if(set.isEmpty()) {
				listeners.remove(type);
			}
		}
	}
	
	public void removeEventListener(EventListener listener) {
		if(listener == null) {
			return;
		}
		Set<EventType> copy = new HashSet<EventType>(listeners.keySet()); 
		for(EventType type : copy) {
			removeEventListener(listener, type);
		}
	}
	
	public void removeEventListeners() {
		listeners.clear();
	}
	
	abstract public void dispatchEvent(Event event);

	/**
	 * Convenience method to dispatch a new event.
	 * 
	 * @param type the event type
	 * @param params pairs of corresponding {@link EventType}s and values
	 */
	public void dispatchEvent(EventType type, Object... params) {
		dispatchEvent(new Event(type, params));		
	}

}
