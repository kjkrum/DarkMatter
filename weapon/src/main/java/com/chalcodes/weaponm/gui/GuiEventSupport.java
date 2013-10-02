package com.chalcodes.weaponm.gui;

import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.EventListener;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

/**
 * All events will be dispatched in the UI thread.  The network thread will
 * generate events and pass collections of them to the UI thread in runnable
 * tasks.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
class GuiEventSupport extends EventSupport {

	@Override
	public void dispatchEvent(Event event) {
		EventType type = event.getType();
		if(listeners.containsKey(type)) {
			for(EventListener listener : listeners.get(type)) {
				try {
					listener.onEvent(event);
				} catch(Throwable t) {
					// TODO if listener is script, unload it
				}
			}
		}		
	}
	
}
