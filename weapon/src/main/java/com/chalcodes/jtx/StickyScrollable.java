package com.chalcodes.jtx;

import javax.swing.Scrollable;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Allows a component to provide its preferred view position to a
 * {@link StickyScrollPane}.
 * 
 * @author Kevin Krumwiede (kjkrum@gmail.com)
 */
public interface StickyScrollable extends Scrollable {
	/**
	 * The {@link StickyViewportLayout} obtains the component's preferred size
	 * and passes it back to this method.  This value reflects the size that
	 * will be assigned to the component.
	 * 
	 * @param currentViewport the current viewport in component coordinates
	 * @param preferredSize the component's preferred size
	 * @return the preferred view position
	 */
	public Point getPreferredViewportPosition(Rectangle currentViewport, Rectangle preferredSize);
}
