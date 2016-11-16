package com.chalcodes.weaponm;

import com.chalcodes.event.ClassBusFactory;
import com.chalcodes.event.EventReceiver;
import com.chalcodes.weaponm.event.Event;
import com.chalcodes.weaponm.event.PluginEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Action;
import javax.swing.JMenu;
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
	 * If the name is null or empty, the simple class name will be used.
	 *
	 * @return the plugin name
	 */
	@Nullable public String getName() {
		return null;
	}

	@Nonnull final String resolveName() {
		final String name = getName();
		return name == null || name.isEmpty() ? getClass().getSimpleName() : name;
	}

	/**
	 * Gets the UI actions for this plugin. The default implementation returns
	 * null.
	 *
	 * @return the actions, or null
	 */
	@Nullable public List<Action> getActions() {
		return null;
	}

	/**
	 * Gets the menu for this plugin. This will appear as a submenu of the
	 * plugins menu. The default implementation returns a menu based on {@link
	 * #getName()} and {@link #getActions()}, or null if {@code getActions()}
	 * returns null.
	 *
	 * @return the menu, or null
	 */
	@Nullable public JMenu getMenu() {
		final List<Action> actions = getActions();
		if(actions != null && !actions.isEmpty()) {
			final JMenu menu = new JMenu(resolveName());
			for(final Action action : actions) {
				menu.add(action);
			}
			return menu;
		}
		else {
			return null;
		}
	}

	/**
	 * Get the dockable UI for this plugin. The default implementation returns
	 * null.
	 *
	 * @return the dockable, or null
	 */
	@Nullable public Dockable getDockable() {
		return null;
	}

	/**
	 * Initializes this plugin. This is called once when the plugin is loaded.
	 * To make a plugin enable itself automatically, override this method and
	 * have it call {@link #enable}.
	 */
	public void init() {}

	/**
	 * Enables this plugin. This is where plugins should register their event
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
