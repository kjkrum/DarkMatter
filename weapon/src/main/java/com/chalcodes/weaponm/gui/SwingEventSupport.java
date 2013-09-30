package com.chalcodes.weaponm.gui;

import javax.swing.SwingUtilities;

import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.EventListener;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

/**
 * A version of <tt>EventSupport</tt> that guarantees its listeners will be
 * notified in the UI thread.  Events may be dispatched from any thread, but
 * listeners should be added and removed only from the UI thread.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
class SwingEventSupport extends EventSupport {

	@Override
	public void dispatchEvent(final Event event) {
		if(SwingUtilities.isEventDispatchThread()) {
			EventType type = event.getType();
			if(listeners.containsKey(type)) {
				for(EventListener listener : listeners.get(type)) {
					listener.onEvent(event);
				}
			}
		}
		else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					dispatchEvent(event);
				}
			});
		}
	}

}
