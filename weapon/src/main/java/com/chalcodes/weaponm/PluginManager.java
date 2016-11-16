package com.chalcodes.weaponm;

import com.chalcodes.event.ClassBusFactory;
import com.chalcodes.weaponm.plugin.Terminal;

import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;

/**
 * Loads plugins.
 *
 * @author Kevin Krumwiede
 */
class PluginManager {
	private final File mConfigFile;
	private final URL mClasspath;
	private final ClassBusFactory mEventBuses;

	PluginManager(@Nonnull final ClassBusFactory eventBuses,
	              @Nonnull final File dataDir) {
		mConfigFile = new File(dataDir, "plugins.cfg");
		try {
			mClasspath = new File(dataDir, "plugins").toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		mEventBuses = eventBuses;
	}

	private final List<Plugin> mPlugins = new LinkedList<>();

	void loadPlugins() throws IOException {
		unloadPlugins();
		if(!mConfigFile.exists()) {
			writeDefaultConfig();
		}
		final List<String> classNames = readConfigFile();
		if(classNames.isEmpty()) {
			Log.i("no plugins configured");
		}
		else {
			final ClassLoader loader = new URLClassLoader(new URL[]{mClasspath});
			for(final String className : classNames) {
				try {
					final Class<?> klass = loader.loadClass(className);
					if(Plugin.class.isAssignableFrom(klass)) {
						final Plugin plugin = (Plugin) klass.newInstance();
						mPlugins.add(plugin);
					}
					else {
						Log.w("not a plugin: " + className);
					}
				} catch (ClassNotFoundException e) {
					Log.w("class not found: " + className);
				} catch (InstantiationException | IllegalAccessException e) {
					Log.w("could not instantiate: " + className);
				}
			}
		}
		for(final Plugin plugin : mPlugins) {
			plugin.setPrivateFields(mEventBuses);
			// TODO idea: don't call init until game is set
			// need to think about scopes of game and plugins
			plugin.init();
		}
	}

	void unloadPlugins() {
		for(final Plugin plugin : mPlugins) {
			plugin.disable();
		}
		mPlugins.clear();
	}

	JMenu getMenu() {
		return new JMenu(); // TODO
	}

	private final Action mReloadAction = new AbstractAction() {
		{
			putValue(Action.NAME, "Reload plugins");
			putValue(Action.MNEMONIC_KEY, 'R');
		}
		@Override
		public void actionPerformed(final ActionEvent event) {
			try {
				loadPlugins();
			} catch (IOException e) {
				Log.e("error loading plugins", e);
			}
		}
	};

	void populateMenu(@Nonnull final JMenu menu) {
		menu.removeAll();
		for(final Plugin plugin : mPlugins) {
			final List<Action> actions = plugin.getActions();
			// TODO activation action
			// if plugin provides actions, put activation action in submenu and display "Enable" or "Disable"
			// else put it in top level and display plugin name
			if(actions != null && !actions.isEmpty()) {
				final JMenu submenu = new JMenu(plugin.getName());
				for(final Action action : actions) {
					submenu.add(action);
				}
				menu.add(submenu);
			}
		}
		if(menu.getItemCount() > 0) {
			menu.addSeparator();
		}
		menu.add(mReloadAction);
	}

	private void writeDefaultConfig() throws IOException {
		try(final PrintWriter file = new PrintWriter(new FileWriter(mConfigFile))) {
			file.println("# fully qualified class names, one per line");
			file.println(Terminal.class.getName());
			// TODO write names of all standard plugins
		}
	}

	private List<String> readConfigFile() throws IOException {
		try(final BufferedReader file = new BufferedReader(new FileReader(mConfigFile))) {
			final List<String> classNames = new LinkedList<>();
			String line;
			while((line = file.readLine()) != null) {
				final int comment = line.indexOf('#');
				if(comment != -1) {
					line = line.substring(0, comment);
				}
				line = line.trim();
				if(!line.isEmpty()) {
					classNames.add(line);
				}
			}
			return classNames;
		}
	}
}
