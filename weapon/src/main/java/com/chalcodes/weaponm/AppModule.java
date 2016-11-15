package com.chalcodes.weaponm;

import com.chalcodes.event.*;
import com.chalcodes.weaponm.entity.Game;
import dagger.Module;
import dagger.Provides;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * Dagger module.
 *
 * @author Kevin Krumwiede
 */
@Module
class AppModule {
	@Provides
	@Nonnull
	@Singleton
	File dataDir() {
		try {
			return Environment.getDataDir();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	@Nonnull
	@Singleton
	ClassBusFactory eventBuses() {
		final Executor swingExecutor = new Executor() {
			@Override
			public void execute(@Nonnull final Runnable command) {
				SwingUtilities.invokeLater(command);
			}
		};
		final EventBus<Exception> exceptionBus = new SimpleEventBus<>(swingExecutor, null);
		final ReceiverSetFactory<Object> receiverSetFactory = new ReceiverSetFactory<Object>() {
			@Nonnull
			@Override
			public Set<EventReceiver<Object>> newSet(@Nonnull final Set<EventReceiver<Object>> current) {
				return new RotatingIteratorSet<>(current);
			}
		};
		final UncaughtExceptionHandler<Object> uncaughtExceptionHandler = UncaughtExceptionHandlers.report();
		final BusFactory<Object> defaultBusFactory =
				new SimpleBusFactory<>(swingExecutor, exceptionBus, receiverSetFactory, uncaughtExceptionHandler);
		final Map<Class<?>, EventBus<?>> initialMappings = new HashMap<>();
		initialMappings.put(Game.class,
				new StickyEventBus<>(swingExecutor, exceptionBus, receiverSetFactory, uncaughtExceptionHandler));
		// TODO create other buses that need to be sticky or whatever
		return new ClassBusFactory(defaultBusFactory, initialMappings);
	}

	@Provides
	@Nonnull
	@Singleton
	DatabaseManager databaseManager(@Nonnull final File dataDir, @Nonnull ClassBusFactory eventBuses) {
		try {
			return new DatabaseManager(dataDir, eventBuses.getBus(Game.class));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Provides
	@Nonnull
	@Singleton
	NetworkManager networkManager(@Nonnull final ClassBusFactory eventBuses) {
		return new NetworkManager(eventBuses);
	}

	@Provides
	@Nonnull
	@Singleton
	PluginManager pluginManager(@Nonnull final File dataDir, @Nonnull final ClassBusFactory eventBuses) {
		return new PluginManager(dataDir, eventBuses);
	}

	@Provides
	@Nonnull
	@Singleton
	Ui ui(@Nonnull final File dataDir,
	      @Nonnull final DatabaseManager databaseManager,
	      @Nonnull final NetworkManager networkManager,
	      @Nonnull final PluginManager pluginManager) {
		return new Ui(dataDir, databaseManager, networkManager, pluginManager);
	}
}
