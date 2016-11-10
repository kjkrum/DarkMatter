package com.chalcodes.weaponm;

import com.chalcodes.event.ClassBusFactory;
import com.chalcodes.event.EventReceiver;
import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.PluginEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Action;
import java.awt.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
abstract public class Plugin {
	private ClassBusFactory mEventBuses;

	/**
	 * Sets private fields that we don't want to expose as constructor
	 * arguments.
	 */
	void init(@Nonnull final ClassBusFactory eventBuses) {
		mEventBuses = eventBuses;
	}

	private final Map<EventReceiver<? extends Event>, Class<? extends Event>> mRegisteredReceivers = new HashMap<>();

	/**
	 * Registers an event receiver.
	 *
	 * @param klass the event class
	 * @param receiver the event receiver
	 * @param <T> the event type
	 */
	protected <T extends Event> void register(@Nonnull final Class<T> klass,
	                                          @Nonnull final EventReceiver<T> receiver) {
		mEventBuses.getBus(klass).register(receiver);
		mRegisteredReceivers.put(receiver, klass);
	}

	/**
	 * Unregisters an event receiver.
	 *
	 * @param receiver the receiver to unregister
	 * @param <T> the event type
	 */
	protected <T extends Event> void unregister(@Nonnull final EventReceiver<T> receiver) {
		if(mRegisteredReceivers.containsKey(receiver)) {
			// noinspection unchecked - I think this is unavoidable
			final Class<T> klass = (Class<T>) mRegisteredReceivers.get(receiver);
			mEventBuses.getBus(klass).unregister(receiver);
			mRegisteredReceivers.remove(receiver);
		}
	}

	/**
	 * Broadcasts a custom event.
	 *
	 * @param event the event
	 * @param <T> the event type
	 */
	protected <T extends PluginEvent> void broadcast(@Nonnull final T event) {
		// noinspection unchecked - I think this is unavoidable
		final Class<T> klass = (Class<T>) event.getClass();
		mEventBuses.getBus(klass).broadcast(event);
	}

	// UI stuff:

	/**
	 * Gets the name of this plugin. The name will appear in the plugin menu.
	 * The default implementation returns the simple class name.
	 *
	 * @return the plugin name
	 */
	@Nonnull public String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * Gets the UI actions associated with this plugin. The actions will
	 * appear in the plugin menu and action bar. The default implementation
	 * returns an empty list.
	 *
	 * @return the actions
	 */
	@Nonnull public List<Action> getActions() {
		return Collections.emptyList();
	}

	/**
	 * Gets the UI component associated with this plugin. The component will
	 * be displayed in a dockable frame. The default implementation returns
	 * null.
	 *
	 * @return the component, or null
	 */
	@Nullable public Component getComponent() {
		return null;
	}

	// TODO lifecycle methods
}
