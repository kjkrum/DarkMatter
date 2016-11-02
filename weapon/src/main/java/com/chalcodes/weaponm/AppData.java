package com.chalcodes.weaponm;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
public class AppData {
	private AppData() {}

	@Nonnull
	public static File findDataDir() {
		final String os = System.getProperty("os.name");
		String name;
		if(os.contains("Windows")) {
			name = System.getenv("LOCALAPPDATA");
			if(name == null) {
				name = System.getenv("APPDATA");
			}
			if(!name.endsWith(File.separator)) {
				name += File.separator;
			}
			name += "WeaponM";
		}
		else if(os.contains("Linux")) {
			name = System.getenv("XDG_DATA_HOME");
			if(name == null) {
				name = System.getenv("HOME");
				if(!name.endsWith(File.separator)) {
					name += File.separator;
				}
				name += ".local/share/";
			}
			if(!name.endsWith(File.separator)) {
				name += File.separator;
			}
			name += "WeaponM";
		}
		else if(os.contains("OS X")) {
			name = System.getenv("HOME");
			if(!name.endsWith(File.separator)) {
				name += File.separator;
			}
			name += "Library/Application Support/WeaponM";
		}
		else {
			// assume generic unix
			name = System.getenv("HOME");
			if(!name.endsWith(File.separator)) {
				name += File.separator;
			}
			name += ".WeaponM";
		}
		final File dir = new File(name);
		if(dir.exists() || dir.mkdirs()) {
			return dir.getAbsoluteFile();
		}
		throw new RuntimeException("unable to locate data dir");
	}
}
