package com.chalcodes.weaponm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * Utility methods related to the runtime environment.
 *
 * @author Kevin Krumwiede
 */
public class Environment {
	private Environment() {
	}

	/**
	 * Locates the Weapon M data directory. Verifies that it is a readable and
	 * writable directory, and creates it if it does not exist.
	 *
	 * @return the data directory
	 * @throws IOException if something goes wrong
	 */
	@Nonnull
	public static File getDataDir() throws IOException {
		String name = getEnvDir("WEAPON_DATA");

		if (name == null) {
			final String os = System.getProperty("os.name");
			if (os.contains("Windows")) {
				name = getEnvDir("LOCALAPPDATA");
				if (name == null) {
					name = getEnvDir("APPDATA");
				}
			}
			else if (os.contains("Linux")) {
				name = getEnvDir("XDG_DATA_HOME");
				if (name == null) {
					name = getEnvDir("HOME");
					if (name != null) {
						name += ".local/share/";
					}
				}
			}
			else if (os.contains("OS X")) {
				name = getEnvDir("HOME");
				if (name != null) {
					name += "Library/Application Support/";
				}
			}
			else {
				// assume generic unix
				name = getEnvDir("HOME");
				if (name != null) {
					name += '.';
				}
			}

			if (name != null) {
				name += "WeaponM";
			}
		}

		if (name == null) {
			throw new IOException("unable to locate data directory");
		}
		final File file = new File(name).getAbsoluteFile();
		if (file.exists()) {
			if (file.isDirectory() && file.canRead() && file.canWrite()) {
				return file;
			}
			else {
				throw new IOException(file.getPath() + " is not a readable and writable directory");
			}
		}
		else if (file.mkdirs()) {
			return file;
		}
		else {
			throw new IOException("could not create " + file.getPath());
		}
	}

	/**
	 * Gets an environment variable that refers to a directory. Appends the
	 * system file separator if not already present.
	 *
	 * @param name the variable name
	 * @return the variable value, or null
	 */
	@Nullable
	private static String getEnvDir(@Nonnull final String name) {
		final String value = System.getenv(name);
		if (value == null) {
			return null;
		}
		return value.endsWith(File.separator) ? value : value + File.separator;
	}
}
