package com.chalcodes.weaponm;

import dagger.Component;

import javax.inject.Singleton;

/**
 * Dagger component.
 *
 * @author Kevin Krumwiede
 */
@Singleton
@Component(modules = AppModule.class)
interface AppComponent {
	Ui ui();
}
