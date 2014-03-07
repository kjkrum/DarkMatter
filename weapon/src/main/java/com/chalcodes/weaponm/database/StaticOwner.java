package com.chalcodes.weaponm.database;

/**
 * Special static owners.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
class StaticOwner implements Owner {
	private static final Owner[] instances = new Owner[3];
	static final int ID_ROGUE = 0;
	static final int ID_FED = -2;

	static {
		instances[0] = new StaticOwner("Rogue Mercenaries", ID_ROGUE);
		instances[1] = new StaticOwner("unknown", Constants.UNKNOWN);
		instances[2] = new StaticOwner("The Federation", ID_FED);
	}

	static Owner forId(int id) {
		return instances[-id];
	}
		
	private final String name;
	private final int id;

	private StaticOwner(String name, int id) {
		this.name = name;
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getOwnerId() {
		return id;
	}
}
