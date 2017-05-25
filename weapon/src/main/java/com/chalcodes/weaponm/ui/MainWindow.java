package com.chalcodes.weaponm.ui;

import com.chalcodes.jtx.*;
import org.neo4j.ogm.session.SessionFactory;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Random;

/**
 * TODO javadoc
 *
 * @author Kevin Krumwiede
 */
public class MainWindow {
	private final JFrame mFrame;

	public MainWindow(@Nonnull final SessionFactory sessionFactory) {
		mFrame = UiFactory.newJFrame();
		mFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mFrame.setExtendedState(mFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		mFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				confirmExit();
			}
		});
		mFrame.setJMenuBar(new JMenuBar());

		try {
			final Buffer buffer = new ScrollingBuffer(1000, 80);
			final SoftFont font = new VgaSoftFont();
			final Display display = new Display(buffer, font, 25, 80);
			final StickyScrollPane scroll = new StickyScrollPane(display);
			scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			mFrame.add(scroll);
			mFrame.pack();

			new Timer(1000, new ActionListener() {
				private final String message = "All work and no play makes Jack a dull boy.";
				private final int[] encoded = new int[message.length()];
				private final Random random = new Random();
				private int row = 0;

				@Override
				public void actionPerformed(final ActionEvent e) {
					final int color = random.nextInt(7) + 1;
					for(int i = 0; i < message.length(); ++i) {
						encoded[i] = VgaBufferElement.getValue(message.charAt(i), color, VgaColors.BLACK,
								false, false, false, false);
					}
					buffer.write(row++, 0, encoded, 0, encoded.length);
				}
			}).start();

		} catch(IOException e) {
			e.printStackTrace(); // TODO
		}
	}

	private void confirmExit() {
		if(JOptionPane.showConfirmDialog(mFrame,
				"Exit Weapon M?",
				"Confirm Exit",
				JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			System.exit(0);
		}
	}

	public void show() {
		mFrame.setVisible(true);
	}
}
