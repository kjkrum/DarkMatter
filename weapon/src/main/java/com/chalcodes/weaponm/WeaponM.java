package com.chalcodes.weaponm;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.station.CScreenDockStationWindowClosingStrategy;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.gui.dock.station.screen.window.DefaultScreenDockWindowFactory;
import com.chalcodes.event.*;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * Main class.
 *
 * @author Kevin Krumwiede
 */
public class WeaponM {

	static {
		SwingUtil.setLookAndFeel();
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					final File dataDir = Environment.getDataDir();
					new WeaponM(dataDir).start();
				} catch (IOException e) {
					Log.e(e.getMessage());
					System.exit(1);
				}
			}
		});
	}

	private final JFrame mMainWindow;
	private final CControl mDockControl;
	private final File mLayoutFile;
	private final ClassBusFactory mEventBuses;
	private final GameManager mGameManager;
	private final NetworkManager mNetworkManager;
	private final PluginManager mPluginManager;

	private WeaponM(@Nonnull final File dataDir) throws IOException {
		mMainWindow = createMainWindow();
		mDockControl = createDockControl(mMainWindow);
		mMainWindow.add(mDockControl.getContentArea());
		mLayoutFile = new File(dataDir, "layout.bin");
		mEventBuses = createEventBuses();
		mGameManager = new GameManager(mEventBuses, dataDir, mMainWindow);
		mNetworkManager = new NetworkManager(mEventBuses);
		mPluginManager = new PluginManager(mEventBuses, dataDir);
		// TODO set up menu bar
		final JMenuBar menuBar = new JMenuBar();
		menuBar.add(mGameManager.getMenu());
		menuBar.add(mNetworkManager.getMenu());
		menuBar.add(mPluginManager.getMenu());
		mMainWindow.setJMenuBar(menuBar);
	}

	private JFrame createMainWindow() {
		final JFrame frame = new JFrame("Weapon M");
		final URL url = getClass().getResource("/icon.png");
		final ImageIcon icon = new ImageIcon(url);
		frame.setIconImage(icon.getImage());
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				confirmExit();
			}
		});
		frame.setSize(800, 600);
		return frame;
	}

	private static CControl createDockControl(@Nonnull final JFrame mainWindow) {
		final CControl control = new CControl(mainWindow);
		control.setTheme(ThemeMap.KEY_ECLIPSE_THEME);
		/* Set a window factory that puts externalized dockables in floating JFrames. */
		final DefaultScreenDockWindowFactory windowFactory = new DefaultScreenDockWindowFactory();
		windowFactory.setKind(DefaultScreenDockWindowFactory.Kind.FRAME);
		windowFactory.setUndecorated(false);
		control.putProperty(ScreenDockStation.WINDOW_FACTORY, windowFactory);
		/* Set a window closing strategy that allows floating JFrames to be closed. */
		control.putProperty(ScreenDockStation.WINDOW_CLOSING_STRATEGY, new CScreenDockStationWindowClosingStrategy());
		return control;
	}

	private static ClassBusFactory createEventBuses() {
		final Executor swingExecutor = new Executor() {
			@Override
			public void execute(@Nonnull final Runnable command) {
				SwingUtilities.invokeLater(command);
			}
		};
		final EventBus<Exception> exceptionBus = new SimpleEventBus<>(swingExecutor, null);
		final ReceiverSetFactory<Object> receiverSetFactory = new ReceiverSetFactory<Object>() {
			@Nonnull
			@Override
			public Set<EventReceiver<Object>> newSet(@Nonnull final Set<EventReceiver<Object>> current) {
				return new RotatingIteratorSet<>(current);
			}
		};
		final UncaughtExceptionHandler<Object> uncaughtExceptionHandler = UncaughtExceptionHandlers.report();
		final BusFactory<Object> busFactory =
				new SimpleBusFactory<>(swingExecutor, exceptionBus, receiverSetFactory, uncaughtExceptionHandler);
		return new ClassBusFactory(busFactory);
	}

	private void start() {
		mMainWindow.setVisible(true);
		try {
			mGameManager.connect();
		} catch (IOException e) {
			// TODO display error dialog
		}
	}

	private static final String LAYOUT_NAME = "default";

	// TODO call this when plugins are loaded
	private void loadLayout() {
		if(mLayoutFile.exists() && mLayoutFile.canRead()) {
			try {
				mDockControl.getResources().readFile(mLayoutFile);
				mDockControl.load(LAYOUT_NAME);
				/* If a dockable isn't in the layout file, it's made invisible. */
				final int count = mDockControl.getCDockableCount();
				for(int i = 0; i < count; ++i) {
					mDockControl.getCDockable(i).setVisible(true);
				}
			} catch (IOException | IllegalArgumentException e) {
				Log.w("error loading layout", e);
			}
		}
	}

	private void saveLayout() {
		try {
			mDockControl.save(LAYOUT_NAME);
			mDockControl.getResources().writeFile(mLayoutFile);
		} catch (IllegalArgumentException | IOException e) {
			Log.w("error saving layout", e);
		}
	}

	private void confirmExit() {
		if (JOptionPane.showOptionDialog(mMainWindow, "Exit Weapon M?", "Confirm exit",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {
			exit();
		}
	}

	private void exit() {
		saveLayout();
		// TODO shut down all the things
		System.exit(0);
	}
}
