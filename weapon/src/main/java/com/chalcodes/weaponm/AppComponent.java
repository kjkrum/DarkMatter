package com.chalcodes.weaponm;

import dagger.Component;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
	void inject(@Nonnull final WeaponM weapon);
}
