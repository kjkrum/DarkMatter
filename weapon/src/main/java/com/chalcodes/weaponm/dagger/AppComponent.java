package com.chalcodes.weaponm.dagger;

import dagger.Component;
import org.neo4j.ogm.session.SessionFactory;

import javax.inject.Singleton;

/**
 * Dagger component.
 *
 * @author Kevin Krumwiede
 */
@Singleton
@Component(modules=AppModule.class)
public interface AppComponent {
	void inject(EagerSingletons eager);
	SessionFactory getSessionFactory();
}
