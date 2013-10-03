package com.chalcodes.weaponm.database;

import java.io.Serializable;

/**
 * Server login settings.
 */
public class LoginOptions implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title = "";
	private String host = "";
	private int port = 23;
	private char game = 'A';
	private String name = "";
	private String password = "";
	private boolean autoLogin = false;

	public String getTitle() {
		synchronized (Database.lock) {
			return title;
		}
	}

	public void setTitle(String title) {
		synchronized (Database.lock) {
			this.title = title;
		}
	}

	public String getHost() {
		synchronized (Database.lock) {
			return host;
		}
	}

	public void setHost(String host) {
		synchronized (Database.lock) {
			this.host = host;
		}
	}

	public int getPort() {
		synchronized (Database.lock) {
			return port;
		}
	}

	public void setPort(int port) {
		synchronized (Database.lock) {
			this.port = port;
		}
	}

	public char getGame() {
		synchronized (Database.lock) {
			return game;
		}
	}

	public void setGame(char game) {
		synchronized (Database.lock) {
			this.game = game;
		}
	}

	public String getName() {
		synchronized (Database.lock) {
			return name;
		}
	}

	public void setName(String name) {
		synchronized (Database.lock) {
			this.name = name;
		}
	}

	public String getPassword() {
		synchronized (Database.lock) {
			return password;
		}
	}

	public void setPassword(String password) {
		synchronized (Database.lock) {
			this.password = password;
		}
	}

	public boolean isAutoLogin() {
		synchronized (Database.lock) {
			return autoLogin;
		}
	}

	public void setAutoLogin(boolean autoLogin) {
		synchronized (Database.lock) {
			this.autoLogin = autoLogin;
		}
	}

}
