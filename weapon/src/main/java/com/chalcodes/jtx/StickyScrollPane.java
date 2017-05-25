package com.chalcodes.jtx;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.awt.Rectangle;

/**
 * A scroll pane that respects the desired scroll offset of client views that
 * implement {@link StickyScrollable}.  This allows components to do things
 * like keep themselves scrolled to the bottom as their size changes.
 * <p>
 * The conventional way of doing this is by queueing a {@link Runnable} to
 * call {@link JComponent#scrollRectToVisible(Rectangle)}.  But this approach
 * often causes the scroll bars to be painted at the original viewport
 * position before being painted again at the new position, resulting in
 * "bouncing" scroll bar knobs.  This class adjusts the viewport position
 * while laying out the scroll pane, so the scroll bars are painted only once
 * and the knobs do not bounce.
 * 
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 * @see StickyViewportLayout
 */
public class StickyScrollPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	public StickyScrollPane() {
		super();
		replaceViewportLayout();
	}

	public StickyScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		super(view, vsbPolicy, hsbPolicy);
		replaceViewportLayout();
	}

	public StickyScrollPane(Component view) {
		super(view);
		replaceViewportLayout();
	}

	public StickyScrollPane(int vsbPolicy, int hsbPolicy) {
		super(vsbPolicy, hsbPolicy);
		replaceViewportLayout();
	}

	protected void replaceViewportLayout() {
		viewport.setLayout(new StickyViewportLayout());
	}
}
