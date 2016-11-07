package com.chalcodes.weaponm;

import dagger.Module;
import dagger.Provides;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import java.io.File;

/**
 * Dagger module.  Makes all the things.
 *
 * @author Kevin Krumwiede
 */
@Module
class AppModule {
	@Nonnull private final File mDataDir;

	AppModule(@Nonnull final File dataDir) {
		mDataDir = dataDir;
	}

	@Provides
	@Singleton
	@Nonnull
	Database database() {
		return new Database();
	}

	@Provides
	@Singleton
	@Nonnull
	Network network() {
		return new Network();
	}

	@Provides
	@Singleton
	@Nonnull
	Plugins plugins() {
		return new Plugins();
	}

	@Provides
	@Singleton
	@Nonnull
	Ui ui(@Nonnull final Database database,
	      @Nonnull final Network network,
	      @Nonnull final Plugins plugins) {
		return new Ui(database, network, plugins, mDataDir);
	}
}
