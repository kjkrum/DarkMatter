package com.chalcodes.weaponm.dagger;

import org.neo4j.ogm.session.SessionFactory;

import javax.inject.Inject;

/**
 * Singletons that may throw exceptions from their constructors. We inject a
 * throwaway instance of this class at startup to prevent the Dagger component
 * from throwing exceptions later.
 *
 * @author Kevin Krumwiede
 */
public class EagerSingletons {
	@Inject
	SessionFactory gSessionFactory;
}
