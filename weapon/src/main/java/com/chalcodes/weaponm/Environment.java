package com.chalcodes.weaponm;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * Utility methods related to the runtime environment.
 *
 * @author Kevin Krumwiede
 */
class Environment {
	private Environment() {}

	/**
	 * Locates the OS-specific application data directory.
	 *
	 * @return the data directory
	 * @throws IOException if the data directory cannot be found
	 */
	@Nonnull
	static File getDataDir() throws IOException {
		String name = System.getenv("WEAPON_DATA");
		if(name != null) {
			final File file = new File(name).getAbsoluteFile();
			if(file.exists() && file.isDirectory() && file.canRead() && file.canWrite()) {
				return file;
			}
			else {
				throw new IOException('\'' + name + "' is not a readable and writable directory");
			}
		}
		final String os = System.getProperty("os.name");
		if(os.contains("Windows")) {
			//noinspection SpellCheckingInspection
			name = System.getenv("LOCALAPPDATA");
			if(name == null) {
				//noinspection SpellCheckingInspection
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
		final File file = new File(name).getAbsoluteFile();
		if(file.exists()) {
			if(file.isDirectory() && file.canRead() && file.canWrite()) {
				return file;
			}
			else {
				throw new IOException('\'' + name + "' is not a readable and writable directory");
			}
		}
		else if(file.mkdirs()) {
			return file;
		}
		else {
			throw new IOException("unable to locate data directory");
		}
	}
}
