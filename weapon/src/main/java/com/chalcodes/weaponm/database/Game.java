package com.chalcodes.weaponm.database;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.typeconversion.CompositeAttributeConverter;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Identifies a specific "bang" of a game. Since TWGS does not provide game
 * IDs, we identify games by their unique combination of host, port, game
 * letter, and start date.
 *
 * @author Kevin Krumwiede
 */
@NodeEntity
public class Game {
	@GraphId
	private Long mGraphId;

	/**
	 * Required by OGM.
	 */
	public Game() {}

	public Game(@Nonnull final String host, final int port, final char letter, @Nonnull final GameStats stats) {
		mHost = host;
		mPort = port;
		mLetter = letter;
		mStats = stats;
	}

	@Property(name="host")
	private String mHost;

	@Property(name="port")
	private int mPort;

	@Property(name="letter")
	private char mLetter;

	public String getHost() {
		return mHost;
	}

	public int getPort() {
		return mPort;
	}

	public char getLetter() {
		return mLetter;
	}

	@Property(name="stats")
	private GameStats mStats;

	public GameStats getStats() {
		return mStats;
	}

	// TODO sectors, etc.

}
