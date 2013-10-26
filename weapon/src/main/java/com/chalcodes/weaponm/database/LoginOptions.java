package com.chalcodes.weaponm.database;

import java.util.regex.Pattern;

/**
 * Server login settings.
 */
public class LoginOptions extends DataObject {
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
			if(this.title.equals(title)) {
				this.title = title;
				fireChanged();
			}
		}
	}

	public String getHost() {
		synchronized (Database.lock) {
			return host;
		}
	}

	public void setHost(String host) {
		if(host == null) {
			host = "";
		}
		synchronized (Database.lock) {
			if(!this.host.equals(host)) {
				this.host = host;
				fireChanged();
			}
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
			if(this.port != port) {
				this.port = port;
				fireChanged();
			}
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
			if(this.letter != letter) {
				this.letter = letter;
				fireChanged();
			}
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
			if(!this.name.equals(name)) {
				this.name = name;
				fireChanged();
			}
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
			if(!this.password.equals(password)) {
				this.password = password;
				fireChanged();
			}
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
			if(this.autoLogin != autoLogin) {
				this.autoLogin = autoLogin;
				fireChanged();
			}			
		}
	}
}
