package com.chalcodes.weaponm.gui;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import com.chalcodes.weaponm.database.LoginOptions;
import com.chalcodes.weaponm.event.EventSupport;
import com.chalcodes.weaponm.event.EventType;

/**
 * This was mocked up in NetBeans and then completed by hand.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
class LoginOptionsPanel extends javax.swing.JPanel {
	private static final long serialVersionUID = 1L;
	private static final Character[] letters = new Character[25];
	private final LoginOptions options;
	
	static {
		int i = 0;
		for (char c = 'A'; c <= 'Z'; ++c) {
			if (c != 'Q') {
				letters[i] = c;
				++i;
			}
		}
	}
	
    private final JLabel titleLabel;
    private final JLabel hostLabel;
    private final JLabel portLabel;
    private final JLabel letterLabel;
    private final JLabel nameLabel;
    private final JLabel passwordLabel;
    private final JLabel autoLoginLabel;
    private final JPanel gamePanel;
    private final JPanel playerPanel;
    final JTextField titleField;
    final JTextField hostField;
    final JTextField portField;
    final JComboBox<Character> letterComboBox;
    final JTextField nameField;
    final JTextField passwordField;
    final JCheckBox autoLoginCheckBox;
    final EventSupport eventSupport;
	
    public LoginOptionsPanel(LoginOptions options, EventSupport eventSupport) {
    	this.options = options;
        gamePanel = new JPanel();
        titleLabel = new JLabel();
        hostLabel = new JLabel();
        portLabel = new JLabel();
        letterLabel = new JLabel();
        titleField = new JTextField();
        titleField.addFocusListener(SelectOnFocus.getInstance());
        hostField = new JTextField();
        hostField.addFocusListener(SelectOnFocus.getInstance());
        portField = new JTextField();
        portField.addFocusListener(SelectOnFocus.getInstance());
		letterComboBox = new JComboBox<Character>(letters);
        playerPanel = new JPanel();
        nameLabel = new JLabel();
        passwordLabel = new JLabel();
        autoLoginLabel = new JLabel();
        nameField = new JTextField();
        nameField.addFocusListener(SelectOnFocus.getInstance());
        passwordField = new JTextField();
        passwordField.addFocusListener(SelectOnFocus.getInstance());
        autoLoginCheckBox = new JCheckBox();
        this.eventSupport = eventSupport;
        
        initComponents();
        
        resetFields();
    }
    
    private void resetFields() {
    	titleField.setText(options.getTitle());
    	hostField.setText(options.getHost());
    	portField.setText(Integer.toString(options.getPort()));
    	letterComboBox.setSelectedItem(options.getGameLetter());
    	nameField.setText(options.getUserName());
    	passwordField.setText(options.getPassword());
    	autoLoginCheckBox.setSelected(options.isAutoLogin());
    }
    
    /**
     * If a field contains an invalid value, this method will focus it and
     * throw an <tt>IllegalStateException</tt> with a descriptive message.
     * 
     * @throws IllegalStateException if something is bogus
     */
    void validateFields() throws IllegalStateException {
    	// TODO i18n exception messages
    	
		// host
		String host = hostField.getText();
		if ("".equals(host)) {
			hostField.requestFocusInWindow();
			throw new IllegalStateException("Host required.");
		}
		try {
			InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			hostField.requestFocusInWindow();
			throw new IllegalStateException("Unknown host: " + host);
		}
		
		// port
		int port;
		try {
			port = Integer.parseInt(portField.getText());
			if (port < 1 || port > 65535) {
				portField.requestFocusInWindow();
				throw new IllegalStateException("Invalid port: " + port);
			}
		} catch (NumberFormatException e) {
			portField.requestFocusInWindow();
			throw new IllegalStateException("Invalid port: "
					+ portField.getText());
		}

		// game
		char letter = (Character) letterComboBox.getSelectedItem();
		if (letter < 'A' || letter > 'Z' || letter == 'Q') {
			letterComboBox.requestFocusInWindow();
			throw new IllegalStateException("Invalid game letter: " + letter);
		}

		// name
		String name = nameField.getText();
		if (!LoginOptions.isValidName(name)) {
			nameField.requestFocusInWindow();
			throw new IllegalStateException("Invalid name: " + name);
		}

		// password
		String password = passwordField.getText();
		if (!LoginOptions.isValidPassword(password)) {
			passwordField.requestFocusInWindow();
			throw new IllegalStateException("Invalid password: " + password);
		}

		// auto-login
		boolean autoLogin = autoLoginCheckBox.isSelected();
		if (autoLogin && "".equals(name)) {
			nameField.requestFocusInWindow();
			throw new IllegalStateException("Name required for auto-login.");
		}
    }
    
    void saveFields() {
    	if(eventSupport != null) {
    		eventSupport.firePropertyChange(EventType.DATABASE_TITLE, null, titleField.getText());
    	}
    	options.setTitle(titleField.getText());
    	options.setHost(hostField.getText());
    	options.setPort(Integer.parseInt(portField.getText()));
    	options.setGameLetter((char) letterComboBox.getSelectedItem());
    	options.setUserName(nameField.getText());
    	options.setPassword(passwordField.getText());
    	options.setAutoLogin(autoLoginCheckBox.isSelected());
    }

    private void initComponents() {
        gamePanel.setBorder(BorderFactory.createTitledBorder(Strings.getString("LABEL_GAME")));
        titleLabel.setText(Strings.getString("LABEL_TITLE"));
        hostLabel.setText(Strings.getString("LABEL_HOST"));
        portLabel.setText(Strings.getString("LABEL_PORT"));
        letterLabel.setText(Strings.getString("LABEL_GAME_LETTER"));

        GroupLayout jPanel1Layout = new GroupLayout(gamePanel);
        gamePanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(letterLabel)
                    .addComponent(portLabel)
                    .addComponent(hostLabel)
                    .addComponent(titleLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(titleField)
                    .addComponent(hostField)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(letterComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(portField, GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 115, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLabel)
                    .addComponent(titleField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(hostLabel)
                    .addComponent(hostField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(portLabel)
                    .addComponent(portField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(letterLabel)
                    .addComponent(letterComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        playerPanel.setBorder(BorderFactory.createTitledBorder(Strings.getString("LABEL_PLAYER")));
        nameLabel.setText(Strings.getString("LABEL_USER_NAME"));
        passwordLabel.setText(Strings.getString("LABEL_PASSWORD"));
        autoLoginLabel.setText(Strings.getString("LABEL_AUTOLOGIN"));

        GroupLayout jPanel2Layout = new GroupLayout(playerPanel);
        playerPanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(passwordLabel)
                    .addComponent(nameLabel)
                    .addComponent(autoLoginLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(nameField)
                    .addComponent(passwordField)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(autoLoginCheckBox)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(autoLoginLabel)
                    .addComponent(autoLoginCheckBox))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(gamePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(playerPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(gamePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }  
}

