package com.chalcodes.weaponm.database;

import java.util.regex.Pattern;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.chalcodes.weaponm.event.EventType;

/**
 * Server login settings.
 */
@XmlRootElement
public class LoginOptions implements DataObject {
	// acceptable names are 1-41 chars, but supplying a name is optional
	private static final Pattern namePattern = Pattern.compile("[ -\\}]{0,41}");
	// password can actually be blank
	private static final Pattern passwordPattern = Pattern.compile("[ -\\}]{0,8}");
	private static final int DEFAULT_PORT = 23;
	
	@XmlTransient
	private Database db;
	// using PascalCase for tags and camelCase for attributes
	// just because I find it aesthetically pleasing
	@XmlElement(name="Title")
	private String title = "";
	@XmlElement(name="Host")
	private String host = "";
	@XmlElement(name="Port")
	private int port = DEFAULT_PORT;
	@XmlElement(name="GameLetter")
	private char gameLetter = 'A';
	@XmlElement(name="LoginName")
	private String loginName = "";
	@XmlElement(name="Password")
	private String password = "";
	@XmlElement(name="AutoLogin")
	private boolean autoLogin = false;
	
	/**
	 * Creates a new login options with the specified database as its parent.
	 * 
	 * @param db the parent database
	 */
	LoginOptions(Database db) {
		this.db = db;
	}
	
	/**
	 * Required by JAXB.
	 */
	@SuppressWarnings("unused")
	private LoginOptions() {}
	
	/**
	 * Restores the reference to the parent database.
	 * 
	 * @param u the JAXB unmarshaller
	 * @param parent the parent object
	 */
	@SuppressWarnings("unused")
	private void afterUnmarshal(Unmarshaller u, Object parent) {
		db = (Database) parent;
	}
	
	@Override
	public Database getDatabase() {
		return db;
	}
	
	/**
	 * Tests if a login name is valid.  Valid names are 1-41 characters, but
	 * this method also accepts an empty string because recording a login name
	 * in the database is optional.  Valid characters range from space (32) to
	 * the right curly brace (126).
	 * 
	 * @param name the name to test
	 * @return true if the name is valid; otherwise false
	 */
	public static boolean isValidName(String name) {
		if(name == null) {
			return false;
		}
		else {
			return namePattern.matcher(name).matches();			
		}		
	}
	
	/**
	 * Tests if a password is valid.  Valid passwords are 0-8 characters.
	 * (Yes, the game actually accepts a blank password.)  Valid characters
	 * range from space (32) to the right curly brace (126).
	 * 
	 * @param password the password to test
	 * @return true if the password is valid; otherwise false
	 */
	public static boolean isValidPassword(String password) {
		if(password == null) {
			return false;
		}
		else {
			return passwordPattern.matcher(password).matches();
		}
	}

	/**
	 * Gets the game title.  This is a descriptive name displayed in window
	 * titles and elsewhere. 
	 * 
	 * @return the game title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the game title.
	 * 
	 * @param newTitle the game title
	 */
	public void setTitle(String newTitle) {
		if(newTitle == null) {
			newTitle = "";
		}
		if(!this.title.equals(newTitle)) {
			String oldTitle = this.title;
			this.title = newTitle;
			if(db != null) {
				db.fireChanged(this);
				db.fireEvent(db, EventType.DATABASE_TITLE, oldTitle, newTitle);
			}
		}
	}

	/**
	 * Gets the hostname of the game server.
	 * 
	 * @return the hostname
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the hostname of the game server.
	 * 
	 * @param host the hostname
	 */
	public void setHost(String host) {
		// TODO validate?
		if(host == null) {
			host = "";
		}
		if(!this.host.equals(host)) {
			this.host = host;
			if(db != null) {
				db.fireChanged(this);
			}
		}
	}

	/**
	 * Gets the port number of the game server.
	 * 
	 * @return the port number
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port number of the game server.  Valid ports are 1-65535,
	 * inclusive.  The port will not be changed if the argument is invalid.
	 * 
	 * @param port the port number
	 */
	public void setPort(int port) {
		if(port < 1 || port > 65535) {
			return;
		}
		if(this.port != port) {
			this.port = port;
			if(db != null) {
				db.fireChanged(this);
			}
		}
	}

	/**
	 * Gets the game letter this database is associated with.
	 * 
	 * @return the game letter
	 */
	public char getGameLetter() {
		return gameLetter;
	}

	/**
	 * Sets the game letter this database is associated with.  Valid letters
	 * are A-Z, except Q.  The game letter will not be changed if the argument
	 * is invalid.
	 * 
	 * @param gameLetter the game letter
	 */
	public void setGameLetter(char gameLetter) {
		if(gameLetter < 'A' || gameLetter == 'Q' || gameLetter > 'Z') {
			return;
		}
		if(this.gameLetter != gameLetter) {
			this.gameLetter = gameLetter;
			if(db != null) {
				db.fireChanged(this);
			}
		}
	}

	/**
	 * Gets the login name.
	 * 
	 * @return the login name
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * Sets the login name.  The name will not be changed if the argument is
	 * not a valid name, or if the argument is an empty string and auto-login
	 * is true.
	 * 
	 * @param loginName the login name
	 * @see #isValidName(String)
	 * @see #isAutoLogin()
	 */
	public void setLoginName(String loginName) {
		if(!isValidName(loginName) || ("".equals(loginName) && isAutoLogin())) {
			return;
		}
		if(!this.loginName.equals(loginName)) {
			this.loginName = loginName;
			if(db != null) {
				db.fireChanged(this);
			}
		}
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.  If the argument is not valid, the password will not
	 * be changed.
	 * 
	 * @param password the password
	 * @see #isValidPassword(String)
	 */
	public void setPassword(String password) {
		if(!isValidPassword(password)) {
			return;
		}
		if(!this.password.equals(password)) {
			this.password = password;
			if(db != null) {
				db.fireChanged(this);
			}
		}
	}

	/**
	 * Gets the auto-login setting.  This is really just a suggestion to
	 * scripts.
	 * 
	 * @return the auto-login setting
	 */
	public boolean isAutoLogin() {
		return autoLogin;
	}

	/**
	 * Sets the auto-login setting.  This is really just a suggestion to
	 * scripts.  The auto-login setting will not be changed if the argument is
	 * true and the user name is an empty string.
	 * 
	 * @param autoLogin the auto-login setting
	 * @see #getLoginName()
	 */
	public void setAutoLogin(boolean autoLogin) {
		if(autoLogin && "".equals(getLoginName())) {
			return;
		}
		if(this.autoLogin != autoLogin) {
			this.autoLogin = autoLogin;
			if(db != null) {
				db.fireChanged(this);
			}
		}
	}
}
