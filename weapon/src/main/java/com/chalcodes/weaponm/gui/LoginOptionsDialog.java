package com.chalcodes.weaponm.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import com.chalcodes.weaponm.database.LoginOptions;

// TODO: i18n

public class LoginOptionsDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public static final int CANCEL_ACTION = 0;
	public static final int OK_ACTION = 1;

	// acceptable names are 1-41 chars, but supplying it is optional
	private static final Pattern namePattern = Pattern
			.compile("[ -\\}]{0,41}");
	// password can actually be blank
	private static final Pattern passwordPattern = Pattern
			.compile("[ -\\}]{0,8}");

	private LoginOptions options;
	private int action = CANCEL_ACTION;

	private JTextField titleField;

	private JTextField hostField;
	private JTextField portField; // TODO: make formatted text field?
	private JComboBox<Character> gameComboBox;

	private JTextField nameField;
	private JPasswordField passwordField;
	private JCheckBox autoLoginCheckBox;

	private JButton okButton;
	private JButton cancelButton;

	public static int showDialog(Frame parent, LoginOptions options) {
		LoginOptionsDialog dialog = new LoginOptionsDialog(parent, options);
		dialog.setVisible(true);
		// thread resumes when dialog is hidden
		return dialog.action;
	}

	LoginOptionsDialog(Frame parent, LoginOptions options) {
		super(parent, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.options = options;
		initComponents();
		action = CANCEL_ACTION;
		titleField.setText(options.getTitle());
		hostField.setText(options.getHost());
		portField.setText(Integer.toString(options.getPort()));
		gameComboBox.setSelectedItem(new Character(options.getGame()));
		nameField.setText(options.getName());
		passwordField.setText(options.getPassword());
		autoLoginCheckBox.setSelected(options.isAutoLogin());
		setLocationRelativeTo(parent);
	}

	private void initComponents() {
		titleField = new JTextField();
		titleField.addFocusListener(SelectOnFocus.getInstance());
		hostField = new JTextField();
		hostField.addFocusListener(SelectOnFocus.getInstance());
		portField = new JTextField();
		portField.addFocusListener(SelectOnFocus.getInstance());

		Character[] letters = new Character[25];
		int i = 0;
		for (char c = 'A'; c <= 'Z'; ++c) {
			if (c != 'Q') {
				letters[i] = c;
				++i;
			}
		}
		gameComboBox = new JComboBox<Character>(letters);

		nameField = new JTextField();
		nameField.addFocusListener(SelectOnFocus.getInstance());
		passwordField = new JPasswordField();
		passwordField.addFocusListener(SelectOnFocus.getInstance());
		autoLoginCheckBox = new JCheckBox();

		okButton = new JButton("OK");
		okButton.setMnemonic('O');
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveFields();
					action = OK_ACTION;
					dispose();
				} catch (IllegalStateException ex) {
					JOptionPane.showMessageDialog(LoginOptionsDialog.this,
							ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic('C');
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action = CANCEL_ACTION;
				dispose();
			}
		});

		setTitle("Login Options");
		JLabel titleLabel = new JLabel("Title");
		JLabel hostLabel = new JLabel("Host");
		JLabel portLabel = new JLabel("Port");
		JLabel gameLabel = new JLabel("Game");
		JLabel nameLabel = new JLabel("Name");
		JLabel passwordLabel = new JLabel("Password");
		JLabel autoLoginLabel = new JLabel("Auto-login");
		JPanel gamePanel = new JPanel();
		gamePanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Game"));
		JPanel playerPanel = new JPanel();
		playerPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Player"));

		// layout shizzle
		GroupLayout gamePanelLayout = new GroupLayout(gamePanel);
		gamePanel.setLayout(gamePanelLayout);
		gamePanelLayout
				.setHorizontalGroup(gamePanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								gamePanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gamePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																titleLabel,
																GroupLayout.Alignment.TRAILING)
														.addComponent(
																hostLabel,
																GroupLayout.Alignment.TRAILING)
														.addComponent(
																portLabel,
																GroupLayout.Alignment.TRAILING)
														.addComponent(
																gameLabel,
																GroupLayout.Alignment.TRAILING))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												gamePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																titleField,
																GroupLayout.DEFAULT_SIZE,
																199,
																Short.MAX_VALUE)
														.addComponent(
																hostField,
																GroupLayout.DEFAULT_SIZE,
																199,
																Short.MAX_VALUE)
														.addComponent(
																gameComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																portField,
																GroupLayout.PREFERRED_SIZE,
																51,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap()));
		gamePanelLayout
				.setVerticalGroup(gamePanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								gamePanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gamePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(titleLabel)
														.addComponent(
																titleField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												gamePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(hostLabel)
														.addComponent(
																hostField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												gamePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(portLabel)
														.addComponent(
																portField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												gamePanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(gameLabel)
														.addComponent(
																gameComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		GroupLayout playerPanelLayout = new GroupLayout(playerPanel);
		playerPanel.setLayout(playerPanelLayout);
		playerPanelLayout
				.setHorizontalGroup(playerPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								playerPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												playerPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																nameLabel,
																GroupLayout.Alignment.TRAILING)
														.addComponent(
																passwordLabel,
																GroupLayout.Alignment.TRAILING)
														.addComponent(
																autoLoginLabel,
																GroupLayout.Alignment.TRAILING))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												playerPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addComponent(
																nameField,
																GroupLayout.DEFAULT_SIZE,
																166,
																Short.MAX_VALUE)
														.addComponent(
																passwordField,
																GroupLayout.DEFAULT_SIZE,
																166,
																Short.MAX_VALUE)
														.addComponent(
																autoLoginCheckBox))
										.addContainerGap()));
		playerPanelLayout
				.setVerticalGroup(playerPanelLayout
						.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								playerPanelLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												playerPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(nameLabel)
														.addComponent(
																nameField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												playerPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(passwordLabel)
														.addComponent(
																passwordField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												playerPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(autoLoginLabel)
														.addComponent(
																autoLoginCheckBox))
										.addContainerGap(
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.LEADING)
												.addComponent(
														playerPanel,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addComponent(
														gamePanel,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.addGroup(
														GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		okButton)
																.addPreferredGap(
																		LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		cancelButton)))
								.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(gamePanel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(playerPanel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.BASELINE)
												.addComponent(cancelButton)
												.addComponent(okButton))
								.addContainerGap(GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		getRootPane().setDefaultButton(okButton);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setResizable(false);
	}

	private void saveFields() throws IllegalStateException {
		// title
		String title = titleField.getText();
		
		// host
		String host = hostField.getText();
		if ("".equals(host)) {
			hostField.requestFocusInWindow();
			throw new IllegalStateException("host required");
		}
		try {
			InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			hostField.requestFocusInWindow();
			throw new IllegalStateException("unknown host: " + host);
		}

		// port
		int port;
		try {
			port = Integer.parseInt(portField.getText());
			if (port < 1 || port > 65535) {
				portField.requestFocusInWindow();
				throw new IllegalStateException("invalid port: " + port);
			}
		} catch (NumberFormatException e) {
			portField.requestFocusInWindow();
			throw new IllegalStateException("invalid port: "
					+ portField.getText());
		}

		// game
		char game = (Character) gameComboBox.getSelectedItem();
		if (game < 'A' || game > 'Z' || game == 'Q') {
			gameComboBox.requestFocusInWindow();
			throw new IllegalStateException("invalid game letter: " + game);
		}

		// name
		String name = nameField.getText();
		if (!namePattern.matcher(name).matches()) {
			nameField.requestFocusInWindow();
			throw new IllegalStateException("invalid name: " + name);
		}

		// password
		@SuppressWarnings("deprecation")
		String password = passwordField.getText();
		if (!passwordPattern.matcher(password).matches()) {
			passwordField.requestFocusInWindow();
			throw new IllegalStateException("invalid password: " + password);
		}

		// auto-login
		boolean autoLogin = autoLoginCheckBox.isSelected();
		if (autoLogin && "".equals(name)) {
			nameField.requestFocusInWindow();
			throw new IllegalStateException("name required for auto-login");
		}

		// golden!
		options.setTitle(title);
		options.setHost(host);
		options.setPort(port);
		options.setGame(game);
		options.setName(name);
		options.setPassword(password);
		options.setAutoLogin(autoLogin);
		
		// this is a bit of a hack
		//((MainWindow) getParent()).gui.updateTitles(title);
		// TODO fire title event
	}
}
