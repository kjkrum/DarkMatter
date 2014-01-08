package com.chalcodes.weaponm.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;

import com.chalcodes.weaponm.Build;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private Frame parent;
	private JButton closeButton;
	private JButton copyButton;
	private JLabel imageLabel;
	private JPanel copyrightPanel;
	private JScrollPane scrollPane;
	private JTextArea infoTextArea;

	public static void showDialog(Frame parent) {
		new AboutDialog(parent).setVisible(true);
		// thread resumes when dialog is hidden
	}
	
	private AboutDialog(Frame parent) {
		super(parent, true);
		this.parent = parent;
		initComponents();
		setLocationRelativeTo(parent);
	}

	private void initComponents() {
		imageLabel = new JLabel();
		copyrightPanel = new JPanel();
		infoTextArea = new JTextArea();
		scrollPane = new JScrollPane(infoTextArea);
		closeButton = new JButton();
		copyButton = new JButton();

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("About Weapon M");
		setResizable(false);

		imageLabel.setIcon(new ImageIcon(parent.getIconImage()));
		imageLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		imageLabel.setToolTipText("Forged in the fires of Betelgeuse!");

		copyrightPanel.setLayout(new BoxLayout(copyrightPanel, BoxLayout.Y_AXIS));
		JLabel copyrightLabel1 = new JLabel("Copyright 2012, 2013, Kevin Krumwiede");
		copyrightLabel1.setAlignmentX(CENTER_ALIGNMENT);
		copyrightPanel.add(copyrightLabel1);
		JLabel copyrightLabel2 = new JLabel("Published under the BSD License");
		copyrightLabel2.setAlignmentX(CENTER_ALIGNMENT);
		copyrightPanel.add(copyrightLabel2);
		//JLabel copyrightLabel3 = new JLabel("https://sourceforge.net/projects/weapon-m/");
		//copyrightLabel3.setAlignmentX(CENTER_ALIGNMENT);
		//copyrightPanel.add(copyrightLabel3);
		
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// this is kind of hackish, but it keeps the scroll pane from widening itself
		scrollPane.setMaximumSize(new Dimension(imageLabel.getPreferredSize().width, Integer.MAX_VALUE));
		
		// this keeps the scroll pane scrolled to the top as text is added
		DefaultCaret caret = (DefaultCaret) infoTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		
		infoTextArea.append("Weapon M version: " + Build.getVersion() + "\n");
		infoTextArea.append("Build timestamp: " + Build.getTimestamp() + "\n");
		infoTextArea.append("Java vendor: " + System.getProperty("java.vendor") + "\n");
		infoTextArea.append("Java version: " + System.getProperty("java.version") + "\n");
		infoTextArea.append("Runtime name: " + System.getProperty("java.runtime.name") + "\n");
		infoTextArea.append("Runtime version: " + System.getProperty("java.runtime.version") + "\n");
		infoTextArea.append("VM name: " + System.getProperty("java.vm.name") + "\n");
		infoTextArea.append("VM version: " + System.getProperty("java.vm.version") + "\n");
		infoTextArea.append("OS name: " + System.getProperty("os.name") + "\n");
		infoTextArea.append("OS version: " + System.getProperty("os.version") + "\n");
		infoTextArea.append("OS architecture: " + System.getProperty("os.arch") + "\n");
		infoTextArea.append("Available processors: " + Runtime.getRuntime().availableProcessors());
		infoTextArea.setEditable(false);
		
		I18n.setText(closeButton, "BUTTON_CLOSE");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		I18n.setText(copyButton, "BUTTON_COPY");
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StringSelection text = new StringSelection(infoTextArea.getText());
				getToolkit().getSystemClipboard().setContents(text, text);
			}
		});

		// layout shizzle
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
												.addGroup(
														GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		copyButton)
																.addPreferredGap(
																		LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		closeButton))
												.addGroup(
														layout.createParallelGroup(
																GroupLayout.Alignment.TRAILING,
																false)
																.addComponent(
																		scrollPane,
																		GroupLayout.Alignment.LEADING)
																.addComponent(
																		copyrightPanel,
																		GroupLayout.Alignment.LEADING,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(
																		imageLabel,
																		GroupLayout.Alignment.LEADING,
																		GroupLayout.DEFAULT_SIZE,
																		GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(
				GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(imageLabel)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(copyrightPanel,
										GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(scrollPane,
										GroupLayout.PREFERRED_SIZE, 75,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.BASELINE)
												.addComponent(closeButton)
												.addComponent(copyButton))
								.addContainerGap(GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		getRootPane().setDefaultButton(closeButton);
		pack();
	}
}
