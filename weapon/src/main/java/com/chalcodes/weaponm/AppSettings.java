package com.chalcodes.weaponm;

import java.util.UUID;
import java.util.prefs.Preferences;

/**
 * Global application settings.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class AppSettings {
	// TODO validation all around
	
	private static final String PREFS_NODE = "com/chalcodes/weaponm";
	
	private static final String INSTALL_UUID = "InstallUUID";
	
	private static final String LOOK_AND_FEEL = "LookAndFeel";
	private static final String DEFAULT_LOOK_AND_FEEL = "Nimbus";
	
	private static final String BUFFER_LINES = "BufferLines";
	private static final int DEFAULT_BUFFER_LINES = 10000;
	
	private static final String BUFFER_COLUMNS = "BufferColumns";
	private static final int DEFAULT_BUFFER_COLUMNS = 80;
	
	private static final String SCRIPT_CLASSPATH = "ScriptClasspath";
	private static final String DEFAULT_SCRIPT_CLASSPATH = "./scripts";
	
	private static final String AUTO_LOAD_SCRIPTS = "AutoLoadScripts";
	private static final String DEFAULT_AUTO_LOAD_SCRIPTS = "";
	
	private static final String GIANT_FONT = "UseGiantFont";
	private static final boolean DEFAULT_GIANT_FONT = false;
	
	//private static final String LOG_FILE = "LogFile";
	//private static final String DEFAULT_LOG_FILE = "WeaponM.log";
	
	private static final Preferences prefs;
	
	private AppSettings() { }
	
	static {
		prefs = Preferences.userRoot().node(PREFS_NODE);
		
		// this ensures that the preferences node is created
		setLookAndFeel(getLookAndFeel());
		setBufferLines(getBufferLines());
		setBufferColumns(getBufferColumns());
		setScriptClasspath(getScriptClasspath());
		setAutoLoadScripts(getAutoLoadScripts());
		setUseGiantFont(isUseGiantFont());
		//setLogFile(getLogFile());
		
		if(getInstallUuid().equals("")) {
			prefs.put(INSTALL_UUID, UUID.randomUUID().toString());
		}
	}
	
	public static String getInstallUuid() {
		return prefs.get(INSTALL_UUID, "");
	}
	
	public static String getLookAndFeel() {
		return prefs.get(LOOK_AND_FEEL, DEFAULT_LOOK_AND_FEEL);
	}
	
	public static void setLookAndFeel(String name) {
		prefs.put(LOOK_AND_FEEL, name);
	}
	
	public static int getBufferLines() {
		return prefs.getInt(BUFFER_LINES, DEFAULT_BUFFER_LINES);
	}
	
	public static void setBufferLines(int lines) {
		prefs.putInt(BUFFER_LINES, lines);
	}
	
	public static int getBufferColumns() {
		return prefs.getInt(BUFFER_COLUMNS, DEFAULT_BUFFER_COLUMNS);
	}
	
	public static void setBufferColumns(int columns) {
		prefs.putInt(BUFFER_COLUMNS, columns);
	}
	
	public static String getScriptClasspath() {
		return prefs.get(SCRIPT_CLASSPATH, DEFAULT_SCRIPT_CLASSPATH);
	}
	
	public static void setScriptClasspath(String classpath) {
		prefs.put(SCRIPT_CLASSPATH, classpath);
	}
	
	public static String getAutoLoadScripts() {
		return prefs.get(AUTO_LOAD_SCRIPTS, DEFAULT_AUTO_LOAD_SCRIPTS);
	}
	
	/**
	 * 
	 * @param autoLoadScripts a comma-separated list of fully qualified class names
	 */
	public static void setAutoLoadScripts(String autoLoadScripts) {
		prefs.put(AUTO_LOAD_SCRIPTS, autoLoadScripts);
	}
	
	public static boolean isUseGiantFont() {
		return prefs.getBoolean(GIANT_FONT, DEFAULT_GIANT_FONT);
	}
	
	public static void setUseGiantFont(Boolean giantFont) {
		prefs.putBoolean(GIANT_FONT, giantFont);
	}
	
	/*
	public static String getLogFile() {
		return prefs.get(LOG_FILE, DEFAULT_LOG_FILE);
	}
	
	public static void setLogFile(String logFile) {
		prefs.put(LOG_FILE, logFile);
	}
	*/
}
