package com.chalcodes.weaponm.gui.terminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;

import com.chalcodes.jtx.Display;
import com.chalcodes.jtx.ScrollbackBuffer;
import com.chalcodes.jtx.SoftFont;
import com.chalcodes.jtx.StickyScrollPane;
import com.chalcodes.jtx.VgaSoftFont;
import com.chalcodes.jtx.extensions.SelectionControl;
import com.chalcodes.weaponm.AppSettings;
import com.chalcodes.weaponm.emulation.Emulation;
import com.chalcodes.weaponm.emulation.EmulationParser;
import com.chalcodes.weaponm.emulation.lexer.EmulationLexer;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;
import com.chalcodes.weaponm.event.NetworkStatus;
import com.chalcodes.weaponm.event.WeaponEvent;
import com.chalcodes.weaponm.gui.I18n;

public class Terminal {
//	private static final Logger log = LoggerFactory.getLogger(Terminal.class.getSimpleName());
	private static final int MODE_NORMAL = 0;
	private static final int MODE_BURST = 1;
	private static final int MODE_RECORD = 2;
	
	private final EventSupport eventSupport;
	private final ScrollbackBuffer buffer;
	private final SoftFont font;
	private final EmulationLexer lexer;
	private final EmulationParser parser;
	private final Emulation emulation;
	
	private final Display display;
	private final JPopupMenu popupMenu;
	private final JScrollPane scrollPane;
	private final RecordPanel recordPanel;
	private final BurstPanel burstPanel;
	private final JPanel borderPanel;
	private final DefaultSingleCDockable dockable;
	
	private boolean connected;
	private boolean textOnClipboard;
	private int panelMode;
	
	public Terminal(EventSupport eventSupport) throws IOException, ClassNotFoundException {
		this.eventSupport = eventSupport;
		
		buffer = new ScrollbackBuffer(
				AppSettings.getBufferColumns(),
				AppSettings.getBufferLines());
		font = new VgaSoftFont();
		display = new Display(buffer, font, AppSettings.getBufferColumns(), 0);
		buffer.addBufferObserver(display);
		lexer = new EmulationLexer();
		parser = new EmulationParser(buffer);
		lexer.addEventListener(parser);
		emulation = new Emulation(lexer);
	
		popupMenu = new JPopupMenu();
		scrollPane = new StickyScrollPane(display);
		burstPanel = new BurstPanel(eventSupport, this);
		recordPanel = new RecordPanel(eventSupport);
		
		borderPanel = new JPanel(new BorderLayout());
		borderPanel.add(scrollPane, BorderLayout.CENTER);
		borderPanel.setBorder(null);

		dockable = new DefaultSingleCDockable("TERMINAL", I18n.getString("TITLE_TERMINAL"), borderPanel);
		
		configureTerminal();
		configureModeHotkeys();
		configureDocking();
	}
	
	public DefaultSingleCDockable getDockable() {
		return dockable;
	}
	
