package com.chalcodes.weaponm.event;

/**
 * Interface for classes that want to receive events.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public interface EventListener {
	void onEvent(Event event);
}
