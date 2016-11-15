package com.chalcodes.weaponm;

import com.chalcodes.event.ClassBusFactory;
import com.chalcodes.event.EventReceiver;
import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.PluginEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Action;
import java.awt.Component;
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
	final void setPrivateFields(@Nonnull final ClassBusFactory eventBuses) {
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
	protected final <T extends Event> void register(@Nonnull final Class<T> klass,
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
	protected final <T extends Event> void unregister(@Nonnull final EventReceiver<T> receiver) {
		if(mRegisteredReceivers.containsKey(receiver)) {
			// noinspection unchecked - I think this is unavoidable
			final Class<T> klass = (Class<T>) mRegisteredReceivers.get(receiver);
			mEventBuses.getBus(klass).unregister(receiver);
			mRegisteredReceivers.remove(receiver);
		}
	}

	/**
	 * Unregisters all event receivers.
	 */
	public final void unregisterReceivers() {
		for(Map.Entry<EventReceiver<? extends Event>, Class<? extends Event>> entry : mRegisteredReceivers.entrySet()) {
			// noinspection unchecked - I think this is unavoidable
			mEventBuses.getBus(entry.getValue()).unregister((EventReceiver) entry.getKey());
		}
		mRegisteredReceivers.clear();
	}

	/**
	 * Broadcasts a custom event.
	 *
	 * @param event the event
	 * @param <T> the event type
	 */
	protected final <T extends PluginEvent> void broadcast(@Nonnull final T event) {
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
	 * returns null.
	 *
	 * @return the actions
	 */
	@Nullable public List<Action> getActions() {
		return null;
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

	/**
	 * Initializes this plugin.  This is called once when the plugin is
	 * loaded.  To make a plugin enable itself automatically, override this
	 * method and have it call {@link #enable}.
	 */
	public void init() {}

	/**
	 * Enables this plugin.  This is where plugins should register their event
	 * receivers.  Receivers are unregistered automatically when the plugin is
	 * disabled.
	 */
	public void enable() {}

	/**
	 * Override this method to close any system resources used by this plugin,
	 * such as files or sockets.  This is called when the plugin is disabled.
	 */
	public void closeSystemResources() {}

	/**
	 * Disables this plugin.
	 */
	public final void disable() {
		unregisterReceivers();
		closeSystemResources();
	}
}
