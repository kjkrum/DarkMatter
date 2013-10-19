package com.chalcodes.weaponm.database;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Server login settings.
 */
public class LoginOptions implements Serializable {
	private static final long serialVersionUID = 1L;
	
	// acceptable names are 1-41 chars, but supplying a name is optional
	private static final Pattern namePattern = Pattern
			.compile("[ -\\}]{0,41}");
	// password can actually be blank
	private static final Pattern passwordPattern = Pattern
			.compile("[ -\\}]{0,8}");

	private String title = "";
	private String host = "";
	private int port = 23;
	private char letter = 'A';
	private String name = "";
	private String password = "";
	private boolean autoLogin = false;
	
	public static boolean isValidName(String name) {
		if(name == null) {
			return false;
		}
		else {
			return namePattern.matcher(name).matches();			
		}		
	}
	
	public static boolean isValidPassword(String password) {
		if(password == null) {
			return false;
		}
		else {
			return passwordPattern.matcher(password).matches();
		}
	}

	public String getTitle() {
		synchronized (Database.lock) {
			return title;
		}
	}

	public void setTitle(String title) {
		if(title == null) {
			title = "";
		}
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
		// TODO validate?
		if(host == null) {
			host = "";
		}
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
		if(port < 1 || port > 65535) {
			throw new IllegalArgumentException("invalid port: " + port);
		}
		synchronized (Database.lock) {
			this.port = port;
		}
	}

	public char getGameLetter() {
		synchronized (Database.lock) {
			return letter;
		}
	}

	public void setGameLetter(char letter) {
		if(letter < 'A' || letter == 'Q' || letter > 'Z') {
			throw new IllegalArgumentException("invalid game letter: " + letter);
		}
		synchronized (Database.lock) {
			this.letter = letter;
		}
	}

	public String getUserName() {
		synchronized (Database.lock) {
			return name;
		}
	}

	public void setUserName(String name) {
		if(!isValidName(name)) {
			throw new IllegalArgumentException("invalid user name: " + name);
		}
		if("".equals(name) && isAutoLogin()) {
			throw new IllegalStateException("user name cannot be empty when auto-login is true");
		}
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
		if(!isValidPassword(password)) {
			throw new IllegalArgumentException("invalid password: " + password);
		}
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
		if(autoLogin && "".equals(getUserName())) {
			throw new IllegalStateException("auto-login cannot be true when user name is empty");
		}
		synchronized (Database.lock) {
			this.autoLogin = autoLogin;
		}
	}

}