	private void configureTerminal() {
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().setBackground(Color.DARK_GRAY);
		scrollPane.setAutoscrolls(true);
		scrollPane.setBorder(null);
		
		// appends game text and status messages to terminal
		PropertyChangeListener listener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if(e instanceof WeaponEvent) {
					WeaponEvent evt = (WeaponEvent) e;
					switch(evt.getType()) {
					case TEXT_RECEIVED:
						String text = (String) evt.getNewValue();
						emulation.write(text);
						break;
					case NETWORK_STATUS:
						NetworkStatus oldStatus = (NetworkStatus) evt.getOldValue();
						NetworkStatus newStatus = (NetworkStatus) evt.getNewValue();
						if(oldStatus == NetworkStatus.CONNECTED && newStatus == NetworkStatus.DISCONNECTED) {
							String msg = I18n.getString("DISCONNECTED");
							emulation.write("\r\n\r\n\033[1;31m<< " + msg + " >>\033[0m\r\n");
						}
						break;					
					}
				}
			}
		};
		eventSupport.addPropertyChangeListener(EventType.TEXT_RECEIVED, listener);
		eventSupport.addPropertyChangeListener(EventType.NETWORK_STATUS, listener);
		
		// sends keystrokes
		scrollPane.getViewport().addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				int mods = e.getModifiers();
				if(mods == 0 || mods == KeyEvent.SHIFT_MASK) {
					char c = e.getKeyChar();
					if(c >= 32 && c <= 125 || c == KeyEvent.VK_TAB || c == KeyEvent.VK_BACK_SPACE) {
						send(c);
						e.consume();
					}
					else if(c == KeyEvent.VK_ENTER) {
						send("\r\n");
						e.consume();
					}
				}
			}
		});
		
		// selection, copy, paste
		SelectionControl selectionControl = new SelectionControl(eventSupport, scrollPane.getViewport());
		
		final AbstractAction copyAction = new CopyAction(selectionControl);
		copyAction.setEnabled(false);
		scrollPane.getViewport().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK), "copy");
		scrollPane.getViewport().getActionMap().put("copy", copyAction);
		
		final AbstractAction pasteAction = new PasteAction(eventSupport);
		pasteAction.setEnabled(false);
		scrollPane.getViewport().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK), "paste");
		scrollPane.getViewport().getActionMap().put("paste", pasteAction);
		
		// enables and disables actions
		PropertyChangeListener propertyListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if(e.getPropertyName().equals(EventType.NETWORK_STATUS.name())) {
					connected = (NetworkStatus) e.getNewValue() == NetworkStatus.CONNECTED;
					pasteAction.setEnabled(connected && textOnClipboard);
					if(!connected) {
						removePanels();
					}
				}
				else { // property must be SelectionControl.PROPERTY_SELECTION_ACTIVE
					copyAction.setEnabled((boolean) e.getNewValue());
				}
			}
		};
		eventSupport.addPropertyChangeListener(EventType.NETWORK_STATUS, propertyListener);
		eventSupport.addPropertyChangeListener(SelectionControl.PROPERTY_SELECTION_ACTIVE, propertyListener);
		
		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		textOnClipboard = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
		clipboard.addFlavorListener(new FlavorListener() {
			@Override
			public void flavorsChanged(FlavorEvent e) {
				textOnClipboard = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
				pasteAction.setEnabled(connected && textOnClipboard);
			}
		});
		
		// popup menu
		popupMenu.add(copyAction);
		popupMenu.add(pasteAction);
		
		// trigger for popup menu
		MouseListener popupListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					showPopupMenu(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					showPopupMenu(e);
				}
			}
			
			private void showPopupMenu(MouseEvent e) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		};
		scrollPane.getViewport().addMouseListener(popupListener);
		Component[] children = scrollPane.getViewport().getComponents();
		for(Component child : children) {
			child.addMouseListener(popupListener);
		}
	}
	
	private void configureModeHotkeys() {
		// burst
		final AbstractAction burstAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(connected && panelMode == MODE_NORMAL) {
					borderPanel.add(burstPanel.getComponent(), BorderLayout.SOUTH);
					borderPanel.validate();
					burstPanel.focusInputField();
					panelMode = MODE_BURST;
				}				
			}
		};
		borderPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		.put(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK), "burst");
		borderPanel.getActionMap().put("burst", burstAction);
		
		// record
		final AbstractAction recordAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if(connected && panelMode == MODE_NORMAL) {
					recordPanel.reset();
					recordPanel.setRecording(true);
					borderPanel.add(recordPanel.getComponent(), BorderLayout.SOUTH);
					borderPanel.validate();
					panelMode = MODE_RECORD;
				}
				else if(panelMode == MODE_RECORD) {
					recordPanel.setRecording(false);
					borderPanel.remove(recordPanel.getComponent());		
					borderPanel.validate();

					String recorded = recordPanel.getText();
					if(recorded.length() > 0) {
						recorded = recorded.replaceAll("\r\n", "^M");
						burstPanel.setBurst(recorded);					
					}
					panelMode = MODE_NORMAL;
				}
			}
		};
		borderPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK), "record");
		borderPanel.getActionMap().put("record", recordAction);
		
		// cancel
		final AbstractAction cancelAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				removePanels();
			}
		};
		borderPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
		borderPanel.getActionMap().put("cancel", cancelAction);

	}
	
	void removePanels() {
		if(panelMode == MODE_BURST) {
			borderPanel.remove(burstPanel.getComponent());		
			borderPanel.validate();
			scrollPane.getViewport().requestFocusInWindow();
			panelMode = MODE_NORMAL;
		}
		else if(panelMode == MODE_RECORD) {
			recordPanel.setRecording(false);
			borderPanel.remove(recordPanel.getComponent());		
			borderPanel.validate();
			panelMode = MODE_NORMAL;
		}
	}
	
	private void configureDocking() {
		dockable.setFocusComponent(scrollPane.getViewport());
		dockable.setDefaultLocation(ExtendedMode.MINIMIZED, CLocation.base().minimalSouth());
		dockable.setCloseable(true);
		ImageIcon icon = new ImageIcon(getClass().getResource("/com/chalcodes/weaponm/icons/terminal16.png"));
		dockable.setTitleIcon(icon);
	}
	
	private void send(String string) {
		WeaponEvent event = new WeaponEvent(EventType.TEXT_TYPED, null, string);
		eventSupport.firePropertyChange(event);
	}
	
	private void send(char c) {
		send(new String(new char[] { c }));
	}

}
