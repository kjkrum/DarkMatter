package com.chalcodes.jtx;

import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.ViewportLayout;
import java.awt.*;

/**
 * The brains of a {@link StickyScrollPane}.  This class needs to account for
 * the component's preferred scroll position in the middle of the superclass
 * {@link #layoutContainer(Container)}, so I had to completely reimplement
 * that method instead of just calling it.
 *
 * @author <a href="mailto:kjkrum@gmail.com">Kevin Krumwiede</a>
 */
public class StickyViewportLayout extends ViewportLayout {
	private static final long serialVersionUID = -4574299896415565272L;

	@Override
	public void layoutContainer(Container parent) {
	    final JViewport viewport = (JViewport) parent;
	    final Component component = viewport.getView();

	    if(component == null) {
	    	return;
	    }

	    final Scrollable scrollable = (component instanceof Scrollable) ? (Scrollable) component : null;
	    final StickyScrollable sticky = (component instanceof StickyScrollable) ? (StickyScrollable) component : null;
	    
	    final Dimension preferredSize = component.getPreferredSize();
		final Dimension viewportSize = viewport.getSize();	    
	    
	    // respect tracks viewport width/height
		if(scrollable != null) {
			if(scrollable.getScrollableTracksViewportWidth()) {
				preferredSize.width = viewportSize.width;
			}
			if(scrollable.getScrollableTracksViewportHeight()) {
				preferredSize.height = viewportSize.height;
			}
		}
		
		final Point viewPosition = viewport.getViewPosition();
	    
		// respect preferred view position
		if(sticky != null) {
			viewPosition.setLocation(sticky.getPreferredViewportPosition(
					new Rectangle(viewPosition, viewportSize),
					new Rectangle(preferredSize)));
		}
		
		// don't really understand how logical viewport size differs from actual viewport size
		// copied this from an example superclass implementation
		final Dimension logicalViewportSize = viewport.toViewCoordinates(viewport.getSize());
		
		// only allow empty space on the right and bottom if the view is smaller than the viewport
		if (scrollable == null || viewport.getParent() == null || viewport.getParent().getComponentOrientation().isLeftToRight()) {
			if ((viewPosition.x + logicalViewportSize.width) > preferredSize.width) {
				viewPosition.x = Math.max(0, preferredSize.width - logicalViewportSize.width);
			}
		} else {
			if (logicalViewportSize.width > preferredSize.width) {
				viewPosition.x = preferredSize.width - logicalViewportSize.width;
			} else {
				viewPosition.x = Math.max(0, Math.min(preferredSize.width - logicalViewportSize.width, viewPosition.x));
			}
		}
	    if ((viewPosition.y + logicalViewportSize.height) > preferredSize.height) {
	        viewPosition.y = Math.max(0, preferredSize.height - logicalViewportSize.height);
	    }
	    
	    // resize non-scrollable components if they are smaller than the viewport
	    if (scrollable == null) {
	        if ((viewPosition.x == 0) && (viewportSize.width > preferredSize.width)) {
	            preferredSize.width = viewportSize.width;
	        }
	        if ((viewPosition.y == 0) && (viewportSize.height > preferredSize.height)) {
	            preferredSize.height = viewportSize.height;
	        }
	    }
	    
	    viewport.setViewSize(preferredSize);
	    viewport.setViewPosition(viewPosition);
		
	}
}
