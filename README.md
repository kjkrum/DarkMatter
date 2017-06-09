Weapon M: Dark Matter
=====================
Dark Matter is a complete rewrite of [Weapon M](http://github.org/kjkrum/WeaponM), a cross-platform client for the classic BBS game [TradeWars 2002](http://classictw.com).

This is a very early preview. Weapon M helped me land a job as an Android developer, but it never had the impact on the game community that I hoped it would. Now I'm applying the experience I've gained to a new and greatly improved version.

# Configuration
If you run Weapon M with no configuration, it chooses a default location for its configuration and data files based on what OS you're running.

* Windows: `%LOCALAPPDATA%\WeaponM` or `%APPDATA%\WeaponM`
* OS X: `$HOME/Library/Application Support/WeaponM`
* Linux: `$XDG_DATA_HOME/WeaponM` or `$HOME/.local/share/WeaponM`
* Other, assumes generic Unix: `$HOME/.WeaponM`

You can change these locations by setting an environment variable named `WEAPON_DATA` that identifies the directory to use.

The database configuration is in a text file named `ogm.properties` located in the `WEAPON_DATA` directory. The default configuration uses an embedded Neo4j server that runs inside Weapon M and stores its database under the `WEAPON_DATA` directory. Other options are explained in the [Neo4j OGM Reference](https://neo4j.com/docs/ogm-manual/current/reference/#reference:configuration).



